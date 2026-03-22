package com.bingoextra.item;

import java.util.*;
import java.util.List;

import com.bingoextra.config.BiomeCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.gui.BiomeCompassGuiWrapper;
import com.bingoextra.utils.BiomeCompassState;
import com.bingoextra.utils.BiomeItemUtils;
import com.bingoextra.utils.BiomePlayerUtils;
import com.bingoextra.utils.BiomeUtils;
import com.bingoextra.worker.BiomeSearchWorker;
import com.bingoextra.network.BiomeSyncPacket;
import com.google.common.collect.ListMultimap;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;


public class BiomeCompassItem extends Item {
    private BiomeSearchWorker worker;

    public BiomeCompassItem(Item.Properties properties) {
        super(properties.stacksTo(1)
                        .component(ModDataComponents.SHOW_POS, false)
                        .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            final ItemStack stack = BiomeItemUtils.getHeldNatureCompass(player);
            if (Boolean.FALSE.equals(stack.get(ModDataComponents.SHOW_POS))) {
                stack.set(ModDataComponents.SHOW_POS, true);
                player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 1.0F, 1.5F);
            } else {
                stack.set(ModDataComponents.SHOW_POS, false);
                player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 1.0F, 0.5F);
            }
        } else {
            if (level.isClientSide()) {
                final ItemStack stack = BiomeItemUtils.getHeldNatureCompass(player);
                if (!stack.has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) || Boolean.FALSE.equals(stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)))
                    BiomeCompassGuiWrapper.openGUI(level, player, stack);
            } else {
                final ServerLevel serverLevel = (ServerLevel) level;
                final ServerPlayer serverPlayer = (ServerPlayer) player;
                final boolean canTeleport = BiomeCompassConfig.allowTeleport && BiomePlayerUtils.canTeleport(serverPlayer.level().getServer(), player);
                final int maxNextSearches = BiomeCompassConfig.maxNextSearches;
                final boolean hasInfiniteXp = player.hasInfiniteMaterials();
                final List<Identifier> allowedBiomeIds = BiomeUtils.getAllowedBiomes(level);
                final Map<Identifier, Integer> xpLevels = BiomeUtils.getXpLevelsForAllowedBiomes(serverLevel, allowedBiomeIds);
                final ListMultimap<Identifier, Identifier> generatingDimensions = BiomeUtils.getGeneratingDimensionsForAllowedBiomes(serverLevel, allowedBiomeIds);
                ServerPlayNetworking.send(serverPlayer, new BiomeSyncPacket(canTeleport, maxNextSearches, hasInfiniteXp, allowedBiomeIds, xpLevels, generatingDimensions));
            }
        }
        return InteractionResult.CONSUME;
    }

    public void searchForBiome(ServerLevel level, Player player, Identifier biomeId, BlockPos pos, ItemStack stack) {
        Optional<Biome> optionalBiome = BiomeUtils.getBiomeForId(level, biomeId);
        if (optionalBiome.isPresent()) {
            search(stack, biomeId);

            if (worker != null) {
                worker.stop();
            }
            List<BlockPos> prevPos = new ArrayList<BlockPos>();
            worker = new BiomeSearchWorker(level, player, stack, optionalBiome.get(), pos, prevPos);
            worker.start();

            int xpLevels = BiomeUtils.getXpLevelsForBiome(level, biomeId);
            if (!player.hasInfiniteMaterials() && xpLevels > 0) {
                player.giveExperienceLevels(-xpLevels);
            }
        } else {
            fail(stack, 0, 0);
        }
    }

    public void searchForNextBiome(ServerLevel level, Player player, BlockPos pos, ItemStack stack) {
        if (stack.getItem() == ModItems.BIOME_COMPASS) {
            List<BlockPos> prevPos = stack.getOrDefault(ModDataComponents.PREV_POS, null);
            String biomeIdStr = stack.getOrDefault(ModDataComponents.BIOME_ID, null);
            if (prevPos != null && biomeIdStr != null) {
                Identifier biomeId = Identifier.parse(biomeIdStr);
                Optional<Biome> optionalBiome = BiomeUtils.getBiomeForId(level, biomeId);
                if (optionalBiome.isPresent()) {
                    search(stack, biomeId);

                    if (worker != null) {
                        worker.stop();
                    }
                    worker = new BiomeSearchWorker(level, player, stack, optionalBiome.get(), pos, prevPos);
                    worker.start();

                    int xpLevels = BiomeUtils.getXpLevelsForBiome(level, biomeId);
                    if (!player.hasInfiniteMaterials() && xpLevels > 0) {
                        player.giveExperienceLevels(-xpLevels);
                    }
                } else {
                    fail(stack, 0, 0);
                }
            }
        }
    }

    public void setCompassState(ItemStack stack, BiomeCompassState state) {
        stack.set(ModDataComponents.COMPASS_STATE, state.getID());
    }

    public BiomeCompassState getCompassState(ItemStack stack) {
        return BiomeCompassState.fromID(stack.getOrDefault(ModDataComponents.COMPASS_STATE, BiomeCompassState.FOUND.getID()));
    }

    public void succeed(ItemStack stack, Identifier biomeId, int x, int z, List<BlockPos> prevPos, int samples, boolean displayCoordinates) {
        clearCompassData(stack);
        setCompassState(stack, BiomeCompassState.FOUND);
        stack.set(ModDataComponents.BIOME_ID, biomeId.toString());
        stack.set(ModDataComponents.FOUND_X, x);
        stack.set(ModDataComponents.FOUND_Z, z);
        stack.set(ModDataComponents.PREV_POS, prevPos);
        stack.set(ModDataComponents.SAMPLES, samples);
        stack.set(ModDataComponents.DISPLAY_COORDS, displayCoordinates);
        worker = null;

        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        String structureStr = stack.get(ModDataComponents.BIOME_ID);
        structureStr = "biome.minecraft." + structureStr.substring(structureStr.indexOf(":") + 1);
        stack.set(DataComponents.ITEM_NAME, Component.translatable(structureStr).append(" ").append(Component.translatable("item.bingoextra.biome_compass")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal(stack.get(ModDataComponents.FOUND_X) + " ~ " + stack.get(ModDataComponents.FOUND_Z)).withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false)));
        stack.set(DataComponents.LORE, new ItemLore(lore));
    }

    public void fail(ItemStack stack, int radius, int samples) {
        clearCompassData(stack);
        setCompassState(stack, BiomeCompassState.NOT_FOUND);
        stack.set(ModDataComponents.SEARCH_RADIUS, radius);
        stack.set(ModDataComponents.SAMPLES, samples);
        worker = null;
    }

    private void search(ItemStack stack, Identifier biomeId) {
        clearCompassData(stack);
        setCompassState(stack, BiomeCompassState.SEARCHING);
        stack.set(ModDataComponents.BIOME_ID, biomeId.toString());
        stack.set(ModDataComponents.SAMPLES, 0);
        stack.set(ModDataComponents.SEARCH_RADIUS, 0);
    }

    private void clearCompassData(ItemStack stack) {
        stack.remove(ModDataComponents.COMPASS_STATE);
        stack.remove(ModDataComponents.BIOME_ID);
        stack.remove(ModDataComponents.FOUND_X);
        stack.remove(ModDataComponents.FOUND_Z);
        stack.remove(ModDataComponents.PREV_POS);
        stack.remove(ModDataComponents.SAMPLES);
        stack.remove(ModDataComponents.SEARCH_RADIUS);
        stack.remove(ModDataComponents.DISPLAY_COORDS);
    }

}