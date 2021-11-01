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
        if (!world.isClient() && this.getItem() instanceof AxeItem && !((PlayerEntityTimber) user).getTimberMode()) {
            if (!this.getOrCreateNbt().contains(Timber.TIMBER_ONE_OR_MORE_BOOL)) {
                this.getOrCreateNbt().putBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL, false);
            }

            boolean shouldChopOne = this.getNbt().getBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL);
            this.getOrCreateNbt().putBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL, !shouldChopOne);
            if (shouldChopOne) {
                user.sendMessage(new TranslatableText("item.timber.axe.chopone"), true);
            } else {
                user.sendMessage(new TranslatableText("item.timber.axe.chopall"), true);
            }
        }
    }


    @Inject(method = "useOnBlock", at = @At(value = "HEAD"))
    public void useOnBlock(ItemUsageContext ctx, CallbackInfoReturnable<ActionResult> cir) {

        if (!ctx.getWorld().isClient() && ctx.getPlayer() != null) {
            if (ctx.getWorld().getBlockState(ctx.getBlockPos()).getBlock().equals(Blocks.POLISHED_GRANITE)) {
                if (this.getItem() instanceof AxeItem) {
                    if (!this.getOrCreateNbt().contains(Timber.TIMBER_NEVER_AGAIN_BOOL)) {
                        this.getOrCreateNbt().putBoolean(Timber.TIMBER_NEVER_AGAIN_BOOL, false);
                    }

                    boolean bl = ((PlayerEntityTimber) ctx.getPlayer()).getTimberMode();
                    ((PlayerEntityTimber) ctx.getPlayer()).setTimberMode(!bl);
                    this.getOrCreateNbt().putBoolean(Timber.TIMBER_ONE_OR_MORE_BOOL, false);
                    ctx.getPlayer().sendMessage(new TranslatableText("item.timber.axe.nevermode"), true);
                }
            }
        }
    }
}


