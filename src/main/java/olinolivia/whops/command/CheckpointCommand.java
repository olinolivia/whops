package olinolivia.whops.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import olinolivia.whops.networking.ClientboundCheckpointFeedbackPayload;
import olinolivia.whops.util.DimensionHelper;

import static olinolivia.whops.command.CommandHelper.*;
import static olinolivia.whops.course.CourseHelper.*;

public class CheckpointCommand {

    public static final Command<CommandSourceStack> QUICK_SAVE = context -> {
        ServerPlayer player = getPlayer(context);
        setCheckpoint(player, WhopsCheckpoint.fromPlayer(player));
        context.getSource().sendSuccess(() -> Component.literal("Saved position as checkpoint"), false);
        return 0;
    };

    public static final Command<CommandSourceStack> CUSTOM_SAVE = context -> {
        ServerPlayer player = getPlayer(context);
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        Vec2 rot = Vec2Argument.getVec2(context, "rot");
        boolean feedback = BoolArgumentType.getBool(context, "feedback");
        //noinspection SuspiciousNameCombination
        setCheckpoint(player, WhopsCheckpoint.fromPlayer(player, pos, new Vec2(rot.y, rot.x), DimensionHelper.getDimension(player)));
        if (feedback) ServerPlayNetworking.send(player, new ClientboundCheckpointFeedbackPayload(true));
        context.getSource().sendSuccess(() -> Component.literal("Saved position as checkpoint"), false);
        return 0;
    };

    public static final Command<CommandSourceStack> CLEAR = context -> {
        ServerPlayer player = getPlayer(context);
        setCheckpoint(player, null);
        context.getSource().sendSuccess(() -> Component.literal("Cleared checkpoint"), false);
        return 0;
    };

    public static final Command<CommandSourceStack> LOAD = context -> {
        ServerPlayer player = getPlayer(context);
        WhopsCheckpoint checkpoint = getCheckpoint(player);
        if (checkpoint != null) {
            checkpoint.returnServer(player);
            context.getSource().sendSuccess(() -> Component.literal("Loaded position from checkpoint"), false);
        }
        else context.getSource().sendFailure(Component.literal("No checkpoint to return to!"));
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
                                .then(Commands.argument("feedback", BoolArgumentType.bool()))
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
