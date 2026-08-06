package olinolivia.whops.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import olinolivia.whops.client.gamerule.ClientLegacyGameRuleTracker;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocalPlayer.class)
public abstract class ClientLocalPlayerMixin {

	@ModifyConstant(method = "isHorizontalCollisionMinor", constant = @Constant(doubleValue = (double) 0.13962634F))
	private double sprintThreshold(double constant) {
		return ClientLegacyGameRuleTracker.legacyGameRules.sprintLeniency();
	}

	@ModifyArg(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprintingPossible(Z)Z"), index = 0)
	private boolean allowShallowWaterSprint1(boolean original) {
		LocalPlayer self = (LocalPlayer)(Object)this;
		return original || !LegacyGameRules.get(self.level()).allowSwimming();
	}

	@ModifyArg(method = "shouldStopRunSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprintingPossible(Z)Z"), index = 0)
	private boolean allowShallowWaterSprint2(boolean original) {
		LocalPlayer self = (LocalPlayer)(Object)this;
		return original || !LegacyGameRules.get(self.level()).allowSwimming();
	}

	@ModifyReturnValue(method = "shouldStopRunSprinting", at = @At("RETURN"))
	private boolean blockSprintSneak(boolean original) {
		LocalPlayer self = (LocalPlayer)(Object)this;
		return original || (!LegacyGameRules.get(self.level()).allowSprintSneak() && self.isCrouching());
	}

	@ModifyReturnValue(method = "shouldStopSwimSprinting", at = @At("RETURN"))
	private boolean disableSwim(boolean original) {
		LocalPlayer self = (LocalPlayer)(Object)this;
		return original || !LegacyGameRules.get(self.level()).allowSwimming();
	}


}