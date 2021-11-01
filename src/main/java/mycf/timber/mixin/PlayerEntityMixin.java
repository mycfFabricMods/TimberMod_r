package mycf.timber.mixin;

import mycf.timber.PlayerEntityTimber;
import mycf.timber.Timber;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityTimber {
    private boolean timberMode;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(Timber.TIMBER_NEVER_AGAIN_BOOL, this.timberMode);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.timberMode = nbt.getBoolean(Timber.TIMBER_NEVER_AGAIN_BOOL);
    }

    @Override
    public boolean getTimberMode() {
        return this.timberMode;
    }

    @Override
    public void setTimberMode(boolean mode) {
        this.timberMode = mode;
    }

}