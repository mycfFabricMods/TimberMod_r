package mycf.timber.mixin;

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
        if (this.getItem() instanceof AxeItem && !this.getOrCreateNbt().getBoolean("MYCFTimberModeNever")) {
            if (!this.getOrCreateNbt().contains("MYCFTimberMode")) {
                this.getOrCreateNbt().putBoolean("MYCFTimberMode", false);
            }

            boolean bl = this.getNbt().getBoolean("MYCFTimberMode");
            this.getOrCreateNbt().putBoolean("MYCFTimberMode", !bl);
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
        if (this.getItem() instanceof AxeItem) {
            if (!this.getOrCreateNbt().contains("MYCFTimberMode")) {
                this.getOrCreateNbt().putBoolean("MYCFTimberMode", false);
            }

            if (!this.getOrCreateNbt().contains("MYCFTimberModeNever")) {
                this.getOrCreateNbt().putBoolean("MYCFTimberMode", false);
            }

            if (ctx.getWorld().getBlockState(ctx.getBlockPos()).getBlock().equals(Blocks.POLISHED_GRANITE)) {
                boolean bl = this.getNbt().getBoolean("MYCFTimberModeNever");
                this.getOrCreateNbt().putBoolean("MYCFTimberModeNever", !bl);
                this.getOrCreateNbt().putBoolean("MYCFTimberMode", false);
                Objects.requireNonNull(ctx.getPlayer()).sendMessage(new TranslatableText("item.timber.axe.nevermode"), true);
            }

        }
    }
}


