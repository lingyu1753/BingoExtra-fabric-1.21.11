package com.bingoextra.network;

import com.bingoextra.utils.BiomeItemUtils;
import com.bingoextra.BingoExtra;
import com.bingoextra.item.BiomeCompassItem;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public record BiomeSearchPacket(Identifier biomeId, BlockPos pos) implements CustomPacketPayload {
	
	public static final Type<BiomeSearchPacket> TYPE = new Type<BiomeSearchPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "biome_search"));
	
	public static final StreamCodec<FriendlyByteBuf, BiomeSearchPacket> CODEC = StreamCodec.ofMember(BiomeSearchPacket::write, BiomeSearchPacket::read);
	
	public static BiomeSearchPacket read(FriendlyByteBuf buf) {
		return new BiomeSearchPacket(buf.readIdentifier(), buf.readBlockPos());
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeIdentifier(biomeId);
		buf.writeBlockPos(pos);
	}
	
	@Override
	public Type<BiomeSearchPacket> type() {
		return TYPE;
	}

    public static void apply(BiomeSearchPacket packet, ServerPlayNetworking.Context context) {
    	context.server().execute(() -> {
	    	final ItemStack stack = BiomeItemUtils.getHeldNatureCompass(context.player());
			if (!stack.isEmpty()) {
				final BiomeCompassItem natureCompass = (BiomeCompassItem) stack.getItem();
				final ServerLevel level = context.player().level();
				natureCompass.searchForBiome(level, context.player(), packet.biomeId(), packet.pos(), stack);
			}
		});
	}

}