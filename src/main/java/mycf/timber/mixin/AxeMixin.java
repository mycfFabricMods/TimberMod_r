package mycf.timber.mixin;


import mycf.timber.Timber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Set;

@Mixin(AxeItem.class)
public class AxeMixin extends MiningToolItem {
    protected AxeMixin(float attackDamage, float attackSpeed, ToolMaterial material, Set<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, (Tag<Block>) effectiveBlocks, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {

        boolean mode = stack.getOrCreateNbt().getBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL);
        if (!world.isClient() && mode && state.isIn(BlockTags.LOGS)) {
            int damage = 1;
            final int trueDamage = Math.abs(stack.getMaxDamage() - stack.getDamage());

            BlockPos.Mutable mutableUp = pos.mutableCopy();
            BlockPos.Mutable mutableDown = pos.mutableCopy();

            while (world.getBlockState(mutableUp.move(Direction.UP)).isIn(BlockTags.LOGS) && damage < trueDamage) {
                world.breakBlock(mutableUp, true);
                damage++;
            }
            while (world.getBlockState(mutableDown.move(Direction.DOWN)).isIn(BlockTags.LOGS) && damage < trueDamage) {
                world.breakBlock(mutableDown, true);
                damage++;
            }

            if (world.getBlockState(mutableUp.offset(Direction.UP)).isIn(BlockTags.LEAVES)) {
                for (BlockPos blockPos : BlockPos.iterateOutwards(mutableUp.offset(Direction.UP), 2, 2, 2)) {
                    var blockState = world.getBlockState(blockPos);
                    if (blockState.isIn(BlockTags.LEAVES)) {
                        ((LeavesBlock) blockState.getBlock()).scheduledTick(blockState, (ServerWorld) world, blockPos, world.getRandom());
                    } else if (blockState.isIn(BlockTags.LOGS) && damage < trueDamage) {
                        world.breakBlock(blockPos, true);
                        damage++;
                    }
                }
            }

            if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
                stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            }

        } else if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }
}