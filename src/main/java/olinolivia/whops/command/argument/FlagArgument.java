package olinolivia.whops.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import olinolivia.whops.Whops;
import olinolivia.whops.flag.WhopsFlags;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class FlagArgument implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String flagName = reader.readUnquotedString();
        if (!Arrays.asList(WhopsFlags.flagNames).contains(flagName)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid flag name");
        return flagName;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String flagName : WhopsFlags.flagNames) builder.suggest(flagName);
        return builder.buildFuture();
    }

    static {

        ArgumentTypeRegistry.registerArgumentType(
                Whops.id("flag"),
                FlagArgument.class,
                SingletonArgumentInfo.contextFree(FlagArgument::new)
        );

    }

    public static void init() {}

}
