package olinolivia.whops.checkpoint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ServerboundReturnPayload;

import java.util.Set;

public record WhopsCheckpoint(Vec3 pos, Vec2 rot) {

    public static WhopsCheckpoint fromPlayer(ServerPlayer player) {
        return new WhopsCheckpoint(player.position(), new Vec2(player.getXRot(), player.getYRot()));
    }

    public void returnServer(ServerPlayer player) {
        player.teleportTo(player.level(), pos.x, pos.y, pos.z, Set.of(), rot.y, rot.x, true);
    }

    public static final Codec<WhopsCheckpoint> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec3.CODEC.fieldOf("pos").forGetter(WhopsCheckpoint::pos),
            Vec2.CODEC.fieldOf("rot").forGetter(WhopsCheckpoint::rot)
    ).apply(i, WhopsCheckpoint::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Vec2> VEC2_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, vec2 -> vec2.x,
            ByteBufCodecs.FLOAT, vec2 -> vec2.y,
            Vec2::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, WhopsCheckpoint> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC, WhopsCheckpoint::pos,
            VEC2_STREAM_CODEC, WhopsCheckpoint::rot,
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

