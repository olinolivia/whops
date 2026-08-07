package olinolivia.whops.course;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;
import olinolivia.whops.Whops;

import java.util.Map;

public record CourseProgress(WhopsCheckpoint checkpoint, long timeElapsed, boolean completed) {

    public static CourseProgress fromPlayer(ServerPlayer player) {
        return new CourseProgress(player.getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT), player.getAttachedOrCreate(WhopsCourse.TIMER_ATTACHMENT), false);
    }

    public static CourseProgress fromCourse(WhopsCourse course) {
        return new CourseProgress(course.start(), 0, false);
    }

    public static final Codec<CourseProgress> CODEC = RecordCodecBuilder.create(i -> i.group(
            WhopsCheckpoint.CODEC.fieldOf("checkpoint").forGetter(CourseProgress::checkpoint),
            Codec.LONG.fieldOf("time_elapsed").forGetter(CourseProgress::timeElapsed),
            Codec.BOOL.fieldOf("completed").forGetter(CourseProgress::completed)
    ).apply(i, CourseProgress::new));

    public static final Codec<Map<String, CourseProgress>> WORLD_PROGRESS_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

    public static final AttachmentType<Map<String, CourseProgress>> WORLD_PROGRESS_ATTACHMENT = AttachmentRegistry.create(
            Whops.id("world_course_progress"),
            builder -> builder
                    .initializer(ImmutableMap::of)
                    .persistent(WORLD_PROGRESS_CODEC)
                    .copyOnDeath()
    );

}
