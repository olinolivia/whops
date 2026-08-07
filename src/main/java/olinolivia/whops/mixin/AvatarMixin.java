package olinolivia.whops.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public abstract class AvatarMixin extends Entity {

    private AvatarMixin(EntityType<?> type, Level level) {super(type, level);}

    @Unique
    private LegacyGameRules legacyRules() {
        return LegacyGameRules.get(level());
    }

    @Inject(method = "getDefaultDimensions", at = @At("RETURN"), cancellable = true)
    private void sneakHeight(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == Pose.CROUCHING) {
            double height = legacyRules().sneakHeight();
            EntityDimensions crouch = cir.getReturnValue();
            cir.setReturnValue(
                    EntityDimensions.scalable(
                            crouch.width(),
                            (float) height).withEyeHeight(1.27f / 1.5f * (float) height
                    ).withAttachments(
                            EntityAttachments.builder().attach(EntityAttachment.VEHICLE, Avatar.DEFAULT_VEHICLE_ATTACHMENT)
                    )
            );
        }
    }

}