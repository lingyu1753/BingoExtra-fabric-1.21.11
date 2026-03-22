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

public record StructureSearchPacket(Identifier structureOrGroupId, boolean isGroup) implements CustomPacketPayload {
	
	public static final Type<StructureSearchPacket> TYPE = new Type<StructureSearchPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "structure_search"));
	
	public static final StreamCodec<FriendlyByteBuf, StructureSearchPacket> CODEC = StreamCodec.ofMember(StructureSearchPacket::write, StructureSearchPacket::read);

	public static StructureSearchPacket read(FriendlyByteBuf buf) {
		final Identifier structureOrGroupId = buf.readIdentifier();
		final boolean isGroup = buf.readBoolean();
		return new StructureSearchPacket(structureOrGroupId, isGroup);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeIdentifier(structureOrGroupId);
		buf.writeBoolean(isGroup);
	}

	public static void handle(StructureSearchPacket packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			final ItemStack stack = StructureCompassItemUtils.getHeldItem(context.player(), ModItems.STRUCTURE_COMPASS);
			if (!stack.isEmpty()) {
				final StructureCompassItem explorersCompass = (StructureCompassItem) stack.getItem();
				explorersCompass.searchForStructure((ServerLevel) context.player().level(), context.player(), context.player().blockPosition(), packet.structureOrGroupId, packet.isGroup, stack);
			}
		});
	}
	
	@Override
	public Type<StructureSearchPacket> type() {
		return TYPE;
	}
}