package me.whizvox.skyislands.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.island.IslandGroup;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static me.whizvox.skyislands.SkyIslands.cmdex;

@NotNullByDefault
public class IslandGroupArgumentType implements CustomArgumentType.Converted<IslandGroup, String> {

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    SkyIslands.getInstance().getPluginConfig().getGroups().getGroups().stream()
        .map(group -> group.name)
        .sorted()
        .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
        .forEach(builder::suggest);
    return builder.buildFuture();
  }

  @Override
  public IslandGroup convert(String nativeType) throws CommandSyntaxException {
    return SkyIslands.getInstance().getPluginConfig().getGroups().getGroup(nativeType)
        .orElseThrow(() -> cmdex("argument.islandGroup.unknown", Map.of("name", nativeType)));
  }

  @Override
  public ArgumentType<String> getNativeType() {
    return StringArgumentType.word();
  }

  public static IslandGroupArgumentType islandGroup() {
    return new IslandGroupArgumentType();
  }

  public static IslandGroup getIslandGroup(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IslandGroup.class);
  }

}
