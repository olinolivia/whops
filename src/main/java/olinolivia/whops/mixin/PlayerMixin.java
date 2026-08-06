package olinolivia.whops.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @ModifyExpressionValue(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;maxUpStep()F"))
    private float stepDownDistance(float original) {
        Player self = (Player)(Object)this;
        if (!LegacyGameRules.get(self.level()).stepHeightLedges()) return 0.99f;
        return original;
    }

}
