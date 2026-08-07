package olinolivia.whops.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
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
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB boundingBox, List<VoxelShape> shapes) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private Level level;
    @Unique
    private static final ImmutableList<Direction.Axis> YXZ = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);

    @Unique
    private LegacyGameRules legacyRules() {
        return LegacyGameRules.get(level);
    }

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
        if (!legacyRules().xzFix()) return biasedCollideBoundingBox(source, movement, boundingBox, level, entityColliders);
        return Entity.collideBoundingBox(source, movement, boundingBox, level, entityColliders);
    }

    @Redirect(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 biasIfNoXZFix2(Vec3 movement, AABB boundingBox, List<VoxelShape> shapes) {
        if (!legacyRules().xzFix()) return biasedCollideWithShapes(movement, boundingBox, shapes);
        return collideWithShapes(movement, boundingBox, shapes);
    }

    @ModifyArg(method = "collide", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"))
    private double blipUp(double y, @Local(name = "movementStep") Vec3 movementStep) {
        if (legacyRules().allowBlipUp() && y < -movementStep.y) return -movementStep.y;
        return y;
    }

    @ModifyArg(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSwimming(Z)V"))
    private boolean blockSwim(boolean swimming) {
        return swimming && legacyRules().allowSwimming();
    }

    @Redirect(method = "checkSupportingBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;findSupportingBlock(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/Optional;"))
    private Optional<BlockPos> dumbOnBlock(Level instance, Entity entity, AABB aabb) {
        if (!LegacyGameRules.get(level).smartOnPosition()) {
            BlockPos pos = new BlockPos(
                    (int)(entity.position().x),
                    (int)(entity.position().y() - 1.0e-6),
                    (int)(entity.position().z)
            );
            return !level.getBlockState(pos).isAir() ? Optional.of(pos) : Optional.empty();
        }
        return instance.findSupportingBlock(entity, aabb);
    }

}
