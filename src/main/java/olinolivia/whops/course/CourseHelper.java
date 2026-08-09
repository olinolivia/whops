package olinolivia.whops.course;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelData;
import olinolivia.whops.networking.ClientboundReplaceTimerPayload;

import java.util.Map;
import java.util.Set;

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

    public static boolean isFinished(ServerPlayer player) {
        return player.getAttachedOrCreate(WhopsCourse.FINISHED_ATTACHMENT);
    }

    // attachment modifiers

    public static void setCourseProgress(ServerPlayer player, String targetCourseName, CourseProgress progress) {
        Map<String, CourseProgress> previous = player.getAttachedOrCreate(CourseProgress.WORLD_PROGRESS_ATTACHMENT);
        ImmutableMap.Builder<String, CourseProgress> newProgressBuilder = ImmutableMap.builder();
        for (String courseName : previous.keySet()) if (!courseName.equals(targetCourseName)) newProgressBuilder.put(courseName, previous.get(courseName));
        newProgressBuilder.put(targetCourseName, progress);
        player.setAttached(CourseProgress.WORLD_PROGRESS_ATTACHMENT, newProgressBuilder.build());
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
            if (newProgress == null) newProgress = CourseProgress.fromCourse(courseData.getCourse(newCourseName), false);
            setCheckpoint(player, newProgress.checkpoint());
            newProgress.checkpoint().returnServer(player);
            setTimer(player, newProgress.timeElapsed());
            setFinished(player, newProgress.finished());
        } else {
            player.setAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT, null);
            setCheckpoint(player, null);
            LevelData.RespawnData respawnData = player.level().getRespawnData();
            ServerLevel level = player.level().getServer().getLevel(respawnData.globalPos().dimension());
            if (level != null) player.teleportTo(level, respawnData.pos().getX(), respawnData.pos().getY(), respawnData.pos().getZ(), Set.of(), respawnData.pitch(), respawnData.yaw(), true);
        }

    }

    public static void setTimer(ServerPlayer player, long timer) {
        player.setAttached(WhopsCourse.TIMER_ATTACHMENT, timer);
        ServerPlayNetworking.send(player, new ClientboundReplaceTimerPayload(timer));
    }

    public static void incrementTimer(ServerPlayer player) {
        player.modifyAttached(WhopsCourse.TIMER_ATTACHMENT, timer -> timer+1);
    }

    public static void setFinished(ServerPlayer player, boolean finished) {
        player.setAttached(WhopsCourse.FINISHED_ATTACHMENT, finished);
    }

    // misc

    public static boolean isInCourse(ServerPlayer player) {
        String courseName = getCurrentCourseName(player);
        return courseName != null && WorldCourseData.get(player.level()).courseExists(courseName);
    }

}
