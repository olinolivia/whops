package olinolivia.whops.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import olinolivia.whops.command.suggestion.CourseSuggestionProvider;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.course.WhopsCourse;
import olinolivia.whops.course.WorldCourseData;

import static olinolivia.whops.command.CommandHelper.*;
import static olinolivia.whops.course.CourseHelper.*;

public abstract class CourseCommand {

    public static final Command<CommandSourceStack> PLAY = context -> {
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (!courseData.courseExists(courseName)) return error(context, "That isn't a course!");
        switchCourses(getPlayer(context), courseName);
        return 0;
    };

    public static final Command<CommandSourceStack> RESTART = context -> {
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        ServerPlayer player = getPlayer(context);
        String courseName = getCurrentCourseName(player);
        if (courseName == null) return error(context, "You aren't in a course!");
        if (!courseData.courseExists(courseName)) return error(context, "You aren't in a valid course!");
        WhopsCheckpoint checkpoint = WhopsCheckpoint.fromCourse(courseData.getCourse(courseName));
        setCheckpoint(player, checkpoint);
        checkpoint.returnServer(player);
        return 0;
    };

    public static final Command<CommandSourceStack> LEAVE = context -> {
        ServerPlayer player = getPlayer(context);
        String courseName = player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT);
        if (courseName == null) return error(context, "You aren't in a course!");
        switchCourses(player, null);
        return 0;
    };

    public static final Command<CommandSourceStack> CREATE = context -> {
        ServerPlayer player = getPlayer(context);
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (courseData.courseExists(courseName)) return error(context, "That is already a course!");
        courseData.addCourse(courseName, WhopsCourse.fromPlayer(player));
        context.getSource().sendSuccess(() -> Component.literal("Created course " + courseName), true);
        return 0;
    };

    public static final Command<CommandSourceStack> REMOVE = context -> {
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (!courseData.courseExists(courseName)) return error(context, "That isn't a course!");
        courseData.removeCourse(courseName);
        context.getSource().sendSuccess(() -> Component.literal("Removed course " + courseName), true);
        return 0;
    };

    static {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(Commands.literal("course")
                        .then(Commands.literal("play").then(Commands.argument("course", StringArgumentType.string()).suggests(new CourseSuggestionProvider()).executes(PLAY)))
                        .then(Commands.literal("restart").executes(RESTART))
                        .then(Commands.literal("leave").executes(LEAVE))
                        .then(Commands.literal("create").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)).then(Commands.argument("course", StringArgumentType.string()).executes(CREATE)))
                        .then(Commands.literal("remove").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)).then(Commands.argument("course", StringArgumentType.string()).suggests(new CourseSuggestionProvider()).executes(REMOVE)))
                )
        );
    }

    public static void init() {}

}
