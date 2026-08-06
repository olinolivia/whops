package olinolivia.whops.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected abstract float getJumpPower();

    @Shadow
    protected boolean jumping;

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.003))
    private double snapThreshold(double constant) {
        LivingEntity self = (LivingEntity)(Object)this;
        return LegacyGameRules.get(self.level()).minimumVelocity();
    }

    @ModifyExpressionValue(method = "getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"))
    private boolean blockSwimGlide(boolean original) {
        LivingEntity self = (LivingEntity)(Object)this;
        return original && LegacyGameRules.get(self.level()).allowSwimming();
    }

    @ModifyArg(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"), index = 1)
    private double bounceCancel(double max) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (max == self.getDeltaMovement().y && !LegacyGameRules.get(self.level()).allowBounceBoost()) return this.getJumpPower();
        return max;
    }

    @Unique
    private boolean actuallyJumping = false;

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("HEAD"))
    private void dontClimb1(Vec3 input, float friction, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!LegacyGameRules.get(self.level()).easyClimbing()) {
            actuallyJumping = jumping;
            jumping = false;
        }
    }

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("TAIL"))
    private void dontClimb2(Vec3 input, float friction, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!LegacyGameRules.get(self.level()).easyClimbing()) jumping = actuallyJumping;
    }

}
