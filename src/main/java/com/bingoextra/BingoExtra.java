package com.bingoextra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bingoextra.callback.block.DyeTransformer;
import com.bingoextra.commands.ModCommands;
import com.bingoextra.config.BiomeCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.event.TickServerEvent;
import com.bingoextra.gamerules.ModGameRules;
import com.bingoextra.item.ModCreativeModeTabs;
import com.bingoextra.item.ModItems;
import com.bingoextra.item.handler.*;
import com.bingoextra.network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;

public class BingoExtra implements ModInitializer {

	public static final String MOD_ID = "bingoextra";

	public static final Logger LOGGER = LogManager.getLogger("BingoExtra");

	public static boolean synced;
	public static boolean canTeleport;
	public static int maxNextSearches;

	public static boolean infiniteXp;
	public static List<Identifier> allowedBiomes;
	public static Map<Identifier, Integer> xpLevelsForAllowedBiomes;
	public static ListMultimap<Identifier, Identifier> dimensionsForAllowedBiomes;

	public static List<Identifier> allowedStructures;
	public static Map<Identifier, Integer> xpLevelsForAllowedStructures;
	public static ListMultimap<Identifier, Identifier> dimensionsForAllowedStructures;
	public static Map<Identifier, Identifier> structureIdsToGroupIds;

	@Override
	public void onInitialize() {
		BiomeCompassConfig.load();

		ModGameRules.init();
		ModItems.init();
		ModCreativeModeTabs.init();
		ModDataComponents.init();
		ModCommands.init();
		TickServerEvent.init();

		AutoSmeltingHandler.init();
		ChainingBreakLogHandler.init();
		ExplorerHandler.init();
		HeadHunterHandler.init();
		MiningDevelopedHandler.init();
		DyeTransformer.EVENT.register(new DyeTransformer());

		initBiomeCompass();
		initStructureCompass();
	}

	public void initBiomeCompass() {
		PayloadTypeRegistry.playC2S().register(BiomeSearchPacket.TYPE, BiomeSearchPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(BiomeSearchForNextPacket.TYPE, BiomeSearchForNextPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(BiomeTeleportPacket.TYPE, BiomeTeleportPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(BiomeSyncPacket.TYPE, BiomeSyncPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(BiomeSearchPacket.TYPE, BiomeSearchPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(BiomeSearchForNextPacket.TYPE, BiomeSearchForNextPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(BiomeTeleportPacket.TYPE, BiomeTeleportPacket::apply);

		allowedBiomes = new ArrayList<Identifier>();
		xpLevelsForAllowedBiomes = new HashMap<Identifier, Integer>();
		dimensionsForAllowedBiomes = ArrayListMultimap.create();
	}

	public void initStructureCompass() {
		PayloadTypeRegistry.playC2S().register(StructureSearchPacket.TYPE, StructureSearchPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(StructureSearchForNextPacket.TYPE, StructureSearchForNextPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(StructureTeleportPacket.TYPE, StructureTeleportPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(StructureSyncPacket.TYPE, StructureSyncPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(StructureSearchPacket.TYPE, StructureSearchPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(StructureSearchForNextPacket.TYPE, StructureSearchForNextPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(StructureTeleportPacket.TYPE, StructureTeleportPacket::handle);

		allowedStructures = new ArrayList<Identifier>();
		xpLevelsForAllowedStructures = new HashMap<Identifier, Integer>();
		dimensionsForAllowedStructures = ArrayListMultimap.create();
		structureIdsToGroupIds = new HashMap<Identifier, Identifier>();
	}
}
