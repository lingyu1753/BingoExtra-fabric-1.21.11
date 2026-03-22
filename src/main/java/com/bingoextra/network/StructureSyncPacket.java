package com.bingoextra.network;

import com.bingoextra.BingoExtra;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record StructureSyncPacket(boolean canTeleport, int maxNextSearches, boolean infiniteXp, List<Identifier> allowedStructureIds, Map<Identifier, Integer> xpLevelsForAllowedStructures, ListMultimap<Identifier, Identifier> dimensionsForAllowedStructures, Map<Identifier, Identifier> structureIdsToGroupIds) implements CustomPacketPayload {

	public static final Type<StructureSyncPacket> TYPE = new Type<StructureSyncPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "structure_sync"));

	public static final StreamCodec<FriendlyByteBuf, StructureSyncPacket> CODEC = StreamCodec.ofMember(StructureSyncPacket::write, StructureSyncPacket::read);

	public static StructureSyncPacket read(FriendlyByteBuf buf) {
		final boolean canTeleport = buf.readBoolean();
		final int maxNextSearches = buf.readInt();
		final boolean infiniteXp = buf.readBoolean();
		final List<Identifier> allowedStructures = new ArrayList<Identifier>();
		final Map<Identifier, Integer> xpLevelsForAllowedStructures = new HashMap<Identifier, Integer>();
		final ListMultimap<Identifier, Identifier> dimensionsForAllowedStructures = ArrayListMultimap.create();
		final Map<Identifier, Identifier> structureIdsToGroupIds = new HashMap<Identifier, Identifier>();

		final int numStructures = buf.readInt();
		for (int i = 0; i < numStructures; i++) {
			final Identifier structureId = buf.readIdentifier();
			final int numDimensions = buf.readInt();
			final List<Identifier> dimensions = new ArrayList<Identifier>();
			for (int j = 0; j < numDimensions; j++) {
				dimensions.add(buf.readIdentifier());
			}
			final Identifier groupId = buf.readIdentifier();
			final int xpLevels = buf.readInt();
			if (structureId != null) {
				allowedStructures.add(structureId);
				dimensionsForAllowedStructures.putAll(structureId, dimensions);
				structureIdsToGroupIds.put(structureId, groupId);
				xpLevelsForAllowedStructures.put(structureId, xpLevels);
			}
		}

		return new StructureSyncPacket(canTeleport, maxNextSearches, infiniteXp, allowedStructures, xpLevelsForAllowedStructures, dimensionsForAllowedStructures, structureIdsToGroupIds);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(canTeleport);
		buf.writeInt(maxNextSearches);
		buf.writeBoolean(infiniteXp);
		buf.writeInt(allowedStructureIds.size());
		for (Identifier structureId : allowedStructureIds) {
			buf.writeIdentifier(structureId);
			List<Identifier> dimensions = dimensionsForAllowedStructures.get(structureId);
			buf.writeInt(dimensions.size());
			for (Identifier dimensionKey : dimensions) {
				buf.writeIdentifier(dimensionKey);
			}
			Identifier typeKey = structureIdsToGroupIds.get(structureId);
			buf.writeIdentifier(typeKey);
			int xpLevels = xpLevelsForAllowedStructures.get(structureId);
			buf.writeInt(xpLevels);
		}
	}

	public static void handle(StructureSyncPacket packet, ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
            BingoExtra.synced = true;
            BingoExtra.canTeleport = packet.canTeleport;
            BingoExtra.maxNextSearches = packet.maxNextSearches;
            BingoExtra.infiniteXp = packet.infiniteXp;
            BingoExtra.allowedStructures = packet.allowedStructureIds;
            BingoExtra.xpLevelsForAllowedStructures = packet.xpLevelsForAllowedStructures;
            BingoExtra.dimensionsForAllowedStructures = packet.dimensionsForAllowedStructures;
            BingoExtra.structureIdsToGroupIds = packet.structureIdsToGroupIds;
		});
	}

	@Override
	public Type<StructureSyncPacket> type() {
		return TYPE;
	}

}
