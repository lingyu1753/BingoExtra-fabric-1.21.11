package com.bingoextra.network;

import com.bingoextra.BingoExtra;
import com.bingoextra.config.StructureCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.item.ModItems;
import com.bingoextra.item.StructureCompassItem;
import com.bingoextra.utils.StructureCompassItemUtils;
import com.bingoextra.utils.StructureCompassPlayerUtils;
import com.bingoextra.utils.StructureCompassState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record StructureTeleportPacket() implements CustomPacketPayload {
	
	public static final Type<StructureTeleportPacket> TYPE = new Type<StructureTeleportPacket>(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "structure_teleport"));
	
	public static final StreamCodec<FriendlyByteBuf, StructureTeleportPacket> CODEC = StreamCodec.ofMember(StructureTeleportPacket::write, StructureTeleportPacket::read);

	public static StructureTeleportPacket read(FriendlyByteBuf buf) {
		return new StructureTeleportPacket();
	}

	public void write(FriendlyByteBuf buf) {
	}

	public static void handle(StructureTeleportPacket packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			final ItemStack stack = StructureCompassItemUtils.getHeldItem(context.player(), ModItems.STRUCTURE_COMPASS);
			if (!stack.isEmpty()) {
				final StructureCompassItem explorersCompass = (StructureCompassItem) stack.getItem();
				final ServerPlayer player = (ServerPlayer) context.player();
				if (StructureCompassConfig.allowTeleport && StructureCompassPlayerUtils.canTeleport(player.level().getServer(), player)) {
					if (explorersCompass.getCompassState(stack) == StructureCompassState.FOUND) {
						final int x = stack.getOrDefault(ModDataComponents.FOUND_X, 0);
						final int z = stack.getOrDefault(ModDataComponents.FOUND_Z, 0);
						final int y = packet.findValidTeleportHeight(player.level(), x, z);

						player.stopRiding();
						player.connection.teleport(x, y, z, player.getYRot(), player.getXRot());

						if (!player.isFallFlying()) {
							player.setDeltaMovement(player.getDeltaMovement().x(), 0, player.getDeltaMovement().z());
							player.setOnGround(true);
						}
					}
				} else {
					BingoExtra.LOGGER.warn("Player " + player.getDisplayName().getString() + " tried to teleport but does not have permission.");
				}
			}
		});
	}
	
	@Override
	public Type<StructureTeleportPacket> type() {
		return TYPE;
	}
	
	private int findValidTeleportHeight(Level level, int x, int z) {
		int upY = level.getSeaLevel();
		int downY = level.getSeaLevel();
		while ((!level.isOutsideBuildHeight(upY) || !level.isOutsideBuildHeight(downY)) && !(isValidTeleportPosition(level, new BlockPos(x, upY, z)) || isValidTeleportPosition(level, new BlockPos(x, downY, z)))) {
			upY++;
			downY--;
		}
		BlockPos upPos = new BlockPos(x, upY, z);
		BlockPos downPos = new BlockPos(x, downY, z);
		if (isValidTeleportPosition(level, upPos)) {
			return upY;
		}
		if (isValidTeleportPosition(level, downPos)) {
			return downY;
		}
		return 256;
	}
	
	private boolean isValidTeleportPosition(Level level, BlockPos pos) {
		return isFree(level, pos) && isFree(level, pos.above()) && !isFree(level, pos.below());
	}
	
	private boolean isFree(Level level, BlockPos pos) {
		return level.getBlockState(pos).isAir() || level.getBlockState(pos).is(BlockTags.FIRE) || level.getBlockState(pos).liquid() || level.getBlockState(pos).canBeReplaced();
	}

}