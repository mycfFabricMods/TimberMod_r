package mycf.timber.mixin;

import mycf.timber.PlayerEntityTimber;
import mycf.timber.Timber;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Shadow
    public abstract NbtCompound getNbt();


    @Inject(method = "use", at = @At(value = "HEAD"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (this.getItem() instanceof AxeItem && !((PlayerEntityTimber) user).getTimberMode()) {
            if (!this.getOrCreateNbt().contains(Timber.TIMBER_KEY)) {
                this.getOrCreateNbt().putBoolean(Timber.TIMBER_KEY, false);
            }

            boolean bl = this.getNbt().getBoolean(Timber.TIMBER_KEY);
            this.getOrCreateNbt().putBoolean(Timber.TIMBER_KEY, !bl);
            if (bl) {
                // maybe switch 'chop all' and 'chop 1'
                user.sendMessage(new LiteralText("chop 1"), true);
            } else {
                user.sendMessage(new LiteralText("chop all"), true);
            }
        }
    }


    // TODO maybe set it to either mode
    // TODO maybe add shift so you dont do that when building a base
    @Inject(method = "useOnBlock", at = @At(value = "HEAD"))
    public void useOnBlock(ItemUsageContext ctx, CallbackInfoReturnable<ActionResult> cir) {

        if (ctx.getPlayer() != null) {

            if (this.getItem() instanceof AxeItem) {
                if (!this.getOrCreateNbt().contains(Timber.TIMBER_KEY)) {
                    this.getOrCreateNbt().putBoolean(Timber.TIMBER_KEY, false);
                }
/*
            if (!this.getOrCreateNbt().contains(Timber.TIMBER_TOGGLE_KEY)) {
                this.getOrCreateNbt().putBoolean(Timber.TIMBER_TOGGLE_KEY, false);
            }
 */

                if (ctx.getWorld().getBlockState(ctx.getBlockPos()).getBlock().equals(Blocks.POLISHED_GRANITE)) {
                    // boolean bl = this.getNbt().getBoolean(Timber.TIMBER_TOGGLE_KEY);
                    // this.getOrCreateNbt().putBoolean(Timber.TIMBER_TOGGLE_KEY, !bl);

                    boolean bl = ((PlayerEntityTimber) ctx.getPlayer()).getTimberMode();
                    ((PlayerEntityTimber) ctx.getPlayer()).setTimberMode(!bl);
                    this.getOrCreateNbt().putBoolean(Timber.TIMBER_KEY, false);
                    Objects.requireNonNull(ctx.getPlayer()).sendMessage(new TranslatableText("item.timber.axe.nevermode"), true);

                }

            }
        }
    }
}


