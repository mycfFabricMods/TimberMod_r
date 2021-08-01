package mycf.timber.mixin;


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

    /*
    Integer mode = 1;

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient()){
            if(mode == 1){
                mode = 2;
                // Text text = new LiteralText("Chop all");
                // user.sendMessage(text,true);

            }else{
                mode = 1;
                // Text text = new LiteralText("Chop 1");
                // user.sendMessage(text,true);

            }
        }
        return super.use(world, user, hand);
    }
    */

    // works
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean mode = stack.getOrCreateNbt().getBoolean("MYCFTimberMode");
        if (mode) {
            if (state.isIn(BlockTags.LOGS)) {
                int damage = 1;
                int maxDamage1 = stack.getMaxDamage(); // The Axes should never break any Blocks if they would have been broken before them
                int maxDamage = stack.getDamage();
                int trueDamage = Math.abs(maxDamage1 - maxDamage);

                // straight trees -> but implementing big trees is not that hard
                Iterator<BlockPos> iteratorUp = BlockPos.iterate(pos.add(0, 1, 0), pos.add(0, 255, 0)).iterator();
                // Always starts at the bottom of the world and therefore cant break any blocks
                // for loop solves this...
                // Is there any way to reduce this to one loop? (for i in range(0,255): blockState(i) and BlockState(-i)
                //Iterator<BlockPos> iteratorDown = BlockPos.iterate(pos.add(0, -1, 0), pos.add(0, -(pos.getY()), 0)).iterator();


                // This always checks ALL Blocks from building height to bedrock...
                // It also breaks blocks that are not connected!

                /*
                for(int i = 0; i < 256; i++) {

                    if(world.getBlockState(pos.down(i)).isIn(BlockTags.LOGS) && damage < maxDamage) {
                        world.breakBlock(pos.down(i), true);
                        damage++;
                    }

                    if(world.getBlockState(pos.up(i)).isIn(BlockTags.LOGS) && damage < maxDamage) {
                        world.breakBlock(pos.up(i), true);
                        damage++;
                    }
                }

                 */

                do {
                    BlockPos blockPos = iteratorUp.next();

                    if (world.getBlockState(blockPos).isIn(BlockTags.LOGS) && damage < trueDamage) {
                        world.breakBlock(blockPos, true);
                        damage++;

                    } else {
                        break;
                    }

                } while (iteratorUp.hasNext());


                // Best Option for checking all of the blocks below?
                for(int i = 1; i < pos.getY() && world.getBlockState(pos.down(i)).isIn(BlockTags.LOGS) && damage < trueDamage; i++){
                    world.breakBlock(pos.down(i), true);
                    damage++;
                }

                /*
                do {
                    BlockPos blockPos2 = iteratorDown.next();

                    if (world.getBlockState(blockPos2).isIn(BlockTags.LOGS)) {
                        world.breakBlock(blockPos2, true);
                        damage++;

                    } else {
                        break;
                    }

                } while (iteratorDown.hasNext() && damage <= maxDamage);
                   */

                if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                    stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

                }
        /*
                int i = 1;
                int ii = 1;
                for (int j = pos.getY(); j <= 255; j++) {
                    BlockState staup = world.getBlockState(pos.up(i));
                    BlockState stadown = world.getBlockState(pos.down(ii));
                    if (staup.isIn(BlockTags.LOGS)) {
                        i++;
                    } else if (stadown.isIn(BlockTags.LOGS)) {
                        ii++;
                    } else {
                        j = 266;
                    }
                }
                for (int k = 1; k <= i - 1; k++) {
                    world.breakBlock(pos.up(k), true);
                }
                for (int k = 1; k <= ii - 1; k++) {
                    world.breakBlock(pos.down(k), true);
                }
                int damages = ii + i - 1;
                if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                    stack.damage(damages, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                }
         */
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