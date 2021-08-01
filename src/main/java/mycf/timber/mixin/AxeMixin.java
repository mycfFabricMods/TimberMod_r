package mycf.timber.mixin;


import mycf.timber.Timber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Iterator;
import java.util.Set;

@Mixin(AxeItem.class)
public class AxeMixin extends MiningToolItem {
    protected AxeMixin(float attackDamage, float attackSpeed, ToolMaterial material, Set<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, (Tag<Block>) effectiveBlocks, settings);
    }

    // works
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean mode = stack.getOrCreateNbt().getBoolean(Timber.TIMBER_KEY);
        if (mode) {
            if (state.isIn(BlockTags.LOGS)) {
                int damage = 1;
                int trueDamage = Math.abs(stack.getMaxDamage() - stack.getDamage());

                Iterator<BlockPos> iteratorUp = BlockPos.iterate(pos.add(0, 1, 0), pos.add(0, 255, 0)).iterator();

                do {
                    BlockPos blockPos = iteratorUp.next();

                    if (world.getBlockState(blockPos).isIn(BlockTags.LOGS) && damage < trueDamage) {
                        world.breakBlock(blockPos, true);
                        damage++;

                    } else {
                        break;
                    }

                } while (iteratorUp.hasNext());


                for(int i = 1; i < pos.getY() && world.getBlockState(pos.down(i)).isIn(BlockTags.LOGS) && damage < trueDamage; i++){
                    world.breakBlock(pos.down(i), true);
                    damage++;
                }
                if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                    stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

                }
                } else {
                    if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                        stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                    }
                }

            } else {
                if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                    stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                }
            }

        return true;
    }
}