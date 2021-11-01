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
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
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

        // I think I should do this recursively too... that way I really get every block from one tree
        // make sure I store the Log Type to cross validate if it's the same tree
        // Maybe I should go up and down and only 1 or 2 left/right to avoid abusing this feature
        boolean mode = stack.getOrCreateNbt().getBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL);
        if (!world.isClient() && mode && state.isIn(BlockTags.LOGS)) {
            int damage = 1;
            int trueDamage = Math.abs(stack.getMaxDamage() - stack.getDamage());

            for (BlockPos blockPos : BlockPos.iterate(pos.add(0, 1, 0), pos.add(0, 255, 0))) {
                if (world.getBlockState(blockPos).isIn(BlockTags.LOGS) && damage < trueDamage) {
                    world.breakBlock(blockPos, true);
                    damage++;
                } else if (world.getBlockState(blockPos).isIn(BlockTags.LEAVES)) {
                    deleteLeaves(world, blockPos);
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; i < pos.getY() && world.getBlockState(pos.down(i)).isIn(BlockTags.LOGS) && damage < trueDamage; i++) {
                world.breakBlock(pos.down(i), true);
                damage++;
            }

            // state.getHardness(world, pos) != 0.0F shouldn't ever be called, but I'll leave it just in case
            if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
                stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            }

        } else if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    // This is a bit toooo much atm
    // Store Leave type and cross validate with Log Type... this is only possible for vanilla leaves though
    private void deleteLeaves(World world, BlockPos pos) {
        for (var direction : Direction.values()){
            var blockState = world.getBlockState(pos.offset(direction));
            if (blockState.isIn(BlockTags.LEAVES) || blockState.isIn(BlockTags.LOGS)) {
//                ((LeavesBlock)world.getBlockState(pos.offset(direction)).getBlock()).scheduledTick();
                world.breakBlock(pos.offset(direction), true);
                deleteLeaves(world, pos.offset(direction));
            }
        }
    }
}