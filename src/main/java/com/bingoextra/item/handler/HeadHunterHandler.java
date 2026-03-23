package com.bingoextra.item.handler;

import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.gamerules.ModGameRules;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;

public class HeadHunterHandler {

    private static final Map<EntityType<?>, ItemStack> HEAD_DROPS = Map.ofEntries(
            Map.entry(EntityType.WITHER_SKELETON, new ItemStack(Items.WITHER_SKELETON_SKULL))
                                                                                 );

    public static void init() {
        LOGGER.info("item.handler.HeadHunterHandler init");
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (damageSource.getEntity() instanceof Player player) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.has(ModDataComponents.HEAD_HUNTER)) {
                    ItemStack headStack = HEAD_DROPS.get(entity.getType());
                    if (headStack != null && !headStack.isEmpty()) {
                        int probability = player.level().getServer().overworld().getGameRules().get(ModGameRules.HEAD_HUNTER_PROBABILITY);
                        if (entity.level().random.nextDouble() < (double) probability / 1000) {
                            ItemEntity itemEntity = new ItemEntity(
                                    entity.level(),
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    headStack.copy()
                            );
                            entity.level().addFreshEntity(itemEntity);
                        }
                    }
                }
            }
        });
    }
}