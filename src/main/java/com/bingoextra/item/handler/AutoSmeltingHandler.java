package com.bingoextra.item.handler;


import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.item.ModItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.bingoextra.BingoExtra.LOGGER;


public class AutoSmeltingHandler {
    private static final Random random = new Random();

    public static void registerSmeltingPickaxeHandler() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {

            if (world.isClientSide()) return true;

            ItemStack mainHandItem = player.getMainHandItem();

            if (Boolean.TRUE.equals(mainHandItem.get(ModDataComponents.AUTO_SMELTING))) {
                return handleSmeltingPickaxeMining(world, player, pos, state, blockEntity);
            }

            return true;
        });
    }

    private static final Map<Item, Item> SMELTING_MAP = Map.ofEntries(
            Map.entry(Items.RAW_COPPER_BLOCK, Items.COPPER_BLOCK),
            Map.entry(Items.RAW_IRON_BLOCK, Items.IRON_BLOCK),
            Map.entry(Items.RAW_GOLD_BLOCK, Items.GOLD_BLOCK),
            Map.entry(Items.RAW_COPPER, Items.COPPER_INGOT),
            Map.entry(Items.RAW_IRON, Items.IRON_INGOT),
            Map.entry(Items.RAW_GOLD, Items.GOLD_INGOT),
            Map.entry(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP),
            Map.entry(Items.GILDED_BLACKSTONE, Items.GOLD_INGOT)
                                                                     );
    private static final Map<Block, Integer> EXP_MAP = Map.ofEntries(
            Map.entry(Blocks.COPPER_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_COAL_ORE, 1),
            Map.entry(Blocks.RAW_COPPER_BLOCK, 9),
            Map.entry(Blocks.IRON_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_IRON_ORE, 1),
            Map.entry(Blocks.RAW_IRON_BLOCK, 9),
            Map.entry(Blocks.GOLD_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_GOLD_ORE, 1),
            Map.entry(Blocks.RAW_GOLD_BLOCK, 9),
            Map.entry(Blocks.REDSTONE_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, 1),
            Map.entry(Blocks.DIAMOND_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, 1),
            Map.entry(Blocks.EMERALD_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, 1),
            Map.entry(Blocks.LAPIS_ORE, 1),
            Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, 1),
            Map.entry(Blocks.NETHER_GOLD_ORE, 1),
            Map.entry(Blocks.NETHER_QUARTZ_ORE, 1),
            Map.entry(Blocks.ANCIENT_DEBRIS, 2),
            Map.entry(Blocks.GILDED_BLACKSTONE, 1)
                                                                    );

    private static boolean handleSmeltingPickaxeMining(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world instanceof ServerLevel serverLevel) {
            ItemStack tool = player.getMainHandItem();

            world.removeBlock(pos, false);

            boolean requiresCorrectTool = state.requiresCorrectToolForDrops();
            boolean isToolSuitable = tool.isCorrectToolForDrops(state);
            boolean canDrop = !requiresCorrectTool || isToolSuitable;

            if (canDrop) {
                serverLevel.sendParticles(
                        ParticleTypes.SMALL_FLAME,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        20,
                        0.1, 0.1, 0.1,
                        0.001
                                         );

                Optional<ResourceKey<LootTable>> lootTableId = state.getBlock().getLootTable();
                if (lootTableId.isPresent()) {
                    LootParams.Builder paramsBuilder = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                            .withParameter(LootContextParams.BLOCK_STATE, state);
                    LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableId.get());
                    List<ItemStack> drops = lootTable.getRandomItems(paramsBuilder.create(LootContextParamSets.BLOCK));
                    for (ItemStack drop : drops) {
                        if (SMELTING_MAP.containsKey(drop.getItem())) drop = SMELTING_MAP.get(drop.getItem()).getDefaultInstance();
                        if (drop.getItem() == Items.GOLD_NUGGET) drop = new ItemStack(Items.GOLD_INGOT, 1);
                        else if (drop.getItem() == Items.COPPER_INGOT) drop = new ItemStack(Items.COPPER_INGOT, random.nextInt(2, 6));
                        if (!player.getInventory().add(drop)) player.drop(drop, false);
                    }
                }

                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), EXP_MAP.getOrDefault(state.getBlock(), 0));
            }

            boolean unbreakable = tool.getComponents().has(DataComponents.UNBREAKABLE);
            if (!unbreakable) tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
        return false;
    }

    public static void init() {
        registerSmeltingPickaxeHandler();
        LOGGER.info("item.handler.AutoSmeltingHandler init");
    }
}