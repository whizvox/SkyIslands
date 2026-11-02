package me.whizvox.skyislands.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.skyislands.command.util.TranslatableCommandExceptionType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public class OfflinePlayerArgumentType implements CustomArgumentType.Converted<OfflinePlayer, String> {

    private OfflinePlayerArgumentType() {
    }

    public static OfflinePlayerArgumentType offlinePlayer() {
        return new OfflinePlayerArgumentType();
    }

    public static OfflinePlayer get(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, OfflinePlayer.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName() != null && op.getName().toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(op.getName());
            }
        }
        return builder.buildFuture();
    }

    @Override
    public OfflinePlayer convert(String nativeType) throws CommandSyntaxException {
        OfflinePlayer value;
        // might be a UUID
        if (nativeType.length() == 36) {
            try {
                UUID id = UUID.fromString(nativeType);
                value = Bukkit.getOfflinePlayer(id);
                if (value.getName() != null) {
                    return value;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        value = Bukkit.getOfflinePlayerIfCached(nativeType);
        if (value != null) {
            return value;
        }
        throw new TranslatableCommandExceptionType("argument.player.unknown").create();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

}
