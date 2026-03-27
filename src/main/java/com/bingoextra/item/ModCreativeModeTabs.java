package com.bingoextra.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.CreativeModeTab.*;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bingoextra.BingoExtra.MOD_ID;
import static com.bingoextra.BingoExtra.LOGGER;
import static net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB;

public class ModCreativeModeTabs {

    public static ItemStack acceptEnchantedItem(ItemDisplayParameters parameters, Item item, Map<ResourceKey<Enchantment>, Integer> enchantments) {
        ItemStack stack = new ItemStack(item);
        parameters.holders().lookup(Registries.ENCHANTMENT).ifPresent(registryLookup -> {
            enchantments.forEach((enchantmentKey, level) -> registryLookup.get(enchantmentKey).ifPresent(enchant -> stack.enchant(enchant, level)));
        });
        return stack;
    }

    public static final ResourceKey<CreativeModeTab> BINGOEXTRA = createKey("bingoextra");

    private static void registerCreativeModeTabs() {
        Registry.register(
                CREATIVE_MODE_TAB,
                BINGOEXTRA,
                CreativeModeTab.builder(null, -1)
                               .title(Component.translatable("itemGroup.bingoextra.bingo_extra"))
                               .icon(() -> new ItemStack(ModItems.WORLD_PEARL))
                               .displayItems(
                                       (itemDisplayParameters, output) -> {
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.SMELTING_PICKAXE, Map.of(Enchantments.EFFICIENCY, 1)));
                                           output.accept(ModItems.BREAKING_AXE);
                                           output.accept(ModItems.MINING_PICKAXE);
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.LOOTING_SWORD, Map.of(Enchantments.SMITE, 3, Enchantments.BANE_OF_ARTHROPODS, 3, Enchantments.LOOTING, 4)));
                                           output.accept(ModItems.WORLD_PEARL);
                                           output.accept(ModItems.SNEAKERS);
                                           output.accept(ModItems.BIOME_COMPASS);
                                           output.accept(ModItems.STRUCTURE_COMPASS);
                                           output.accept(ModItems.COPPER_COMPASS);
                                           output.accept(ModItems.BARBECUE);
                                           output.accept(ModItems.ECHO_SWORD);
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_ARTEMIS, Map.of(Enchantments.PROJECTILE_PROTECTION, 1)));
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_EFFICIENCY, Map.of(Enchantments.EFFICIENCY, 1)));
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_POWER, Map.of(Enchantments.POWER, 1)));
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_PROTECTION, Map.of(Enchantments.PROTECTION, 1)));
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_SHARPNESS, Map.of(Enchantments.SHARPNESS, 1)));
                                           output.accept(acceptEnchantedItem(itemDisplayParameters, ModItems.BOOK_OF_WISDOM, Map.ofEntries(
                                                   Map.entry(Enchantments.PROTECTION, 4),
                                                   Map.entry(Enchantments.QUICK_CHARGE, 2),
                                                   Map.entry(Enchantments.PIERCING, 3),
                                                   Map.entry(Enchantments.PUNCH, 1),
                                                   Map.entry(Enchantments.SHARPNESS, 3),
                                                   Map.entry(Enchantments.FROST_WALKER, 2),
                                                   Map.entry(Enchantments.MULTISHOT, 1),
                                                   Map.entry(Enchantments.POWER, 2),
                                                   Map.entry(Enchantments.FIRE_ASPECT, 1),
                                                   Map.entry(Enchantments.LUCK_OF_THE_SEA, 3),
                                                   Map.entry(Enchantments.LURE, 3))));
                                           ItemStack minerPotion = new ItemStack(ModItems.MINER_POTION);
                                           minerPotion.set(DataComponents.POTION_CONTENTS, new PotionContents(
                                                   Optional.empty(),
                                                   Optional.of(123456),
                                                   List.of(new MobEffectInstance(MobEffects.HASTE, 6000, 1)),
                                                   Optional.empty()
                                           ));
                                           output.accept(minerPotion);
                                           output.accept(ModItems.THUNDER_ORDER);
                                           output.accept(ModItems.RAIN_ORDER);
                                           output.accept(ModItems.SUN_ORDER);
                                           output.accept(ModItems.OXIDANT);
                                           output.accept(ModItems.SOUL_VAULT);
                                           output.accept(ModItems.BOUNTY_ORDER);
                                       })
                               .build());
    }

    private static void addToVanillaCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS)
                       .register((output) -> {

                       });
    }

    private static ResourceKey<CreativeModeTab> createKey(String string) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, string));
    }

    public static void init() {
        addToVanillaCreativeTabs();// 1 添加至原版创造模式物品栏
        registerCreativeModeTabs();// 2 注册创造模式物品栏
        LOGGER.info("item.ModCreativeModeTabs init");
    }
}