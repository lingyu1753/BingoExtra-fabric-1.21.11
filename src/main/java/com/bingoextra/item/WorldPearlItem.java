package com.bingoextra.item;

import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;

import java.util.Set;

public class WorldPearlItem extends Item {
    private static final int RADIUS = 200;
    private static final int MAX_ATTEMPTS = 20;

    public WorldPearlItem(Properties properties) {
        super(properties.stacksTo(16)
                        .useCooldown(1.0F)
                        .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        boolean success;

        if (level.dimension() == Level.OVERWORLD) {
            success = teleportRandomInOverworld(serverLevel, serverPlayer);
        } else if (level.dimension() == Level.NETHER || level.dimension() == Level.END) {
            success = teleportToPlayerSpawn(serverLevel, serverPlayer);
        } else {
            return InteractionResult.PASS;
        }

        if (success) {
            itemStack.consume(1, player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    private boolean teleportRandomInOverworld(ServerLevel level, ServerPlayer player) {
        if (player.getY() > 10) {
            player.displayClientMessage(Component.translatable("info.bingoextra.world_pearl.too_high"), true);
            return false;
        }

        int seaLevel = level.getSeaLevel();

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int dx = level.getRandom().nextInt(2 * RADIUS + 1) - RADIUS;
            int dz = level.getRandom().nextInt(2 * RADIUS + 1) - RADIUS;
            int targetX = Mth.floor(player.getX()) + dx;
            int targetZ = Mth.floor(player.getZ()) + dz;
            int maxY = level.getHeight(Heightmap.Types.WORLD_SURFACE, targetX, targetZ) - 1;

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(targetX, seaLevel, targetZ);
            for (int y = seaLevel; y <= maxY; y++) {
                pos.setY(y);
                BlockState state = level.getBlockState(pos);
                BlockState stateAbove = level.getBlockState(pos.above());
                if (state.isAir() && stateAbove.isAir()) {
                    boolean success = player.teleportTo(level, targetX + 0.5, y, targetZ + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);
                    if (success) {
                        return true;
                    }
                }
            }
        }

        player.displayClientMessage(Component.translatable("info.bingoextra.world_pearl.failed"), true);
        return false;
    }

    private boolean teleportToPlayerSpawn(ServerLevel currentLevel, ServerPlayer player) {
        MinecraftServer server = currentLevel.getServer();

        ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();

        ServerLevel targetWorld;
        BlockPos targetPos;

        if (respawnConfig != null) {
            LevelData.RespawnData respawnData = respawnConfig.respawnData();
            BlockPos respawnPos = respawnData.pos();
            ResourceKey<Level> respawnDim = respawnData.dimension();
            targetWorld = server.getLevel(respawnDim);
            if (targetWorld != null) {
                targetPos = respawnPos;
            } else {
                targetWorld = server.overworld();
                targetPos = targetWorld.getLevelData().getRespawnData().pos();
            }
        } else {
            targetWorld = server.overworld();
            targetPos = targetWorld.getLevelData().getRespawnData().pos();
        }

        return player.teleportTo(targetWorld,
                                 targetPos.getX() + 0.5,
                                 targetPos.getY(),
                                 targetPos.getZ() + 0.5,
                                 Set.of(),
                                 player.getYRot(),
                                 player.getXRot(),
                                 true);
    }
}