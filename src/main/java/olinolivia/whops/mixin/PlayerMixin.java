package olinolivia.whops.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {

    private PlayerMixin(EntityType<?> type, Level level) {super(type, level);}

    @Unique
    private LegacyGameRules legacyRules() {
        return LegacyGameRules.get(level());
    }

    @ModifyExpressionValue(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;maxUpStep()F"))
    private float stepDownDistance(float original) {
        if (!legacyRules().stepHeightLedges()) return 0.99f;
        return original;
    }

}
