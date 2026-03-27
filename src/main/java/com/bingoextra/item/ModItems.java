package com.bingoextra.item;

import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.item.equipment.ModArmorMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.bingoextra.BingoExtra.LOGGER;
import static com.bingoextra.BingoExtra.MOD_ID;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;

public class ModItems {
    public static final Item SMELTING_PICKAXE = registerItem("smelting_pickaxe", new Item.Properties()
                                                                     .pickaxe(ToolMaterial.IRON, 1.0F, -2.8F)
                                                                     .rarity(Rarity.RARE)
                                                                     .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                                     .component(ModDataComponents.AUTO_SMELTING, true)
                                                                     .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.smelting_pickaxe"))))
                                                            );
    public static final Item BREAKING_AXE = registerItem("breaking_axe", properties -> new AxeItem(ToolMaterial.IRON, 6.0F, -3.1F, properties
            .rarity(Rarity.RARE)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            .component(ModDataComponents.CHAINING_LOG_BREAKABLE, true)
            .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.breaking_axe"))))
    ));

    public static final Item MINING_PICKAXE = registerItem("mining_pickaxe", new Item.Properties()
                                                                   .pickaxe(ToolMaterial.DIAMOND, 1.0F, -2.8F)
                                                                   .rarity(Rarity.EPIC)
                                                                   .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                                                   .component(ModDataComponents.MINING_DEVELOPED, 0)
                                                                   .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.mining_pickaxe.1"), Component.translatable("lore.bingoextra.mining_pickaxe.2"))))
                                                          );
    public static final Item LOOTING_SWORD = registerItem("looting_sword", new Item.Properties().sword(ToolMaterial.IRON, 3.0F, -2.4F)
                                                                                                .rarity(Rarity.RARE)
                                                                                                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                                                                .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.looting_sword"))))
                                                         );
    public static final Item WORLD_PEARL = registerItem("world_pearl", WorldPearlItem::new, new Item.Properties().component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.world_pearl.1"), Component.translatable("lore.bingoextra.world_pearl.2"))))
                                                       );
    public static final Item SNEAKERS = registerItem("sneakers", new Item.Properties().humanoidArmor(ModArmorMaterials.SNEAKERS, ArmorType.BOOTS)
                                                                                      .rarity(Rarity.RARE)
                                                                                      .component(ModDataComponents.EXPLORED_BIOMES, List.of())
                                                                                      .component(DataComponents.LORE, new ItemLore(List.of(
                                                                                              Component.translatable("lore.bingoextra.sneakers")
                                                                                                                                          ))));
    public static final Item COPPER_COMPASS = registerItem("copper_compass", CompassItem::new, new Item.Properties()
                                                               .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.copper_compass"))))
                                                      );
    public static final Item BARBECUE = registerItem("barbecue", new Item.Properties().food(Foods.COOKED_PORKCHOP));
    public static final Item ECHO_SWORD = registerItem("echo_sword", new Item.Properties().sword(ToolMaterial.DIAMOND, 3.0F, -2.4F)
                                                                                          .rarity(Rarity.EPIC)
                                                                                          .component(ModDataComponents.HEAD_HUNTER, true)
                                                                                          .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.echo_sword")) ))
                                                      );
    public static final Item BIOME_COMPASS = registerItem("biome_compass", BiomeCompassItem::new);
    public static final Item STRUCTURE_COMPASS = registerItem("structure_compass", StructureCompassItem::new);
    public static final Item BOOK_OF_ARTEMIS = registerItem(
            "book_of_artemis",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item BOOK_OF_EFFICIENCY = registerItem(
            "book_of_efficiency",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item BOOK_OF_POWER = registerItem(
            "book_of_power",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item BOOK_OF_PROTECTION = registerItem(
            "book_of_protection",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item BOOK_OF_SHARPNESS = registerItem(
            "book_of_sharpness",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item BOOK_OF_WISDOM = registerItem(
            "book_of_wisdom",
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                                                          );
    public static final Item MINER_POTION = registerItem(
            "miner_potion",
            PotionItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
                    .usingConvertsTo(GLASS_BOTTLE)
                                                  );
    public static final Item THUNDER_ORDER = registerItem("thunder_order", WeatherOrderItem::new, new Item.Properties()
                                                                  .component(ModDataComponents.WEATHER_TYPE, WeatherOrderItem.THUNDER)
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.thunder_order")) ))
                                                         );
    public static final Item RAIN_ORDER = registerItem("rain_order", WeatherOrderItem::new, new Item.Properties()
                                                                  .component(ModDataComponents.WEATHER_TYPE, WeatherOrderItem.RAIN)
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.rain_order")) ))
                                                         );
    public static final Item SUN_ORDER = registerItem("sun_order", WeatherOrderItem::new, new Item.Properties()
                                                                  .component(ModDataComponents.WEATHER_TYPE, WeatherOrderItem.CLEAR)
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.sun_order")) ))
                                                         );
    public static final Item OXIDANT = registerItem("oxidant", OxidantItem::new, new Item.Properties()
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.oxidant")) ))
                                                         );
    public static final Item SOUL_VAULT = registerItem("soul_vault", SoulVaultItem::new, new Item.Properties()
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.soul_vault")) ))
                                                         );
    public static final Item BOUNTY_ORDER = registerItem("bounty_order", BountyOrderItem::new, new Item.Properties()
                                                                  .component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.bingoextra.bounty_order")) ))
                                                         );



    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }

    private static ResourceKey<Item> itemId(String string) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, string));
    }

    private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> resourceKey) {
        return ResourceKey.create(Registries.ITEM, resourceKey.identifier());
    }

    private static Item registerSpawnEgg(EntityType<?> entityType) {
        return registerItem(
                ResourceKey.create(Registries.ITEM, EntityType.getKey(entityType).withSuffix("_spawn_egg")), SpawnEggItem::new, new Item.Properties().spawnEgg(entityType)
                           );
    }

    public static Item registerBlock(Block block) {
        return registerBlock(block, BlockItem::new);
    }

    public static Item registerBlock(Block block, Item.Properties properties) {
        return registerBlock(block, BlockItem::new, properties);
    }

    public static Item registerBlock(Block block, UnaryOperator<Item.Properties> unaryOperator) {
        return registerBlock(
                block, (BiFunction<Block, Item.Properties, Item>) ((blockx, properties) -> new BlockItem(blockx, (Item.Properties) unaryOperator.apply(properties)))
                            );
    }

    public static Item registerBlock(Block block, Block... blocks) {
        Item item = registerBlock(block);

        for (Block block2 : blocks) {
            Item.BY_BLOCK.put(block2, item);
        }

        return item;
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction) {
        return registerBlock(block, biFunction, new Item.Properties());
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction, Item.Properties properties) {
        return registerItem(
                blockIdToItemId(block.builtInRegistryHolder().key()), propertiesx -> (Item) biFunction.apply(block, propertiesx), properties.useBlockDescriptionPrefix()
                           );
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function) {
        return registerItem(itemId(string), function, new Item.Properties());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function, Item.Properties properties) {
        return registerItem(itemId(string), function, properties);
    }

    public static Item registerItem(String string, Item.Properties properties) {
        return registerItem(itemId(string), Item::new, properties);
    }

    public static Item registerItem(String string) {
        return registerItem(itemId(string), Item::new, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function) {
        return registerItem(resourceKey, function, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = (Item) function.apply(properties.setId(resourceKey));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static void init() {
        LOGGER.info("item.ModItems init");
    }
}
