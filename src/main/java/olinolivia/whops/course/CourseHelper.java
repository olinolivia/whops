package olinolivia.whops.course;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import olinolivia.whops.networking.ClientboundReplaceTimerPayload;

public abstract class CourseHelper {

    // attachment getters

    public static CourseProgress getCourseProgress(Player player, String courseName) {
        return player.getAttachedOrCreate(CourseProgress.WORLD_PROGRESS_ATTACHMENT).get(courseName);
    }

    public static boolean hasCheckpoint(Player player) {
        return player.hasAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
    }

    public static WhopsCheckpoint getCheckpoint(Player player) {
        return player.getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
    }

    public static String getCurrentCourseName(Player player) {
        return player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT);
    }

    public static long getTimerServer(ServerPlayer player) {
        return player.getAttachedOrCreate(WhopsCourse.TIMER_ATTACHMENT);
    }

    // attachment modifiers

    public static void setCourseProgress(ServerPlayer player, String targetCourseName, CourseProgress progress) {
        player.modifyAttached(CourseProgress.WORLD_PROGRESS_ATTACHMENT, map -> {
            ImmutableMap.Builder<String, CourseProgress> newProgressBuilder = ImmutableMap.builder();
            for (String courseName : map.keySet()) if (!courseName.equals(targetCourseName)) newProgressBuilder.put(courseName, map.get(courseName));
            newProgressBuilder.put(targetCourseName, progress);
            return newProgressBuilder.build();
        });
    }

    public static void setCheckpoint(ServerPlayer player, WhopsCheckpoint checkpoint) {
        player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, checkpoint);
    }

    public static void switchCourses(ServerPlayer player, String newCourseName) {
        WorldCourseData courseData = WorldCourseData.get(player.level());
        String previousCourseName = getCurrentCourseName(player);

        // save previous
        if (hasCheckpoint(player)) {
            CourseProgress savedProgress = CourseProgress.fromPlayer(player);
            if (isInCourse(player)) setCourseProgress(player, previousCourseName, savedProgress);
        }

        // switch
        if (newCourseName != null && courseData.courseExists(newCourseName)) {
            player.setAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT, newCourseName);
            CourseProgress newProgress = getCourseProgress(player, newCourseName);
            if (newProgress == null) newProgress = CourseProgress.fromCourse(courseData.getCourse(newCourseName));
            setCheckpoint(player, newProgress.checkpoint());
            newProgress.checkpoint().returnServer(player);
            setTimer(player, newProgress.timeElapsed());
        } else {
            player.setAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT, null);
        }

    }

    public static void setTimer(ServerPlayer player, long timer) {
        player.setAttached(WhopsCourse.TIMER_ATTACHMENT, timer);
        ServerPlayNetworking.send(player, new ClientboundReplaceTimerPayload(timer));
    }

    public static void incrementTimer(ServerPlayer player) {
        player.modifyAttached(WhopsCourse.TIMER_ATTACHMENT, timer -> timer+1);
    }

    // misc

    public static boolean isInCourse(ServerPlayer player) {
        String courseName = getCurrentCourseName(player);
        return courseName != null && WorldCourseData.get(player.level()).courseExists(courseName);
    }

}
