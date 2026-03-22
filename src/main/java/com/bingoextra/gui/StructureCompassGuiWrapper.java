package com.bingoextra.gui;

import com.bingoextra.BingoExtra;
import com.bingoextra.item.StructureCompassItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StructureCompassGuiWrapper {
	
	public static void openGUI(Level level, Player player, ItemStack stack) {
		Minecraft.getInstance().setScreen(new StructureCompassScreen(level, player, stack, (StructureCompassItem) stack.getItem(), BingoExtra.allowedStructures));
	}

}
