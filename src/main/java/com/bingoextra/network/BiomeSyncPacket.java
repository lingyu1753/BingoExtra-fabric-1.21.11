package com.bingoextra.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bingoextra.BingoExtra;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BiomeSyncPacket(boolean canTeleport, int maxNextSearches, boolean infiniteXp, List<Identifier> allowedBiomes, Map<Identifier, Integer> xpLevelsForAllowedBiomes, ListMultimap<Identifier, Identifier> dimensionsForAllowedBiomes) implements CustomPacketPayload {

	public static final Type<BiomeSyncPacket> TYPE = new Type<BiomeSyncPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "biome_sync"));

	public static final StreamCodec<FriendlyByteBuf, BiomeSyncPacket> CODEC = StreamCodec.ofMember(BiomeSyncPacket::write, BiomeSyncPacket::read);

	public static BiomeSyncPacket read(FriendlyByteBuf buf) {
		boolean canTeleport = buf.readBoolean();
		int maxNextSearches = buf.readInt();
		boolean infiniteXp = buf.readBoolean();

		List<Identifier> allowedBiomes = new ArrayList<Identifier>();
		Map<Identifier, Integer> xpLevelsForAllowedBiomes = new HashMap<Identifier, Integer>();
		ListMultimap<Identifier, Identifier> dimensionsForAllowedBiomes = ArrayListMultimap.create();
		int listSize = buf.readInt();
		for (int i = 0; i < listSize; i++) {
			Identifier biomeId = buf.readIdentifier();
			int numDimensions = buf.readInt();
			List<Identifier> dimensionIds = new ArrayList<Identifier>();
			for (int j = 0; j < numDimensions; j++) {
				dimensionIds.add(buf.readIdentifier());
			}

			int xpLevels = buf.readInt();

			if (biomeId != null) {
				allowedBiomes.add(biomeId);
				xpLevelsForAllowedBiomes.put(biomeId, xpLevels);
				dimensionsForAllowedBiomes.putAll(biomeId, dimensionIds);
			}
		}

		return new BiomeSyncPacket(canTeleport, maxNextSearches, infiniteXp, allowedBiomes, xpLevelsForAllowedBiomes, dimensionsForAllowedBiomes);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(canTeleport);
		buf.writeInt(maxNextSearches);
		buf.writeBoolean(infiniteXp);

		buf.writeInt(allowedBiomes.size());
		for (Identifier biomeId : allowedBiomes) {
			buf.writeIdentifier(biomeId);
			List<Identifier> dimensionIds = dimensionsForAllowedBiomes.get(biomeId);
			buf.writeInt(dimensionIds.size());
			for (Identifier dimensionId : dimensionIds) {
				buf.writeIdentifier(dimensionId);
			}
			int xpLevels = xpLevelsForAllowedBiomes.get(biomeId);
			buf.writeInt(xpLevels);
		}
	}

	public static void apply(BiomeSyncPacket packet, ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
			BingoExtra.synced = true;
			BingoExtra.canTeleport = packet.canTeleport;
			BingoExtra.maxNextSearches = packet.maxNextSearches;
			BingoExtra.infiniteXp = packet.infiniteXp;
			BingoExtra.allowedBiomes = packet.allowedBiomes;
			BingoExtra.xpLevelsForAllowedBiomes = packet.xpLevelsForAllowedBiomes;
			BingoExtra.dimensionsForAllowedBiomes = packet.dimensionsForAllowedBiomes;
		});
	}

	@Override
	public Type<BiomeSyncPacket> type() {
		return TYPE;
	}

}
