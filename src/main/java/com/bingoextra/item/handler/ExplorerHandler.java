package com.bingoextra.item.handler;

import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.gamerules.ModGameRules;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bingoextra.BingoExtra.MOD_ID;

public class ExplorerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Identifier MOVEMENT_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath(MOD_ID, "sneaker_movement_speed");
    private static final Identifier STEP_HEIGHT_MODIFIER_ID = Identifier.fromNamespaceAndPath(MOD_ID, "sneaker_step_height");
    private static final Identifier WATER_MOVEMENT_MODIFIER_ID = Identifier.fromNamespaceAndPath(MOD_ID, "sneaker_water_movement");
    private static final Identifier SAFE_FALL_DISTANCE_MODIFIER_ID = Identifier.fromNamespaceAndPath(MOD_ID, "sneaker_safe_fall_distance");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerLevel level = server.overworld();
                int maxLayers = level.getGameRules().get(ModGameRules.MAX_EXPLORER_LAYERS);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack feetStack = player.getItemBySlot(EquipmentSlot.FEET);
                if (feetStack.has(ModDataComponents.EXPLORED_BIOMES)) {
                    updateExploredBiomes(player, feetStack);
                    updateComponents(level, feetStack, maxLayers);
                }
            }
        });
    }

    private static void updateExploredBiomes(ServerPlayer player, ItemStack feetStack) {
        Level world = player.level();
        Holder<Biome> biomeHolder = world.getBiome(player.blockPosition());
        Optional<ResourceKey<Biome>> currentBiomeKey = biomeHolder.unwrapKey();
        if (currentBiomeKey.isEmpty()) return;

        List<String> explored = feetStack.get(ModDataComponents.EXPLORED_BIOMES);
        if (explored == null) explored = new ArrayList<>();
        else explored = new ArrayList<>(explored);

        String currentBiomeStr = currentBiomeKey.get().toString();

        if (!explored.contains(currentBiomeStr)) {
            explored.add(currentBiomeStr);
            feetStack.set(ModDataComponents.EXPLORED_BIOMES, explored);
        }
    }

    private static void updateComponents(ServerLevel level, ItemStack feetStack, int maxLayers) {
        List<String> explored = feetStack.get(ModDataComponents.EXPLORED_BIOMES);
        int count = explored == null ? 0 : Math.min(explored.size(), maxLayers);
        int featherFallingLevel = count / 5;

        var modifiers = ItemAttributeModifiers.builder()
                                              .add(Attributes.MOVEMENT_SPEED,
                                                   new AttributeModifier(MOVEMENT_SPEED_MODIFIER_ID, 0.01 * count, AttributeModifier.Operation.ADD_VALUE),
                                                   EquipmentSlotGroup.FEET)
                                              .add(Attributes.STEP_HEIGHT,
                                                   new AttributeModifier(STEP_HEIGHT_MODIFIER_ID, 0.15 * count, AttributeModifier.Operation.ADD_VALUE),
                                                   EquipmentSlotGroup.FEET)
                                              .add(Attributes.WATER_MOVEMENT_EFFICIENCY,
                                                   new AttributeModifier(WATER_MOVEMENT_MODIFIER_ID, 0.02 * count, AttributeModifier.Operation.ADD_VALUE),
                                                   EquipmentSlotGroup.FEET)
                                              .add(Attributes.SAFE_FALL_DISTANCE,
                                                   new AttributeModifier(SAFE_FALL_DISTANCE_MODIFIER_ID, 0.15 * count, AttributeModifier.Operation.ADD_VALUE),
                                                   EquipmentSlotGroup.FEET)
                                              .build();
        feetStack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);


        if (featherFallingLevel > 0) feetStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemEnchantments stored = feetStack.get(DataComponents.ENCHANTMENTS);
        if (stored == null) stored = ItemEnchantments.EMPTY;
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);
        Holder<Enchantment> holder = level.registryAccess()
                                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                                    .getOrThrow(Enchantments.FEATHER_FALLING);
        mutable.set(holder, featherFallingLevel);
        feetStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        ItemLore currentLore = feetStack.get(DataComponents.LORE);
        if (currentLore != null) {
            List<Component> originalLines = currentLore.lines();
            List<Component> newLines = new ArrayList<>();
            boolean replaced = false;

            for (Component line : originalLines) {
                if (isLayerLine(line)) {
                    newLines.add(createLayerLine(count, maxLayers));
                    replaced = true;
                } else {
                    newLines.add(line);
                }
            }
            if (!replaced) newLines.add(createLayerLine(count, maxLayers));

            feetStack.set(DataComponents.LORE, new ItemLore(newLines));
        }
    }

    private static boolean isLayerLine(Component text) {
        ComponentContents content = text.getContents();
        if (content instanceof TranslatableContents translatable) {
            return "info.bingoextra.layers".equals(translatable.getKey());
        }
        return false;
    }

    private static Component createLayerLine(int count, int maxLayers) {
        return Component.translatable("info.bingoextra.layers")
                        .append(Component.literal(": " + count + " / " + maxLayers)
                                         .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.WHITE).withItalic(false)));
    }

    public static void init() {
        register();
        LOGGER.info("item.handler.ExplorerHandler init");
    }
}