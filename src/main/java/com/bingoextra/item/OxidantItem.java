package com.bingoextra.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;

public class OxidantItem extends Item {

    public OxidantItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (block.getDescriptionId().contains("waxed")) return InteractionResult.FAIL;

        Optional<Block> nextBlockOpt = WeatheringCopper.getNext(block);
        if (nextBlockOpt.isEmpty()) return InteractionResult.FAIL;

        if (!block.getDescriptionId().contains("trapdoor") && block.getDescriptionId().contains("door") && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) return InteractionResult.FAIL;

        Block nextBlock = nextBlockOpt.get();
        BlockState newState = nextBlock.defaultBlockState();


        newState = copyBlockProperties(state, newState);
        level.setBlock(pos, newState, Block.UPDATE_ALL_IMMEDIATE);


        stack.consume(1, player);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private BlockState copyBlockProperties(BlockState from, BlockState to) {
        if (from.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && to.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            to = to.setValue(BlockStateProperties.HORIZONTAL_FACING, from.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }

        if (from.hasProperty(BlockStateProperties.FACING) && to.hasProperty(BlockStateProperties.FACING)) {
            to = to.setValue(BlockStateProperties.FACING, from.getValue(BlockStateProperties.FACING));
        }

        if (from.hasProperty(BlockStateProperties.WATERLOGGED) && to.hasProperty(BlockStateProperties.WATERLOGGED)) {
            to = to.setValue(BlockStateProperties.WATERLOGGED, from.getValue(BlockStateProperties.WATERLOGGED));
        }

        if (from.hasProperty(BlockStateProperties.POWERED) && to.hasProperty(BlockStateProperties.POWERED)) {
            to = to.setValue(BlockStateProperties.POWERED, from.getValue(BlockStateProperties.POWERED));
        }

        if (from.hasProperty(BlockStateProperties.OPEN) && to.hasProperty(BlockStateProperties.OPEN)) {
            to = to.setValue(BlockStateProperties.OPEN, from.getValue(BlockStateProperties.OPEN));
        }

        if (from.hasProperty(BlockStateProperties.HALF) && to.hasProperty(BlockStateProperties.HALF)) {
            to = to.setValue(BlockStateProperties.HALF, from.getValue(BlockStateProperties.HALF));
        }

        if (from.hasProperty(BlockStateProperties.STAIRS_SHAPE) && to.hasProperty(BlockStateProperties.STAIRS_SHAPE)) {
            to = to.setValue(BlockStateProperties.STAIRS_SHAPE, from.getValue(BlockStateProperties.STAIRS_SHAPE));
        }

        if (from.hasProperty(BlockStateProperties.LIT) && to.hasProperty(BlockStateProperties.LIT)) {
            to = to.setValue(BlockStateProperties.LIT, from.getValue(BlockStateProperties.LIT));
        }

        if (from.hasProperty(BlockStateProperties.DOOR_HINGE) && to.hasProperty(BlockStateProperties.DOOR_HINGE)) {
            to = to.setValue(BlockStateProperties.DOOR_HINGE, from.getValue(BlockStateProperties.DOOR_HINGE));
        }

        return to;
    }
}