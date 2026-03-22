package com.bingoextra.gui;

import com.bingoextra.BingoExtra;
import com.bingoextra.item.BiomeCompassItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BiomeCompassGuiWrapper {
	
	public static void openGUI(Level level, Player player, ItemStack stack) {
		Minecraft.getInstance().setScreen(new BiomeCompassScreen(level, player, stack, (BiomeCompassItem) stack.getItem(), BingoExtra.allowedBiomes));
	}

}
