package olinolivia.whops.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import olinolivia.whops.course.WorldCourseData;

import java.util.concurrent.CompletableFuture;

public class CourseSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    private static String quote(String string) {
        return "\"" + string.replace("\"", "\\\"") + "\"";
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (String courseName : context.getSource().getLevel().getDataStorage().computeIfAbsent(WorldCourseData.TYPE).getCourseNames())
            builder.suggest(quote(courseName));
        return builder.buildFuture();
    }

}
