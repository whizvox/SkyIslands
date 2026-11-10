package me.whizvox.skyislands.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import it.unimi.dsi.fastutil.Pair;
import me.whizvox.skyislands.util.StringHelper;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static me.whizvox.skyislands.SkyIslands.cmdex;

@NotNullByDefault
public class EnumArgumentType<T extends Enum<T>> implements CustomArgumentType.Converted<T, String> {

  private final TreeMap<String, Pair<String, T>> map;

  public EnumArgumentType(Class<T> clazz) {
    map = new TreeMap<>();
    for (T value : clazz.getEnumConstants()) {
      String lcc = StringHelper.snakeCaseToLowerCamelCase(value.name());
      map.put(lcc.toLowerCase(), Pair.of(lcc, value));
    }
  }

  @Override
  public ArgumentType<String> getNativeType() {
    return StringArgumentType.word();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    map.forEach((name, value) -> {
      if (name.startsWith(builder.getRemainingLowerCase())) {
        builder.suggest(value.left());
      }
    });
    return builder.buildFuture();
  }

  @Override
  public T convert(String nativeType) throws CommandSyntaxException {
    var value = map.get(nativeType.toLowerCase());
    if (value != null) {
      return value.right();
    }
    throw cmdex("argument.unknownEnumValue", Map.of("values", map.values().stream().map(Pair::left).collect(Collectors.joining(", "))));
  }

}
