package com.bingoextra.network;

import com.bingoextra.BingoExtra;
import com.bingoextra.item.BiomeCompassItem;
import com.bingoextra.utils.BiomeItemUtils;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public record BiomeSearchForNextPacket() implements CustomPacketPayload {

	public static final Type<BiomeSearchForNextPacket> TYPE = new Type<BiomeSearchForNextPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "biome_search_for_next"));

	public static final StreamCodec<FriendlyByteBuf, BiomeSearchForNextPacket> CODEC = StreamCodec.ofMember(BiomeSearchForNextPacket::write, BiomeSearchForNextPacket::read);

	public static BiomeSearchForNextPacket read(FriendlyByteBuf buf) {
		return new BiomeSearchForNextPacket();
	}

	public void write(FriendlyByteBuf buf) {
	}

	public static void apply(BiomeSearchForNextPacket packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			final ItemStack stack = BiomeItemUtils.getHeldNatureCompass(context.player());
			if (!stack.isEmpty()) {
				final BiomeCompassItem natureCompass = (BiomeCompassItem) stack.getItem();
				final ServerLevel level = context.player().level();
				natureCompass.searchForNextBiome(level, context.player(), context.player().blockPosition(), stack);
			}
		});
	}

	@Override
	public Type<BiomeSearchForNextPacket> type() {
		return TYPE;
	}

}
