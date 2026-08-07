package olinolivia.whops.course;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ClientboundReplaceTimerPayload;

import java.util.Map;

public record WhopsCourse(WhopsCheckpoint start) {

    public static final Codec<WhopsCourse> CODEC = RecordCodecBuilder.create(i -> i.group(
            WhopsCheckpoint.CODEC.fieldOf("start").forGetter(WhopsCourse::start)
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

    public static boolean isInCourse(ServerPlayer player) {
        String courseName = player.getAttached(CURRENT_COURSE_ATTACHMENT);
        return courseName != null && WorldCourseData.get(player.level()).courseExists(courseName);
    }

    public static void switchCourses(ServerPlayer player, String newCourseName) {
        WorldCourseData courseData = WorldCourseData.get(player.level());
        String previousCourseName = player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT);
        CourseProgress previousProgress = CourseProgress.fromPlayer(player);
        Map<String, CourseProgress> previousWorldProgress = player.getAttachedOrCreate(CourseProgress.WORLD_PROGRESS_ATTACHMENT);

        if (courseData.courseExists(previousCourseName)) {
            ImmutableMap.Builder<String, CourseProgress> newWorldProgress = new ImmutableMap.Builder<>();
            for (String cn : previousWorldProgress.keySet())
                if (!cn.equals(previousCourseName)) newWorldProgress.put(cn, previousWorldProgress.get(cn));
            if (previousCourseName != null) newWorldProgress.put(previousCourseName, previousProgress);
            player.setAttached(CourseProgress.WORLD_PROGRESS_ATTACHMENT, newWorldProgress.build());
        }

        if (newCourseName != null) {
            player.setAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT, newCourseName);
            CourseProgress newProgress = previousWorldProgress.get(newCourseName);
            newProgress = newProgress != null ? newProgress : CourseProgress.fromCourse(courseData.getCourse(newCourseName));

            player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, newProgress.checkpoint());
            player.setAttached(WhopsCourse.TIMER_ATTACHMENT, newProgress.timeElapsed());

            newProgress.checkpoint().returnServer(player);
            ServerPlayNetworking.send(player, new ClientboundReplaceTimerPayload(newProgress.timeElapsed()));
        } else {
            player.setAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT, null);
        }

    }

    static {

        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            for (ServerPlayer player : level.getPlayers(WhopsCourse::isInCourse))
                player.setAttached(TIMER_ATTACHMENT, player.getAttachedOrCreate(TIMER_ATTACHMENT) + 1);
        });

        ServerPlayerEvents.JOIN.register(player ->
                ServerPlayNetworking.send(player, new ClientboundReplaceTimerPayload(player.getAttachedOrCreate(TIMER_ATTACHMENT)))
        );

    }

    public static void init() {}

}
