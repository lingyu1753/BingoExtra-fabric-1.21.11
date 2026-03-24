package com.bingoextra.item;

import com.bingoextra.core.component.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import static com.bingoextra.BingoExtra.LOGGER;

public class SoulVaultItem extends Item {

    public SoulVaultItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    // 对生物右键（存储）
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (stack.get(ModDataComponents.STORED_ENTITY) != null) {
            return InteractionResult.FAIL;
        }
        EntityType<?> type = target.getType();
        if (type == EntityType.WITHER || type == EntityType.ENDER_DRAGON || type == EntityType.PLAYER) {
            return InteractionResult.FAIL;
        }
        ProblemReporter problemReporter = ProblemReporter.DISCARDING;
        TagValueOutput output = TagValueOutput.createWithoutContext(problemReporter);
        target.save(output);

        CompoundTag entityTag = output.buildResult();
        stack.set(ModDataComponents.STORED_ENTITY, entityTag);
        player.setItemInHand(hand, stack);

        target.discard();
        level.playSound(null, target.blockPosition(), SoundEvents.BEEHIVE_ENTER, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    // 对方块右键（释放）
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();

        CompoundTag storedTag = stack.get(ModDataComponents.STORED_ENTITY);
        if (storedTag == null) {
            return InteractionResult.FAIL;
        }

        String id = String.valueOf(storedTag.getString("id"));
        id = id.substring(9, id.length() - 1);
        EntityType<?> type = EntityType.byString(id).orElse(null);
        if (type == null) {
            stack.remove(ModDataComponents.STORED_ENTITY);
            return InteractionResult.FAIL;
        }

        BlockPos pos = context.getClickedPos().above().relative(context.getClickedFace());

        Entity entity = EntityType.loadEntityRecursive(
                storedTag,
                level,
                null,
                e -> {
                    e.setPos(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5);
                    return e;
                });

        if (entity == null) {
            return InteractionResult.FAIL;
        }

        level.addFreshEntity(entity);
        stack.remove(ModDataComponents.STORED_ENTITY);
        level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}