package olinolivia.whops.course;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ServerboundReturnPayload;
import olinolivia.whops.util.DimensionHelper;
import olinolivia.whops.util.EffectHelper;
import olinolivia.whops.util.SerializationHelper;

import java.util.*;

public record WhopsCheckpoint(
        Vec3 pos,
        Vec2 rot,
        ResourceKey<Level> dimension,
        Map<Holder<MobEffect>, MobEffectInstance> effects,
        List<AttributeInstance.Packed> attributes
) {

    public static WhopsCheckpoint fromPlayer(ServerPlayer player) {
        return fromPlayer(player, player.position(), new Vec2(player.getXRot(), player.getYRot()));
    }

    public static WhopsCheckpoint fromPlayer(ServerPlayer player, Vec3 pos, Vec2 rot) {
        return new WhopsCheckpoint(pos, rot, DimensionHelper.getDimension(player), EffectHelper.copyEffectMap(player.getActiveEffectsMap()), player.getAttributes().pack());
    }

    public static WhopsCheckpoint fromCourse(WhopsCourse course) {
        return new WhopsCheckpoint(course.startPos(), course.startRot(), course.startDimension(), Map.of(), List.of());
    }

    public void returnServer(ServerPlayer player) {
        player.teleportTo(Objects.requireNonNull(player.level().getServer().getLevel(dimension)), pos.x, pos.y, pos.z, Set.of(), rot.y, rot.x, true);
        EffectHelper.applyEffectMap(this.effects, player);
        player.getAttributes().apply(attributes);
    }

    public void matchReturnServer(ServerPlayer player) {
        player.setPos(pos);
        player.setXRot(rot.x);
        player.setYRot(rot.y);
        player.setDeltaMovement(Vec3.ZERO);
        EffectHelper.applyEffectMap(this.effects, player);
        player.getAttributes().apply(attributes);
    }

    public static final Codec<WhopsCheckpoint> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec3.CODEC.fieldOf("pos").forGetter(WhopsCheckpoint::pos),
            Vec2.CODEC.fieldOf("rot").forGetter(WhopsCheckpoint::rot),
            SerializationHelper.DIMENSION_CODEC.fieldOf("dimension").forGetter(WhopsCheckpoint::dimension),
            SerializationHelper.EFFECTS_CODEC.fieldOf("effects").forGetter(WhopsCheckpoint::effects),
            AttributeInstance.Packed.LIST_CODEC.fieldOf("attributes").forGetter(WhopsCheckpoint::attributes)
    ).apply(i, WhopsCheckpoint::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WhopsCheckpoint> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC, WhopsCheckpoint::pos,
            SerializationHelper.VEC2_STREAM_CODEC, WhopsCheckpoint::rot,
            SerializationHelper.DIMENSION_STREAM_CODEC, WhopsCheckpoint::dimension,
            SerializationHelper.EFFECTS_STREAM_CODEC, WhopsCheckpoint::effects,
            ByteBufCodecs.fromCodec(AttributeInstance.Packed.LIST_CODEC), WhopsCheckpoint::attributes,
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
            ServerPlayer player = context.player();
            WhopsCheckpoint checkpoint = player.getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
            if (checkpoint != null) checkpoint.matchReturnServer(player);
        });
    }

}

