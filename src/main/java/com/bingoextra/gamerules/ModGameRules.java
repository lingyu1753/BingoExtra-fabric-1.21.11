package com.bingoextra.gamerules;


import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;

import java.util.function.ToIntFunction;

import static com.bingoextra.BingoExtra.LOGGER;

public class ModGameRules {
    public static final GameRule<Integer> MAX_EXPLORER_LAYERS = registerInteger("bingoextra:max_explorer_layers", GameRuleCategory.PLAYER, 15, 0, 255);
    public static final GameRule<Integer> MINING_DEVELOPED = registerInteger("bingoextra:mining_developed", GameRuleCategory.PLAYER, 30, 0, 32767);
    public static final GameRule<Integer> MINING_LEVEL = registerInteger("bingoextra:mining_level", GameRuleCategory.PLAYER, 8, 0, 255);
    public static final GameRule<Integer> HEAD_HUNTER_PROBABILITY = registerInteger("bingoextra:head_hunter_probability", GameRuleCategory.PLAYER, 200, 0, 1000);
    public static final GameRule<Integer> SPAWN_PHANTOM_DELAY = registerInteger("bingoextra:spawn_phantom_delay", GameRuleCategory.PLAYER, 24000, 0, Integer.MAX_VALUE);
    public static final GameRule<Integer> MAX_BOUNTY_ORDER_RADIUS = registerInteger("bingoextra:max_bounty_order_radius", GameRuleCategory.PLAYER, 200, 0, 1000);
    public static final GameRule<Integer> MAX_BOUNTY_ORDER_TIME = registerInteger("bingoextra:max_bounty_order_time", GameRuleCategory.PLAYER, 3600, 1, 72000);

    private static GameRule<Boolean> registerBoolean(String string, GameRuleCategory gameRuleCategory, boolean bl) {
        return register(
                string,
                gameRuleCategory,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                Codec.BOOL,
                bl,
                FeatureFlagSet.of(),
                GameRuleTypeVisitor::visitBoolean,
                boolean_ -> boolean_ ? 1 : 0
                       );
    }

    private static GameRule<Integer> registerInteger(String string, GameRuleCategory gameRuleCategory, int i, int j) {
        return registerInteger(string, gameRuleCategory, i, j, Integer.MAX_VALUE, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerInteger(String string, GameRuleCategory gameRuleCategory, int defaultValue, int minValue, int maxValue) {
        return registerInteger(string, gameRuleCategory, defaultValue, minValue, maxValue, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerInteger(String string, GameRuleCategory gameRuleCategory, int i, int j, int k, FeatureFlagSet featureFlagSet) {
        return register(
                string,
                gameRuleCategory,
                GameRuleType.INT,
                IntegerArgumentType.integer(j, k),
                Codec.intRange(j, k),
                i,
                featureFlagSet,
                GameRuleTypeVisitor::visitInteger,
                integer -> integer
                       );
    }

    private static <T> GameRule<T> register(
            String string,
            GameRuleCategory gameRuleCategory,
            GameRuleType gameRuleType,
            ArgumentType<T> argumentType,
            Codec<T> codec,
            T object,
            FeatureFlagSet featureFlagSet,
            GameRules.VisitorCaller<T> visitorCaller,
            ToIntFunction<T> toIntFunction
	) {
		return Registry.register(
                BuiltInRegistries.GAME_RULE,
                string,
                new GameRule<>(gameRuleCategory, gameRuleType, argumentType, visitorCaller, codec, toIntFunction, object, featureFlagSet)
                                );
	}

    public static void init() {
        LOGGER.info("command.ModGameRule init");
    }
}