package olinolivia.whops.course;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ServerboundReturnPayload;
import olinolivia.whops.util.DimensionHelper;
import olinolivia.whops.util.SerializationHelper;

import java.util.Objects;
import java.util.Set;

public record WhopsCheckpoint(Vec3 pos, Vec2 rot, ResourceKey<Level> dimension) {

    public static WhopsCheckpoint fromPlayer(ServerPlayer player) {
        return new WhopsCheckpoint(player.position(), new Vec2(player.getXRot(), player.getYRot()), DimensionHelper.getDimension(player));
    }

    public static WhopsCheckpoint fromCourse(WhopsCourse course) {
        return new WhopsCheckpoint(course.startPos(), course.startRot(), course.startDimension());
    }

    public void returnServer(ServerPlayer player) {
        player.teleportTo(Objects.requireNonNull(player.level().getServer().getLevel(dimension)), pos.x, pos.y, pos.z, Set.of(), rot.y, rot.x, true);
    }

    public static final Codec<WhopsCheckpoint> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec3.CODEC.fieldOf("pos").forGetter(WhopsCheckpoint::pos),
            Vec2.CODEC.fieldOf("rot").forGetter(WhopsCheckpoint::rot),
            SerializationHelper.DIMENSION_CODEC.fieldOf("dimension").forGetter(WhopsCheckpoint::dimension)
    ).apply(i, WhopsCheckpoint::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WhopsCheckpoint> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC, WhopsCheckpoint::pos,
            SerializationHelper.VEC2_STREAM_CODEC, WhopsCheckpoint::rot,
            SerializationHelper.DIMENSION_STREAM_CODEC, WhopsCheckpoint::dimension,
            WhopsCheckpoint::new
    );

    public static final AttachmentType<WhopsCheckpoint> CHECKPOINT_ATTACHMENT = AttachmentRegistry.create(
            Whops.id("checkpoint"),
            builder -> builder
                    .persistent(CODEC)
                    .syncWith(STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
                    .copyOnDeath()
    );

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundReturnPayload.TYPE, (_, context) -> {
            WhopsCheckpoint checkpoint = context.player().getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
            if (checkpoint != null) {
                context.player().setPos(checkpoint.pos);
                context.player().setXRot(checkpoint.rot.x);
                context.player().setYRot(checkpoint.rot.y);
                context.player().setDeltaMovement(Vec3.ZERO);
            }
        });
    }

}

