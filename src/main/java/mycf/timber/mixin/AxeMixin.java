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
    private int MAX_BREAK_BLOCKS = 30;

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {

        // I think I should do this recursively too... that way I really get every block from one tree
        // make sure I store the Log Type to cross validate if it's the same tree
        // Maybe I should go up and down and only 1 or 2 left/right to avoid abusing this feature
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

            // wont work
            System.out.println(world.getBlockState(mutableUp));
            if (world.getBlockState(mutableUp.offset(Direction.DOWN)).isIn(BlockTags.LEAVES)){
                damage = deleteLeaves(world, mutableUp, damage, trueDamage);
            }

            // state.getHardness(world, pos) != 0.0F shouldn't ever be false, but I'll leave it just in case
            if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
                this.MAX_BREAK_BLOCKS = 30;
                stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            }

        } else if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    // broke this call
    private int deleteLeaves(World world, BlockPos pos, int damage, int trueDamage) {
        for (var direction : Direction.values()) {
            var blockState = world.getBlockState(pos.offset(direction));
            if (blockState.isIn(BlockTags.LEAVES) && this.MAX_BREAK_BLOCKS > 0) {
                ((LeavesBlock)blockState.getBlock()).randomTick(blockState, (ServerWorld) world, pos, world.getRandom());
                // dont like this solution
                this.MAX_BREAK_BLOCKS--;
                damage = deleteLeaves(world, pos.offset(direction), damage, trueDamage);
            } else if (blockState.isIn(BlockTags.LOGS) && damage < trueDamage && this.MAX_BREAK_BLOCKS > 0) {
                damage++;
                world.breakBlock(pos, true);
                // dont like this solution
                this.MAX_BREAK_BLOCKS--;
                damage = deleteLeaves(world, pos.offset(direction), damage, trueDamage);
            }
        }
        return damage;
    }
}