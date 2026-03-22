package com.bingoextra.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bingoextra.BingoExtra;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.network.BiomeSearchPacket;
import com.bingoextra.network.BiomeTeleportPacket;
import com.bingoextra.utils.BiomeUtils;
import com.bingoextra.item.BiomeCompassItem;
import com.bingoextra.network.BiomeSearchForNextPacket;
import com.bingoextra.sorting.BiomeISorting;
import com.bingoextra.sorting.BiomeNameSorting;
import com.bingoextra.utils.BiomeCompassState;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BiomeCompassScreen extends Screen {

	public Level level;
	private Player player;
	private List<Identifier> allowedBiomes;
	private List<Identifier> biomesMatchingSearch;
	private Identifier foundBiomeId;
	private Button searchForBiomeButton;
	private Button searchForNextButton;
	private Button teleportButton;
	private Button cancelButton;
	private Button sortByButton;
	private BiomeCompassTransparentEditBox searchBox;
	private BiomeSearchList selectionList;
	private BiomeISorting<?> sortingCategory;

	public BiomeCompassScreen(Level level, Player player, ItemStack stack, BiomeCompassItem natureCompass, List<Identifier> allowedBiomes) {
		super(Component.translatable("string.bingoextra.selectBiome"));
		this.level = level;
		this.player = player;
		this.allowedBiomes = new ArrayList<Identifier>(allowedBiomes);

		sortingCategory = new BiomeNameSorting();
		biomesMatchingSearch = new ArrayList<Identifier>(this.allowedBiomes);

		if (natureCompass.getCompassState(stack) == BiomeCompassState.FOUND) {
			String foundBiomeIdStr = stack.getOrDefault(ModDataComponents.BIOME_ID, null);
			if (foundBiomeIdStr != null) {
				foundBiomeId = Identifier.parse(foundBiomeIdStr);
			}
		}
	}

	@Override
	public boolean mouseScrolled(double par1, double par2, double par3, double par4) {
		return selectionList.mouseScrolled(par1, par2, par3, par4);
	}

	@Override
	protected void init() {
		setupWidgets();
	}

	@Override
	public void tick() {
		searchForNextButton.active = teleportButton.active = selectionList.hasSelection() ? selectionList.getSelected().getBiomeId().equals(foundBiomeId) : false;
		searchForBiomeButton.active = selectionList.hasSelection();

		// Check if the sync packet has been received
		if (BingoExtra.synced) {
			teleportButton.visible = BingoExtra.canTeleport;
			removeWidget(selectionList);
			allowedBiomes = new ArrayList<Identifier>(BingoExtra.allowedBiomes);
			biomesMatchingSearch = new ArrayList<Identifier>(allowedBiomes);
			selectionList = new BiomeSearchList(this, minecraft, player, foundBiomeId, width + 110, height - 50, 40, 50);
			addRenderableWidget(selectionList);

			teleportButton.visible = BingoExtra.canTeleport;
			searchForNextButton.visible = BingoExtra.maxNextSearches > 0;
			if (searchForNextButton.visible) {
				sortByButton.setPosition(10, 100);
			} else {
				sortByButton.setPosition(10, 65);
			}

			BingoExtra.synced = false;
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(font, I18n.get("string.bingoextra.selectBiome"), 65, 15, 0xffffffff);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		boolean ret = super.keyPressed(event);
		if (searchBox.isFocused()) {
			processSearchTerm();
			return true;
		}
		return ret;
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		boolean ret = super.charTyped(event);
		if (searchBox.isFocused()) {
			processSearchTerm();
			return true;
		}
		return ret;
	}

	public void searchForBiome(Identifier biomeId) {
		ClientPlayNetworking.send(new BiomeSearchPacket(biomeId, player.blockPosition()));
		minecraft.setScreen(null);
	}

	public void searchForNextBiome() {
		ClientPlayNetworking.send(new BiomeSearchForNextPacket());
		minecraft.setScreen(null);
	}

	public void teleport() {
		ClientPlayNetworking.send(new BiomeTeleportPacket());
		minecraft.setScreen(null);
	}

	public BiomeISorting<?> getSortingCategory() {
		return sortingCategory;
	}

	public void processSearchTerm() {
		biomesMatchingSearch = new ArrayList<Identifier>();
		String searchTerm = searchBox.getValue().toLowerCase();
		for (Identifier biomeId : allowedBiomes) {
			if (searchTerm.startsWith("$")) {
				if (BiomeUtils.getBiomeTags(level, biomeId).toLowerCase().contains(searchTerm.substring(1))) {
					biomesMatchingSearch.add(biomeId);
				}
			} else if (searchTerm.startsWith("@")) {
				if (BiomeUtils.getBiomeSource(level, biomeId).toLowerCase().contains(searchTerm.substring(1))) {
					biomesMatchingSearch.add(biomeId);
				}
			} else if (BiomeUtils.getBiomeNameForDisplay(level, biomeId).toLowerCase().contains(searchTerm)) {
				biomesMatchingSearch.add(biomeId);
			}
		}
		selectionList.refreshList(true);
	}

	public List<Identifier> sortBiomes() {
		final List<Identifier> biomes = biomesMatchingSearch;
		Collections.sort(biomes, new BiomeNameSorting());
		Collections.sort(biomes, sortingCategory);

		return biomes;
	}

	private void setupWidgets() {
		clearWidgets();

		searchForBiomeButton = addRenderableWidget(new BiomeCompassTransparentButton(10, 40, 110, 20, Component.translatable("string.bingoextra.search"), (onPress) -> {
			if (selectionList.hasSelection()) {
				searchForBiome(selectionList.getSelected().getBiomeId());
			}
		}));
		searchForBiomeButton.active = false;

		searchForNextButton = addRenderableWidget(new BiomeCompassTransparentButton(10, 65, 110, 20, Component.translatable("string.bingoextra.searchForNext"), (onPress) -> {
			searchForNextBiome();
		}));
		searchForNextButton.visible = BingoExtra.maxNextSearches > 0;
		searchForNextButton.active = false;

		sortByButton = addRenderableWidget(new BiomeCompassTransparentButton(10, 100, 110, 20, Component.literal(I18n.get("string.bingoextra.sortBy") + ": " + sortingCategory.getLocalizedName()), (onPress) -> {
			sortingCategory = sortingCategory.next();
			sortByButton.setMessage(Component.literal(I18n.get("string.bingoextra.sortBy") + ": " + sortingCategory.getLocalizedName()));
			selectionList.refreshList(true);
		}));
		if (!searchForNextButton.visible) {
			sortByButton.setPosition(10, 65);
		}

		teleportButton = addRenderableWidget(new BiomeCompassTransparentButton(width - 120, 10, 110, 20, Component.translatable("string.bingoextra.teleport"), (onPress) -> {
			teleport();
		}));
		teleportButton.visible = BingoExtra.canTeleport;
		teleportButton.active = false;

		cancelButton = addRenderableWidget(new BiomeCompassTransparentButton(10, height - 30, 110, 20, Component.translatable("gui.cancel"), (onPress) -> {
			minecraft.setScreen(null);
		}));

		searchBox = addRenderableWidget(new BiomeCompassTransparentEditBox(font, 130, 10, 140, 20, Component.translatable("string.bingoextra.search")));
        searchBox.setHint(Component.translatable("string.bingoextra.search"));

		if (selectionList == null) {
			selectionList = new BiomeSearchList(this, minecraft, player, foundBiomeId, width + 110, height - 50, 40, 50);
		}
		addRenderableWidget(selectionList);
	}

}
