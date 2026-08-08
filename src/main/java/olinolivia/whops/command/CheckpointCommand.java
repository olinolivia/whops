package olinolivia.whops.command;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.course.WhopsCheckpoint;

public class CheckpointCommand {

    public static final Command<CommandSourceStack> QUICK_SAVE = context -> {
        if (context.getSource().getEntityOrException() instanceof ServerPlayer player) {
            player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, WhopsCheckpoint.fromPlayer(player));
            context.getSource().sendSuccess(() -> Component.literal("Saved position as checkpoint"), false);
        } else {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
        }
        return 0;
    };

    public static final Command<CommandSourceStack> CUSTOM_SAVE = context -> {
        if (context.getSource().getEntityOrException() instanceof ServerPlayer player) {
            Vec3 pos = Vec3Argument.getVec3(context, "pos");
            Vec2 rot = Vec2Argument.getVec2(context, "rot");
            //noinspection SuspiciousNameCombination
            player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, WhopsCheckpoint.fromPlayer(player, pos, new Vec2(rot.y, rot.x), context.getSource().getLevel().dimension()));
            context.getSource().sendSuccess(() -> Component.literal("Saved position as checkpoint"), false);
        } else {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
        }
        return 0;
    };

    public static final Command<CommandSourceStack> CLEAR = context -> {
        if (context.getSource().getEntityOrException() instanceof ServerPlayer player) {
            player.setAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT, null);
            context.getSource().sendSuccess(() -> Component.literal("Cleared checkpoint"), false);
        } else {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
        }
        return 0;
    };

    public static final Command<CommandSourceStack> LOAD = context -> {
        if (context.getSource().getEntityOrException() instanceof ServerPlayer player) {
            WhopsCheckpoint checkpoint = player.getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
            if (checkpoint != null) {
                checkpoint.returnServer(player);
                context.getSource().sendSuccess(() -> Component.literal("Loaded position from checkpoint"), false);
            }
            else {
                context.getSource().sendFailure(Component.literal("No checkpoint to return to!"));
            }
        } else {
            context.getSource().sendFailure(Component.literal("You aren't a player!"));
        }
        return 0;
    };

    static {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(Commands.literal("checkpoint")
                        .then(Commands.literal("save")
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                                .executes(QUICK_SAVE)
                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                .then(Commands.argument("rot", Vec2Argument.vec2())
                                .executes(CUSTOM_SAVE)
                        )))
                        .then(Commands.literal("clear")
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                                .executes(CLEAR)
                        )
                        .then(Commands.literal("load").executes(LOAD))
                )
        );
    }

    public static void init() {}

}
