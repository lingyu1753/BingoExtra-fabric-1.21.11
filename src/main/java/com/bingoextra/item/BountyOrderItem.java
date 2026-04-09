package com.bingoextra.item;

import com.bingoextra.gamerules.ModGameRules;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.List;

public class BountyOrderItem extends Item {
    private static final List<EntityType<? extends Monster>> RAID_MOBS = List.of(
            EntityType.EVOKER,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.WITCH,
            EntityType.VEX
                                                                                );

    public BountyOrderItem(Properties properties) {
        super(properties.stacksTo(1)
                        .rarity(Rarity.UNCOMMON)
                        .useCooldown(20.0F)
             );
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            player.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
        } else {
            ServerLevel serverLevel = (ServerLevel) level;
            int radius = serverLevel.getGameRules().get(ModGameRules.MAX_BOUNTY_ORDER_RADIUS);
            int time = serverLevel.getGameRules().get(ModGameRules.MAX_BOUNTY_ORDER_TIME);

            List<Entity> entities = serverLevel.getEntitiesOfClass(
                    Entity.class,
                    player.getBoundingBox().inflate(radius),
                    entity -> RAID_MOBS.contains(entity.getType())
                                                                  );

            for (Entity entity : entities) {
                if (entity instanceof Monster mob) {
                    if (mob.hasEffect(MobEffects.GLOWING)) mob.removeEffect(MobEffects.GLOWING);
                    mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, time, 0, false, false, false));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}