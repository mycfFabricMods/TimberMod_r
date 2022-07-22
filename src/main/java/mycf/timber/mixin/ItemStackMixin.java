package mycf.timber.mixin;

import mycf.timber.TimberModes;
import mycf.timber.Toggleable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Toggleable {

    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Override
    public boolean getToggleMode$mycftimber() {
        return this.getOrCreateNbt().getBoolean(TimberModes.AXES.id);
    }

    @Override
    public void setTimberMode$mycftimber(boolean mode) {
        this.getOrCreateNbt().putBoolean(TimberModes.AXES.id, mode);
    }
}


