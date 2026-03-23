package com.bingoextra.item;

import com.bingoextra.core.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class WeatherOrderItem extends Item {
    public static final String RAIN = "rain";
    public static final String THUNDER = "thunder";
    public static final String CLEAR = "clear";

    public WeatherOrderItem(Properties properties) {
        super(properties.stacksTo(1)
                        .useCooldown(60.0F)
                        .rarity(Rarity.COMMON)
             );
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        String weatherType = itemStack.get(ModDataComponents.WEATHER_TYPE);

        if (weatherType == null) {
            player.displayClientMessage(Component.translatable("info.bingoextra.weather_order.invalid"), true);
            return InteractionResult.FAIL;
        }

        itemStack.consume(1, player);

        ServerLevel serverLevel = (ServerLevel) level;

        switch (weatherType) {
            case CLEAR -> serverLevel.setWeatherParameters(0, 6000, false, false);
            case RAIN -> serverLevel.setWeatherParameters(0, 6000, true, false);
            case THUNDER -> serverLevel.setWeatherParameters(0, 6000, true, true);
            default -> {
                return InteractionResult.FAIL;
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.0F, 2.0F);

        player.level().getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("info.bingoextra.weather_order.success", player.getName()),
                false
                                                                 );

        return InteractionResult.SUCCESS;
    }
}
