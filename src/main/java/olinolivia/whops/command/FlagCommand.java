package olinolivia.whops.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import olinolivia.whops.command.argument.FlagArgument;
import olinolivia.whops.flag.WhopsFlags;

import static olinolivia.whops.command.CommandHelper.*;

public class FlagCommand {

    public static final Command<CommandSourceStack> SET = context -> {
        ServerPlayer player = getPlayer(context);
        String flagName = context.getArgument("flag", String.class);
        boolean flagValue = BoolArgumentType.getBool(context, "value");
        try {
            WhopsFlags.set(player, flagName, flagValue);
            context.getSource().sendSuccess(() -> Component.literal("Updated flag " + flagName + " to " + flagValue), true);
        } catch (NoSuchFieldException ignored) {
            context.getSource().sendFailure(Component.literal("That isn't a flag!"));
        }
        return 0;
    };

    static {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(Commands.literal("flag")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                        .then(Commands.argument("flag", new FlagArgument())
                        .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(SET)
                )))
        );
    }

    public static void init() {}

}
