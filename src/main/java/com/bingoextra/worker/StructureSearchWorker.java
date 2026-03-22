package com.bingoextra.worker;

import com.bingoextra.BingoExtra;
import com.bingoextra.config.StructureCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.item.ModItems;
import com.bingoextra.item.StructureCompassItem;
import com.bingoextra.utils.StructureUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.List;

public abstract class StructureSearchWorker<T extends StructurePlacement> implements StructureCompassWorldWorkerManager.IWorker {
	
	protected String managerId;
	protected ServerLevel level;
	protected Player player;
	protected ItemStack stack;
	protected BlockPos startPos;
	protected BlockPos currentPos;
	protected T placement;
	protected List<Structure> structureSet;
	protected Identifier structureOrGroupId;
	protected boolean isGroup;
	protected int samples;
	protected boolean finished;
	protected int lastRadiusThreshold;
	protected List<BlockPos> prevPos;

	public StructureSearchWorker(ServerLevel level, Player player, ItemStack stack, BlockPos startPos, List<BlockPos> prevPos, T placement, List<Structure> structureSet, Identifier structureOrGroupId, boolean isGroup, String managerId) {
		this.level = level;
		this.player = player;
		this.stack = stack;
		this.startPos = startPos;
		this.prevPos = prevPos;
		this.structureSet = structureSet;
		this.structureOrGroupId = structureOrGroupId;
		this.isGroup = isGroup;
		this.placement = placement;
		this.managerId = managerId;

		currentPos = startPos;
		samples = 0;

		finished = !level.getServer().getWorldData().worldGenOptions().generateStructures();
	}

	public void start() {
		if (!stack.isEmpty() && stack.getItem() == ModItems.STRUCTURE_COMPASS) {
			if (StructureCompassConfig.maxRadius > 0) {
				BingoExtra.LOGGER.info("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " starting with " + (shouldLogRadius() ? StructureCompassConfig.maxRadius + " max radius, " : "") + StructureCompassConfig.maxSamples + " max samples, " + prevPos.size() + " previous locations");
				StructureCompassWorldWorkerManager.addWorker(this);
			} else {
				fail();
			}
		}
	}

	@Override
	public boolean hasWork() {
		return !finished && prevPos.size() <= StructureCompassConfig.maxNextSearches && getRadius() < StructureCompassConfig.maxRadius && samples < StructureCompassConfig.maxSamples;
	}

	@Override
	public boolean doWork() {
		int radius = getRadius();
		if (radius > 250 && radius / 250 > lastRadiusThreshold) {
			if (!stack.isEmpty() && stack.getItem() == ModItems.STRUCTURE_COMPASS) {
				stack.set(ModDataComponents.SEARCH_RADIUS, roundRadius(radius, 250));
			}
			lastRadiusThreshold = radius / 250;
		}
		return false;
	}

	protected Pair<BlockPos, Structure> getStructureGeneratingAt(ChunkPos chunkPos) {
		for (Structure structure : structureSet) {
			StructureCheckResult result = level.structureManager().checkStructurePresence(chunkPos, structure, placement, false);
			if (result != StructureCheckResult.START_NOT_PRESENT) {
				if (result == StructureCheckResult.START_PRESENT) {
					return Pair.of(placement.getLocatePos(chunkPos), structure);
				}

				ChunkAccess chunkAccess = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
				StructureStart structureStart = level.structureManager().getStartForStructure(SectionPos.bottomOf(chunkAccess), structure, chunkAccess);
				if (structureStart != null && structureStart.isValid()) {
					return Pair.of(placement.getLocatePos(structureStart.getChunkPos()), structure);
				}
			}
		}

		return null;
	}

	protected void succeed(BlockPos pos, Structure structure) {
		BingoExtra.LOGGER.info("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " succeeded with " + (shouldLogRadius() ? getRadius() + " radius, " : "") + samples + " samples");
		if (!stack.isEmpty() && stack.getItem() == ModItems.STRUCTURE_COMPASS) {
			((StructureCompassItem) stack.getItem()).succeed(stack, StructureUtils.getIdForStructure(level, structure), isGroup, pos.getX(), pos.getZ(), prevPos, samples, StructureCompassConfig.displayCoordinates);
		} else {
			BingoExtra.LOGGER.error("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " found invalid compass after successful search");
		}
		finished = true;
	}

	public boolean shouldIgnore(BlockPos pos) {
		return prevPos.contains(pos);
	}

	protected void fail() {
		BingoExtra.LOGGER.info("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " failed with " + (shouldLogRadius() ? getRadius() + " radius, " : "") + samples + " samples");
		if (!stack.isEmpty() && stack.getItem() == ModItems.STRUCTURE_COMPASS) {
			((StructureCompassItem) stack.getItem()).fail(stack, structureOrGroupId, roundRadius(getRadius(), 250), samples);
		} else {
			BingoExtra.LOGGER.error("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " found invalid compass after failed search");
		}
		finished = true;
	}

	public void stop() {
		BingoExtra.LOGGER.info("StructureCompassSearchWorkerManager " + managerId + ": " + getName() + " stopped with " + (shouldLogRadius() ? getRadius() + " radius, " : "") + samples + " samples");
		finished = true;
	}

	protected int getRadius() {
		return StructureUtils.getHorizontalDistanceToLocation(startPos, currentPos.getX(), currentPos.getZ());
	}

	protected int roundRadius(int radius, int roundTo) {
		return ((int) radius / roundTo) * roundTo;
	}
	
	protected abstract String getName();
	
	protected abstract boolean shouldLogRadius();

}