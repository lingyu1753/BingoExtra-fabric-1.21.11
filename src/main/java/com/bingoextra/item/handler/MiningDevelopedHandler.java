package com.bingoextra.item.handler;

import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.gamerules.ModGameRules;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.bingoextra.BingoExtra.LOGGER;

public class MiningDevelopedHandler {
    private static final Set<Block> MINERALS = Set.of(
            Blocks.COAL_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.RAW_COPPER_BLOCK,
            Blocks.IRON_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.RAW_IRON_BLOCK,
            Blocks.GOLD_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.RAW_GOLD_BLOCK,
            Blocks.NETHER_GOLD_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS
                                                     );

    private static boolean isMineral(BlockState state) {
        return MINERALS.contains(state.getBlock());
    }

    private static void updateMiningDeveloped(ServerLevel level, BlockState state, ItemStack tool) {
        Integer cnt = tool.get(ModDataComponents.MINING_DEVELOPED);
        if (cnt != null &&isMineral(state)) {
            cnt++;
            int threshold = level.getGameRules().get(ModGameRules.MINING_DEVELOPED);
            if (cnt == threshold) {
                cnt = 0;

                tool.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

                ItemEnchantments stored = tool.get(DataComponents.ENCHANTMENTS);
                if (stored == null) stored = ItemEnchantments.EMPTY;
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);

                Holder<Enchantment> efficiencyHolder = level.registryAccess()
                                                            .lookupOrThrow(Registries.ENCHANTMENT)
                                                            .getOrThrow(Enchantments.EFFICIENCY);

                int currentGrade = mutable.getLevel(efficiencyHolder);
                int maxGrade = level.getGameRules().get(ModGameRules.MINING_LEVEL);
                if (currentGrade < maxGrade) {
                    mutable.set(efficiencyHolder, currentGrade + 1);
                    tool.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                }
            }
            tool.set(ModDataComponents.MINING_DEVELOPED, cnt);
        }
    }

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            ItemStack tool = player.getMainHandItem();
            updateMiningDeveloped((ServerLevel) level, state, tool);
        });
        LOGGER.info("item.handler.ChainingBreakLogHandler init");
    }
}
