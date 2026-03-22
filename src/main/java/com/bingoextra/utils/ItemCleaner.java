package com.bingoextra.utils;

import com.bingoextra.core.component.ModDataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;

public class ItemCleaner {
    /**
     * 彻底移除指定玩家身上及周围所有带有 TEAM 组件的物品
     * 包括：主背包、盔甲、副手、鼠标指针、掉落物
     */
    public static void removeAllMarkedItems(ServerPlayer player) {
        // 同步当前容器状态（如打开界面）
        player.containerMenu.broadcastChanges();

        // 清除鼠标指针
        player.containerMenu.setCarried(ItemStack.EMPTY);
        player.connection.send(new ClientboundSetCursorItemPacket(ItemStack.EMPTY));

        // 清除玩家背包中的所有 TEAM 物品
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.has(ModDataComponents.TEAM)) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }

        // 清除副手
        ItemStack offhandStack = player.getItemBySlot(EquipmentSlot.OFFHAND);
        if (offhandStack.has(ModDataComponents.TEAM)) {
            player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }

        // 同步玩家背包和盔甲槽位的变化
        player.containerMenu.broadcastChanges();

        // 清除掉落物（半径 100 格内）
        ServerLevel level = player.level();
        level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(100),
                                 itemEntity -> itemEntity.getItem().has(ModDataComponents.TEAM))
             .forEach(ItemEntity::discard);
    }
}