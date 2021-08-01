package mycf.timber.mixin;


import com.google.common.collect.Sets;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(AxeItem.class)
public class AxeMixin extends MiningToolItem {
    protected AxeMixin(float attackDamage, float attackSpeed, ToolMaterial material, Set<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, (Tag<Block>) effectiveBlocks, settings);
    }
    Integer mode = 1;

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient()){
            if(mode == 1){
                mode = 2;
                Text text = new LiteralText("Chop all");
                user.sendMessage(text,true);

            }else{
                mode = 1;
                Text text = new LiteralText("Chop 1");
                user.sendMessage(text,true);

            }
        }
        return super.use(world, user, hand);
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if(mode == 2) {
            if(state.isIn(BlockTags.LOGS)) {
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
            } else {
                if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                    stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                }
            }
        }else{
            if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                stack.damage(1, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
        return true;
    }
}