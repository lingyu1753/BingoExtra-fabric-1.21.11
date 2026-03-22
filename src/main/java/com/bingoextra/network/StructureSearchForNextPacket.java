package com.bingoextra.network;

import com.bingoextra.BingoExtra;
import com.bingoextra.item.ModItems;
import com.bingoextra.item.StructureCompassItem;
import com.bingoextra.utils.StructureCompassItemUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public record StructureSearchForNextPacket() implements CustomPacketPayload {

	public static final Type<StructureSearchForNextPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "structure_search_for_next"));

	public static final StreamCodec<FriendlyByteBuf, StructureSearchForNextPacket> CODEC = StreamCodec.ofMember(StructureSearchForNextPacket::write, StructureSearchForNextPacket::read);

	public static StructureSearchForNextPacket read(FriendlyByteBuf buf) {
		return new StructureSearchForNextPacket();
	}

	public void write(FriendlyByteBuf buf) {
	}

	public static void handle(StructureSearchForNextPacket packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			final ItemStack stack = StructureCompassItemUtils.getHeldItem(context.player(), ModItems.STRUCTURE_COMPASS);
			if (!stack.isEmpty()) {
				final StructureCompassItem explorersCompass = (StructureCompassItem) stack.getItem();
				explorersCompass.searchForNextStructure((ServerLevel) context.player().level(), context.player(), context.player().blockPosition(), stack);
			}
		});
	}

	@Override
	public Type<StructureSearchForNextPacket> type() {
		return TYPE;
	}

}
