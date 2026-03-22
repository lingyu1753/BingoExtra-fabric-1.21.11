package com.bingoextra.item;

import com.bingoextra.config.StructureCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.gui.StructureCompassGuiWrapper;
import com.bingoextra.network.StructureSyncPacket;
import com.bingoextra.utils.*;
import com.bingoextra.worker.StructureCompassSearchWorkerManager;
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
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureCompassItem extends Item {
    private StructureCompassSearchWorkerManager workerManager;

    public StructureCompassItem(Properties properties) {
        super(properties.stacksTo(1)
                        .component(ModDataComponents.SHOW_POS, false)
                        .rarity(Rarity.RARE));
        workerManager = new StructureCompassSearchWorkerManager();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            final ItemStack stack = StructureCompassItemUtils.getHeldItem(player, ModItems.STRUCTURE_COMPASS);
            if (Boolean.FALSE.equals(stack.get(ModDataComponents.SHOW_POS))) {
                stack.set(ModDataComponents.SHOW_POS, true);
                player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 1.0F, 1.5F);
            } else {
                stack.set(ModDataComponents.SHOW_POS, false);
                player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 1.0F, 0.5F);
            }
        } else {
            if (level.isClientSide()) {
                final ItemStack stack = StructureCompassItemUtils.getHeldItem(player, ModItems.STRUCTURE_COMPASS);
                if (!stack.has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) || Boolean.FALSE.equals(stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)))
                    StructureCompassGuiWrapper.openGUI(level, player, stack);
            } else {
                final ServerLevel serverLevel = (ServerLevel) level;
                final ServerPlayer serverPlayer = (ServerPlayer) player;
                final boolean canTeleport = StructureCompassConfig.allowTeleport && StructureCompassPlayerUtils.canTeleport(serverLevel.getServer(), player);
                final int maxNextSearches = StructureCompassConfig.maxNextSearches;
                final boolean hasInfiniteXp = player.hasInfiniteMaterials();
                final List<Identifier> allowedStructureIds = StructureUtils.getAllowedStructureIds(serverLevel);
                final Map<Identifier, Integer> xpLevels = StructureUtils.getXpLevelsForAllowedStructures(serverLevel, allowedStructureIds);
                final ListMultimap<Identifier, Identifier> generatingDimensions = StructureUtils.getGeneratingDimensionIdsForAllowedStructures(serverLevel, allowedStructureIds);
                ServerPlayNetworking.send(serverPlayer, new StructureSyncPacket(canTeleport, maxNextSearches, hasInfiniteXp, allowedStructureIds, xpLevels, generatingDimensions, StructureUtils.structureIdsToGroupIds(serverLevel)));
            }
        }
        return InteractionResult.CONSUME;
    }

    public void searchForStructure(ServerLevel level, Player player, BlockPos pos, Identifier structureOrGroupId, boolean isGroup, ItemStack stack) {
        search(stack, structureOrGroupId, isGroup);

        List<Identifier> structureIds = List.of(structureOrGroupId);
        if (isGroup) {
            structureIds = StructureUtils.getStructuresForGroup(level, structureOrGroupId);
        }

        List<Structure> structures = new ArrayList<Structure>();
        for (Identifier key : structureIds) {
            structures.add(StructureUtils.getStructureForId(level, key));
        }
        List<BlockPos> prevPos = new ArrayList<BlockPos>();
        workerManager.stop();
        workerManager.createWorkers(level, player, stack, structures, structureOrGroupId, isGroup, pos, prevPos);
        boolean started = workerManager.start();
        if (!started) {
            fail(stack, structureOrGroupId, 0, 0);
        }

        int xpLevels = StructureUtils.getXpLevelsForStructure(level, structureOrGroupId);
        if (!player.hasInfiniteMaterials() && xpLevels > 0) {
            player.giveExperienceLevels(-xpLevels);
        }
    }

    public void searchForNextStructure(ServerLevel level, Player player, BlockPos pos, ItemStack stack) {
        String structureIdStr = stack.getOrDefault(ModDataComponents.STRUCTURE_ID, null);
        List<BlockPos> prevPos = stack.getOrDefault(ModDataComponents.PREV_POS, null);
        boolean isGroup = stack.getOrDefault(ModDataComponents.IS_GROUP, false);
        if (structureIdStr != null && prevPos != null) {
            Identifier structureId = Identifier.parse(structureIdStr);

            List<Identifier> structureIds;
            if (isGroup) {
                // The compass will always store the ID of the specific structure that was found, even if the
                // search itself was for a group, so we need to re-determine the group ID
                Identifier groupId = StructureUtils.structureIdsToGroupIds(level).get(structureId);
                search(stack, groupId, isGroup);
                structureIds = StructureUtils.getStructuresForGroup(level, groupId);
            } else {
                search(stack, structureId, isGroup);
                structureIds = List.of(structureId);
            }

            List<Structure> structures = new ArrayList<Structure>();
            for (Identifier key : structureIds) {
                structures.add(StructureUtils.getStructureForId(level, key));
            }
            workerManager.stop();
            workerManager.createWorkers(level, player, stack, structures, structureId, isGroup, pos, prevPos);
            boolean started = workerManager.start();
            if (!started) {
                fail(stack, structureId, 0, 0);
            }

            int xpLevels = StructureUtils.getXpLevelsForStructure(level, structureId);
            if (!player.hasInfiniteMaterials() && xpLevels > 0) {
                player.giveExperienceLevels(-xpLevels);
            }
        }
    }

    public void succeed(ItemStack stack, Identifier structureID, boolean isGroup, int x, int z, List<BlockPos> prevPos, int samples, boolean displayCoordinates) {
        clearCompassData(stack);
        setCompassState(stack, StructureCompassState.FOUND);
        stack.set(ModDataComponents.STRUCTURE_ID, structureID.toString());
        stack.set(ModDataComponents.IS_GROUP, isGroup);
        stack.set(ModDataComponents.FOUND_X, x);
        stack.set(ModDataComponents.FOUND_Z, z);
        stack.set(ModDataComponents.PREV_POS, prevPos);
        stack.set(ModDataComponents.SAMPLES, samples);
        stack.set(ModDataComponents.DISPLAY_COORDS, displayCoordinates);
        workerManager.clear();

        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        String biomeStr = stack.get(ModDataComponents.STRUCTURE_ID);
        biomeStr = "structure.minecraft." + biomeStr.substring(biomeStr.indexOf(":") + 1);
        stack.set(DataComponents.ITEM_NAME, Component.translatable(biomeStr).append(" ").append(Component.translatable("item.bingoextra.biome_compass")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal(stack.get(ModDataComponents.FOUND_X) + " ~ " + stack.get(ModDataComponents.FOUND_Z)).withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false)));
        stack.set(DataComponents.LORE, new ItemLore(lore));
    }

    public void fail(ItemStack stack, Identifier structureId, int radius, int samples) {
        workerManager.pop();
        boolean started = workerManager.start();
        if (!started) {
            clearCompassData(stack);
            setCompassState(stack, StructureCompassState.NOT_FOUND);
            stack.set(ModDataComponents.STRUCTURE_ID, structureId.toString());
            stack.set(ModDataComponents.SEARCH_RADIUS, radius);
            stack.set(ModDataComponents.SAMPLES, samples);
        }
    }

    public void search(ItemStack stack, Identifier structureId, boolean isGroup) {
        clearCompassData(stack);
        stack.set(ModDataComponents.COMPASS_STATE, StructureCompassState.SEARCHING.getID());
        stack.set(ModDataComponents.STRUCTURE_ID, structureId.toString());
        stack.set(ModDataComponents.IS_GROUP, isGroup);
        stack.set(ModDataComponents.SEARCH_RADIUS, 0);
        stack.set(ModDataComponents.SAMPLES, 0);
    }

    public void setCompassState(ItemStack stack, StructureCompassState state) {
        stack.set(ModDataComponents.COMPASS_STATE, state.getID());
    }

    public StructureCompassState getCompassState(ItemStack stack) {
        return StructureCompassState.fromID(stack.getOrDefault(ModDataComponents.COMPASS_STATE, StructureCompassState.NOT_FOUND.getID()));
    }

    private void clearCompassData(ItemStack stack) {
        stack.remove(ModDataComponents.COMPASS_STATE);
        stack.remove(ModDataComponents.STRUCTURE_ID);
        stack.remove(ModDataComponents.FOUND_X);
        stack.remove(ModDataComponents.FOUND_Z);
        stack.remove(ModDataComponents.PREV_POS);
        stack.remove(ModDataComponents.IS_GROUP);
        stack.remove(ModDataComponents.DISPLAY_COORDS);
        stack.remove(ModDataComponents.SEARCH_RADIUS);
        stack.remove(ModDataComponents.SAMPLES);
    }
}
