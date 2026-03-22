package com.bingoextra.event;

import com.bingoextra.core.component.ModDataComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.mojang.text2speech.Narrator.LOGGER;

public class TickServerEvent {
    public static void registerPosDisplayEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (Player player : server.getPlayerList().getPlayers()) {
                ItemStack stack = player.getMainHandItem();
                if (player.getTags().contains("POS_DISPLAY")) {
                    BlockPos pos = player.getOnPos();

                    float yaw = player.getYRot();
                    String direction = getDirectionFromYaw(yaw);
                    String text = "";
                    if (Boolean.TRUE.equals(stack.get(ModDataComponents.SHOW_POS))) {
                        Integer targetX = stack.get(ModDataComponents.FOUND_X);
                        targetX = targetX == null ? 0 : targetX;
                        Integer targetZ = stack.get(ModDataComponents.FOUND_Z);
                        targetZ = targetZ == null ? 0 : targetZ;
                        int distance = Math.abs(targetX - pos.getX()) + Math.abs(targetZ - pos.getZ());
                        text = String.format("坐标: §a%d §e%d §b%d §c%s §f目标: §a%d §e~ §b%d §f距离: §6%d", pos.getX(), pos.getY(), pos.getZ(), direction, targetX, targetZ, distance);
                    } else text = String.format("坐标: §a%d §e%d §b%d §c%s", pos.getX(), pos.getY(), pos.getZ(), direction);
                    player.displayClientMessage(Component.literal(text), true);
                }
                else if (Boolean.TRUE.equals(stack.get(ModDataComponents.SHOW_POS))) {
                    BlockPos pos = player.getOnPos();
                    Integer targetX = stack.get(ModDataComponents.FOUND_X);
                    targetX = targetX == null ? 0 : targetX;
                    Integer targetZ = stack.get(ModDataComponents.FOUND_Z);
                    targetZ = targetZ == null ? 0 : targetZ;
                    int distance = Math.abs(targetX - pos.getX()) + Math.abs(targetZ - pos.getZ());
                    String text = String.format("目标: §a%d §e~ §b%d §f距离: §6%d", targetX, targetZ, distance);
                    player.displayClientMessage(Component.literal(text), true);
                }
            }
        });
    }

    /**
     * 根据玩家 yaw 角度返回中文方向（八个主方向）
     *
     * @param yaw 玩家水平旋转角度（-180 到 180）
     * @return 方向字符串，如 "北"、"东北"、"东" 等
     */
    private static String getDirectionFromYaw(float yaw) {
        // 将 yaw 标准化到 0~360 范围
        yaw = (yaw % 360 + 360) % 360;

        // 根据区间判断方向（每个方向覆盖 45°）
        if (yaw >= 337.5 || yaw < 22.5) {
            return "南";
        } else if (yaw >= 22.5 && yaw < 67.5) {
            return "西南";
        } else if (yaw >= 67.5 && yaw < 112.5) {
            return "西";
        } else if (yaw >= 112.5 && yaw < 157.5) {
            return "西北";
        } else if (yaw >= 157.5 && yaw < 202.5) {
            return "北";
        } else if (yaw >= 202.5 && yaw < 247.5) {
            return "东北";
        } else if (yaw >= 247.5 && yaw < 292.5) {
            return "东";
        } else {
            return "东南"; // 292.5 ~ 337.5
        }
    }

    public static void init() {
        registerPosDisplayEvent();
        LOGGER.info("event.TickServerEvent init");
    }
}
