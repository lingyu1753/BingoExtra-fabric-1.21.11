package com.bingoextra.callback.block;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

import static com.bingoextra.BingoExtra.LOGGER;

public class DyeTransformer implements UseBlockCallback {

    public static class DyeTransformedBlockRecipes {
        private final DyeColor dyeColor;
        private final Block originalBlock;
        private final Block transformedBlock;

        public DyeTransformedBlockRecipes(DyeColor dyeColor, Block originalBlock, Block transformedBlock) {
            this.dyeColor = dyeColor;
            this.originalBlock = originalBlock;
            this.transformedBlock = transformedBlock;
        }

        public DyeColor getDyeColor() {
            return dyeColor;
        }

        public Block getOriginalBlock() {
            return originalBlock;
        }

        public Block getTransformedBlock() {
            return transformedBlock;
        }
    }

    public static final List<DyeTransformedBlockRecipes> recipes = new ArrayList<>();

    public static void createRecipes() {
        recipes.add(new DyeTransformedBlockRecipes(DyeColor.RED, Blocks.STONE, Blocks.REDSTONE_ORE));
        recipes.add(new DyeTransformedBlockRecipes(DyeColor.RED, Blocks.DEEPSLATE, Blocks.DEEPSLATE_REDSTONE_ORE));
    }

    public DyeTransformer() {
        createRecipes();
        LOGGER.info("callback.block.DyeTransformer init");
    }

    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState clickedState = level.getBlockState(pos);          // 修正：使用当前世界的方块状态
        DyeColor dyeColor = dyeItem.getDyeColor();                      // 修正：Mojang 映射中的正确方法
        DyeTransformedBlockRecipes matchedRecipe = findMatchingRecipe(clickedState.getBlock(), dyeColor);

        if (matchedRecipe == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 消耗染料（创造模式不消耗）
        if (!player.isCreative()) {
            stack.shrink(1);
        }

        // 转换方块
        BlockState newState = matchedRecipe.getTransformedBlock().defaultBlockState();
        level.setBlock(pos, newState, 3);

        // 播放音效
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }

    private DyeTransformedBlockRecipes findMatchingRecipe(Block originalBlock, DyeColor dyeColor) {
        for (DyeTransformedBlockRecipes recipe : recipes) {
            if (recipe.getOriginalBlock() == originalBlock && recipe.getDyeColor() == dyeColor) {
                return recipe;
            }
        }
        return null;
    }
}