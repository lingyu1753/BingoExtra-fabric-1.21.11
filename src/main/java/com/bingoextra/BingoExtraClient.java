package com.bingoextra;

import com.bingoextra.network.BiomeSyncPacket;

import com.bingoextra.network.StructureSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BingoExtraClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(BiomeSyncPacket.TYPE, BiomeSyncPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(StructureSyncPacket.TYPE, StructureSyncPacket::handle);
	}

}
