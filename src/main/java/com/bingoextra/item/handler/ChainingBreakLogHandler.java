package com.bingoextra.item.handler;


import com.bingoextra.core.component.ModDataComponents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;


public class ChainingBreakLogHandler {

    private static final int MAX_CHAIN_SAFETY = 500;

    private static Set<Block> getLogBlocks(Level level) {
        Set<Block> logs = new HashSet<>();
        TagKey<Block> logTag = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("logs"));
        Optional<HolderSet.Named<Block>> optional = level.registryAccess().lookupOrThrow(Registries.BLOCK).get(logTag);
        if (optional.isPresent()) {
            Set<Block> blocks = new HashSet<>();
            for (Holder<Block> entry : optional.get()) blocks.add(entry.value());
            logs = Set.copyOf(blocks);
        }
        return logs;
    }

    private static boolean isLogBlock(Level level, BlockState state) {
        return getLogBlocks(level).contains(state.getBlock());
    }

    /**
     * 启动连锁砍树
     */
    private static void startTreeFelling(ServerLevel world, Player player, BlockPos startPos, ItemStack tool) {
        boolean unbreakable = tool.getComponents().has(DataComponents.UNBREAKABLE);

        if (!unbreakable && tool.getDamageValue() >= tool.getMaxDamage()) return;

        Queue<FellingTask> taskQueue = new LinkedList<>();
        Set<BlockPos> processed = new HashSet<>();
        int processedCount = 0;
        taskQueue.offer(new FellingTask(startPos, 0));
        processed.add(startPos);
        processedCount++;
        scheduleNextTask(world, player, tool, taskQueue, processed, processedCount, unbreakable, startPos);
    }

    /**
     * 调度下一个连锁任务
     */
    private static void scheduleNextTask(ServerLevel level, Player player, ItemStack tool,
                                         Queue<FellingTask> taskQueue, Set<BlockPos> processed,
                                         int processedCount, boolean unbreakable, BlockPos startPos) {
        if (taskQueue.isEmpty()) return;

        FellingTask task = taskQueue.poll();
        BlockPos currentPos = task.pos();

        BlockState currentState = level.getBlockState(currentPos);
        if (!isLogBlock(level, currentState)) {
            scheduleNextTask(level, player, tool, taskQueue, processed, processedCount, unbreakable, startPos);
            return;
        }

        // 播放破坏粒子效果（使用当前方块状态）
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, currentState),
                currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5,
                10, 0.2, 0.2, 0.2, 0.1
                           );

        // 播放破坏音效
        if (currentState.getBlock() == Blocks.CHERRY_LOG)
            level.playSound(null, currentPos, SoundEvents.CHERRY_WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
        else if (currentState.getBlock() == Blocks.CRIMSON_STEM || currentState.getBlock() == Blocks.WARPED_STEM)
            level.playSound(null, currentPos, SoundEvents.NETHER_WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
        else level.playSound(null, currentPos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

        BlockEntity blockEntity = level.getBlockEntity(currentPos);
        level.removeBlock(currentPos, false);
        Block.dropResources(currentState, level, currentPos, blockEntity, player, tool);

        if (!unbreakable) {
            int currentDamage = tool.getDamageValue();
            int maxDamage = tool.getMaxDamage();

            if (currentDamage >= maxDamage) return;
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        if (processedCount >= MAX_CHAIN_SAFETY) return;

        Direction[] directions = {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction dir : directions) {
            BlockPos neighborPos = currentPos.relative(dir);

            if (processed.contains(neighborPos)) continue;

            BlockPos offset = neighborPos.subtract(startPos);
            int dx = Math.abs(offset.getX());
            int dz = Math.abs(offset.getZ());
            int dy = offset.getY();

            if (dx > 3 || dz > 3 || dy > 7 || dy < 0) continue;

            BlockState neighborState = level.getBlockState(neighborPos);
            if (!isLogBlock(level, neighborState)) continue;

            processed.add(neighborPos);
            processedCount++;

            taskQueue.offer(new FellingTask(neighborPos, task.delay() + 4));

            if (processedCount >= MAX_CHAIN_SAFETY) {
                break;
            }
        }

        if (!taskQueue.isEmpty()) {
            FellingTask nextTask = taskQueue.peek();
            int nextDelay = nextTask.delay() - task.delay();
            int finalProcessedCount = processedCount;

            Util.backgroundExecutor().execute(() -> {
                try {
                    Thread.sleep(nextDelay * 50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                level.getServer().execute(() ->
                                                  scheduleNextTask(level, player, tool, taskQueue, processed, finalProcessedCount, unbreakable, startPos)
                                         );
            });
        }
    }

    /**
     * 连锁任务记录类
     */
    private record FellingTask(BlockPos pos, int delay) {
    }

    public static void init() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) return true;

            ItemStack tool = player.getMainHandItem();

            if (Boolean.TRUE.equals(tool.get(ModDataComponents.CHAINING_LOG_BREAKABLE)) && isLogBlock(level, state)) {
                startTreeFelling((ServerLevel) level, player, pos, tool);
                return false;
            }
            return true;
        });
        LOGGER.info("item.handler.ChainingBreakLogHandler init");
    }
}
