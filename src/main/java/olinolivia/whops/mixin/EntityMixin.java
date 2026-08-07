package olinolivia.whops.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB boundingBox, List<VoxelShape> shapes) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    private static final ImmutableList<Direction.Axis> YXZ = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);

    @Unique
    private static Vec3 biasedCollideWithShapes(final Vec3 movement, final AABB boundingBox, final List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            return movement;
        }

        Vec3 resolvedMovement = Vec3.ZERO;

        for (Direction.Axis axis : YXZ) {
            double axisMovement = movement.get(axis);
            if (axisMovement != 0.0) {
                double collision = Shapes.collide(axis, boundingBox.move(resolvedMovement), shapes, axisMovement);
                resolvedMovement = resolvedMovement.with(axis, collision);
            }
        }

        return resolvedMovement;
    }

    @Unique
    private static Vec3 biasedCollideBoundingBox(
            final Entity source, final Vec3 movement, final AABB boundingBox, final Level level, final List<VoxelShape> entityColliders
    ) {
        List<VoxelShape> colliders = Entity.collectCollidersIgnoringWorldBorder(source, level, entityColliders, boundingBox.expandTowards(movement));
        return biasedCollideWithShapes(movement, boundingBox, colliders);
    }

    @Redirect(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 biasIfNoXZFix1(Entity source, Vec3 movement, AABB boundingBox, Level level, List<VoxelShape> entityColliders) {
        Entity self = (Entity)(Object)this;
        if (!LegacyGameRules.get(self.level()).xzFix()) return biasedCollideBoundingBox(source, movement, boundingBox, level, entityColliders);
        return Entity.collideBoundingBox(source, movement, boundingBox, level, entityColliders);
    }

    @Redirect(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 biasIfNoXZFix2(Vec3 movement, AABB boundingBox, List<VoxelShape> shapes) {
        Entity self = (Entity)(Object)this;
        if (!LegacyGameRules.get(self.level()).xzFix()) return biasedCollideWithShapes(movement, boundingBox, shapes);
        return collideWithShapes(movement, boundingBox, shapes);
    }

    @ModifyArg(method = "collide", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"))
    private double blipUp(double y, @Local(name = "movementStep") Vec3 movementStep) {
        Entity self = (Entity)(Object)this;
        if (LegacyGameRules.get(self.level()).allowBlipUp() && y < -movementStep.y) return -movementStep.y;
        return y;
    }

    @ModifyArg(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSwimming(Z)V"))
    private boolean blockSwim(boolean swimming) {
        Entity self = (Entity)(Object)this;
        return swimming && LegacyGameRules.get(self.level()).allowSwimming();
    }

}
