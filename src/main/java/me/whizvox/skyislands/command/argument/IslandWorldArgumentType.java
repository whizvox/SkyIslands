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
import me.whizvox.skyislands.island.IslandWorld;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static me.whizvox.skyislands.SkyIslands.cmdex;

@NotNullByDefault
public class IslandWorldArgumentType implements CustomArgumentType.Converted<IslandWorld, String> {

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    SkyIslands.getInstance().getIslandsWorldManager().getWorlds()
        .filter(world -> world.getWorld().key().toString().toLowerCase().startsWith(builder.getRemainingLowerCase()))
        .map(world -> world.getWorld().key())
        .sorted(Key::compareTo)
        .forEach(key -> builder.suggest(key.toString()));
    return builder.buildFuture();
  }

  @Override
  public IslandWorld convert(String nativeType) throws CommandSyntaxException {
    Key key = NamespacedKey.fromString(nativeType, SkyIslands.getInstance());
    if (key != null) {
      return SkyIslands.getInstance().getIslandsWorldManager().getIslandWorld(key)
          .orElseThrow(() -> cmdex("argument.skyIslandWorld.invalid", Map.of("value", key)));
    }
    throw cmdex("argument.invalidWorldName");
  }

  @Override
  public ArgumentType<String> getNativeType() {
    return StringArgumentType.word();
  }

  public static IslandWorldArgumentType islandWorld() {
    return new IslandWorldArgumentType();
  }

  public static IslandWorld getIslandWorld(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IslandWorld.class);
  }

}
