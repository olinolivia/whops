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

public abstract class CourseCommand {

    public static final Command<CommandSourceStack> PLAY = context -> {
        if (!(context.getSource().getEntityOrException() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
            return 0;
        }
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (!courseData.courseExists(courseName)) {
            context.getSource().sendFailure(Component.literal("That isn't a course!"));
            return 0;
        }
        WhopsCourse.switchCourses(player, courseName);
        context.getSource().sendSuccess(() -> Component.literal("Now playing " + courseName), true);
        return 0;
    };

    public static final Command<CommandSourceStack> RESTART = context -> {
        if (!(context.getSource().getEntityOrException() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
            return 0;
        }
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        String courseName = player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT);
        if (courseName == null || !courseData.courseExists(courseName)) {
            context.getSource().sendFailure(Component.literal("You aren't in a course!"));
            return 0;
        }
        WhopsCheckpoint checkpoint = courseData.getCourse(courseName).start();
        player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, checkpoint);
        checkpoint.returnServer(player);
        return 0;
    };

    public static final Command<CommandSourceStack> CREATE = context -> {
        if (!(context.getSource().getEntityOrException() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
            return 0;
        }
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (courseData.courseExists(courseName)) {
            context.getSource().sendFailure(Component.literal("That is already a course!"));
            return 0;
        }
        courseData.addCourse(courseName, new WhopsCourse(WhopsCheckpoint.fromPlayer(player)));
        context.getSource().sendSuccess(() -> Component.literal("Created course " + courseName), true);
        return 0;
    };

    public static final Command<CommandSourceStack> REMOVE = context -> {
        String courseName = StringArgumentType.getString(context, "course");
        WorldCourseData courseData = WorldCourseData.get(context.getSource().getLevel());
        if (!courseData.courseExists(courseName)) {
            context.getSource().sendFailure(Component.literal("That isn't a course!"));
            return 0;
        }
        courseData.removeCourse(courseName);
        context.getSource().sendSuccess(() -> Component.literal("Removed course " + courseName), true);
        return 0;
    };

    static {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(Commands.literal("course")
                        .then(Commands.literal("play").then(Commands.argument("course", StringArgumentType.string()).suggests(new CourseSuggestionProvider()).executes(PLAY)))
                        .then(Commands.literal("restart").executes(RESTART))
                        .then(Commands.literal("create").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)).then(Commands.argument("course", StringArgumentType.string()).executes(CREATE)))
                        .then(Commands.literal("remove").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)).then(Commands.argument("course", StringArgumentType.string()).suggests(new CourseSuggestionProvider()).executes(REMOVE)))
                )
        );
    }

    public static void init() {}

}
