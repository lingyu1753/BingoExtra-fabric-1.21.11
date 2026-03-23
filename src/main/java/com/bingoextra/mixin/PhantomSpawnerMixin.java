package com.bingoextra.mixin;

import com.bingoextra.gamerules.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin implements CustomSpawner {
    @Shadow
    private int nextTick;

    /**
     * @author BingoExtra
     * @reason Change phantom spawn delay from 72000 to 24000 ticks
     */
    @Overwrite
    public void tick(ServerLevel serverLevel, boolean bl) {
        if (bl) {
            if (serverLevel.getGameRules().get(GameRules.SPAWN_PHANTOMS)) {
                RandomSource randomSource = serverLevel.random;
                nextTick--;

                if (nextTick <= 0) {
                    nextTick = nextTick + (60 + randomSource.nextInt(60)) * 20;

                    if (serverLevel.getSkyDarken() >= 5 || !serverLevel.dimensionType().hasSkyLight()) {
                        for (ServerPlayer serverPlayer : serverLevel.players()) {
                            if (!serverPlayer.isSpectator()) {

                                BlockPos blockPos = serverPlayer.blockPosition();

                                if (!serverLevel.dimensionType().hasSkyLight()
                                    || blockPos.getY() >= serverLevel.getSeaLevel()
                                       && serverLevel.canSeeSky(blockPos)) {

                                    DifficultyInstance difficultyInstance =
                                            serverLevel.getCurrentDifficultyAt(blockPos);

                                    if (difficultyInstance.isHarderThan(randomSource.nextFloat() * 3.0F)) {

                                        ServerStatsCounter stats = serverPlayer.getStats();

                                        int i = Mth.clamp(
                                                stats.getValue(
                                                        Stats.CUSTOM.get(Stats.TIME_SINCE_REST)
                                                              ),
                                                1,
                                                Integer.MAX_VALUE
                                                         );
                                        if (randomSource.nextInt(i) >= serverLevel.getGameRules().get(ModGameRules.SPAWN_PHANTOM_DELAY)) {
                                            BlockPos spawnPos =
                                                    blockPos.above(20 + randomSource.nextInt(15))
                                                            .east(-10 + randomSource.nextInt(21))
                                                            .south(-10 + randomSource.nextInt(21));

                                            BlockState blockState =
                                                    serverLevel.getBlockState(spawnPos);
                                            FluidState fluidState =
                                                    serverLevel.getFluidState(spawnPos);

                                            if (NaturalSpawner.isValidEmptySpawnBlock(
                                                    serverLevel,
                                                    spawnPos,
                                                    blockState,
                                                    fluidState,
                                                    EntityType.PHANTOM
                                                                                     )) {

                                                SpawnGroupData spawnGroupData = null;

                                                int count =
                                                        1 + randomSource.nextInt(
                                                                difficultyInstance.getDifficulty().getId() + 1
                                                                                );

                                                for (int l = 0; l < count; l++) {

                                                    Phantom phantom =
                                                            EntityType.PHANTOM.create(
                                                                    serverLevel,
                                                                    EntitySpawnReason.NATURAL
                                                                                     );

                                                    if (phantom != null) {
                                                        phantom.snapTo(spawnPos, 0.0F, 0.0F);

                                                        spawnGroupData =
                                                                phantom.finalizeSpawn(
                                                                        serverLevel,
                                                                        difficultyInstance,
                                                                        EntitySpawnReason.NATURAL,
                                                                        spawnGroupData
                                                                                     );

                                                        serverLevel.addFreshEntityWithPassengers(phantom);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}