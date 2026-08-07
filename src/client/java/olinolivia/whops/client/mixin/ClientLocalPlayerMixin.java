package olinolivia.whops.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocalPlayer.class)
public abstract class ClientLocalPlayerMixin extends Entity {

	private ClientLocalPlayerMixin(EntityType<?> type, Level level) {super(type, level);}

	@Unique
	private LegacyGameRules legacyRules() {
		return LegacyGameRules.get(level());
	}

	@ModifyConstant(method = "isHorizontalCollisionMinor", constant = @Constant(doubleValue = (double) 0.13962634F))
	private double sprintThreshold(double constant) {
		return legacyRules().sprintLeniency();
	}

	@ModifyArg(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprintingPossible(Z)Z"), index = 0)
	private boolean allowShallowWaterSprint1(boolean original) {
		return original || !legacyRules().allowSwimming();
	}

	@ModifyArg(method = "shouldStopRunSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprintingPossible(Z)Z"), index = 0)
	private boolean allowShallowWaterSprint2(boolean original) {
		return original || !legacyRules().allowSwimming();
	}

	@ModifyReturnValue(method = "shouldStopRunSprinting", at = @At("RETURN"))
	private boolean blockSprintSneak(boolean original) {
		LocalPlayer self = (LocalPlayer)(Object)this;
		return original || (!legacyRules().allowSprintSneak() && self.isCrouching());
	}

	@ModifyReturnValue(method = "shouldStopSwimSprinting", at = @At("RETURN"))
	private boolean disableSwim(boolean original) {
		return original || !legacyRules().allowSwimming();
	}


}