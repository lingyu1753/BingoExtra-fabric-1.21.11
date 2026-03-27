package com.bingoextra.core.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.bingoextra.BingoExtra.MOD_ID;
import static com.bingoextra.BingoExtra.LOGGER;

public class ModDataComponents {
    public static final DataComponentType<Integer> COMPASS_STATE = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<Integer> FOUND_X = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<Integer> FOUND_Z = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<Integer> SEARCH_RADIUS = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<Integer> SAMPLES = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<Boolean> DISPLAY_COORDS = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> SHOW_POS = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<List<BlockPos>> PREV_POS = DataComponentType.<List<BlockPos>>builder().persistent(BlockPos.CODEC.listOf().xmap(ArrayList::new, list -> list)).networkSynchronized(ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC)).build();

    public static final DataComponentType<String> BIOME_ID = DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<String> STRUCTURE_ID = DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<Boolean> IS_GROUP = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();

    public static final DataComponentType<Boolean> AUTO_SMELTING = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> CHAINING_LOG_BREAKABLE = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Integer> MINING_DEVELOPED = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<List<String>> EXPLORED_BIOMES = DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8)).build();
    public static final DataComponentType<String> TEAM = DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<Boolean> HEAD_HUNTER = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<String> WEATHER_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<CompoundTag> STORED_ENTITY = DataComponentType.<CompoundTag>builder()
                                                                                        .persistent(CompoundTag.CODEC)
                                                                                        .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)  // 修改这里
                                                                                        .build();

    public static <T> void register(DataComponentType<T> type, String name) {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, name), type);
    }

    public static void init() {
        register(COMPASS_STATE, "compass_state");
        register(FOUND_X, "found_x");
        register(FOUND_Z, "found_z");
        register(SEARCH_RADIUS, "search_radius");
        register(SAMPLES, "samples");
        register(DISPLAY_COORDS, "display_coords");
        register(SHOW_POS, "show_pos");
        register(PREV_POS, "prev_pos");

        register(BIOME_ID, "biome_id");
        register(STRUCTURE_ID, "structure_id");
        register(IS_GROUP, "is_group");

        register(AUTO_SMELTING, "auto_smelting");
        register(CHAINING_LOG_BREAKABLE, "chaining_log_breakable");
        register(MINING_DEVELOPED, "mining_developed");
        register(EXPLORED_BIOMES, "explored_biomes");
        register(TEAM, "team");
        register(HEAD_HUNTER, "head_hunter");
        register(WEATHER_TYPE, "weather_type");
        register(STORED_ENTITY, "stored_entity");

        LOGGER.info("core.component.ModDataComponents init");
    }
}
