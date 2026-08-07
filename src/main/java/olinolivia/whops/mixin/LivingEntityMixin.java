package olinolivia.whops.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected abstract float getJumpPower();

    private LivingEntityMixin(EntityType<?> type, Level level) {super(type, level);}

    @Unique
    private LegacyGameRules legacyRules() {
        return LegacyGameRules.get(level());
    }

    @Shadow
    protected boolean jumping;

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.003))
    private double snapThreshold(double constant) {
        return legacyRules().minimumVelocity();
    }

    @ModifyExpressionValue(method = "getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"))
    private boolean blockSwimGlide(boolean original) {
        return original && legacyRules().allowSwimming();
    }

    @ModifyArg(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"), index = 1)
    private double bounceCancel(double max) {
        if (max == getDeltaMovement().y && !legacyRules().allowBounceBoost()) return this.getJumpPower();
        return max;
    }

    @Unique
    private boolean actuallyJumping = false;

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("HEAD"))
    private void dontClimb1(Vec3 input, float friction, CallbackInfoReturnable<Vec3> cir) {
        if (!legacyRules().easyClimbing()) {
            actuallyJumping = jumping;
            jumping = false;
        }
    }

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("TAIL"))
    private void dontClimb2(Vec3 input, float friction, CallbackInfoReturnable<Vec3> cir) {
        if (!legacyRules().easyClimbing()) jumping = actuallyJumping;
    }

}
