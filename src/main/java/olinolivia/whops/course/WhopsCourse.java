package olinolivia.whops.course;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.Whops;
import olinolivia.whops.util.DimensionHelper;
import olinolivia.whops.util.SerializationHelper;

import static olinolivia.whops.course.CourseHelper.*;

public record WhopsCourse(Vec3 startPos, Vec2 startRot, ResourceKey<Level> startDimension) {

    public static WhopsCourse fromPlayer(ServerPlayer player) {
        return new WhopsCourse(player.position(), new Vec2(player.getXRot(), player.getYRot()), DimensionHelper.getDimension(player));
    }

    public static final Codec<WhopsCourse> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec3.CODEC.fieldOf("start_pos").forGetter(WhopsCourse::startPos),
            Vec2.CODEC.fieldOf("start_rot").forGetter(WhopsCourse::startRot),
            SerializationHelper.DIMENSION_CODEC.fieldOf("start_dimension").forGetter(WhopsCourse::startDimension)
    ).apply(i, WhopsCourse::new));

    public static final AttachmentType<String> CURRENT_COURSE_ATTACHMENT = AttachmentRegistry.create(
            Whops.id("current_course"),
            builder -> builder
                    .syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.targetOnly())
                    .persistent(Codec.STRING)
                    .copyOnDeath()
    );

    public static final AttachmentType<Long> TIMER_ATTACHMENT = AttachmentRegistry.create(
            Whops.id("timer"),
            builder -> builder
                    .initializer(() -> (long) 0)
                    .persistent(Codec.LONG)
                    .copyOnDeath()
    );

    static {

        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            for (ServerPlayer player : level.getPlayers(CourseHelper::isInCourse)) incrementTimer(player);
        });

        // sync
        ServerPlayerEvents.JOIN.register(player -> setTimer(player, getTimerServer(player)));

    }

    public static void init() {}

}
