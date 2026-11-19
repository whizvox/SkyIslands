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
import me.whizvox.skyislands.core.command.island.TeleportSubCommand;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandInfo;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.util.IslandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static me.whizvox.skyislands.SkyIslands.cmdex;

@NotNullByDefault
public class IslandTeleportParametersArgumentType implements CustomArgumentType.Converted<TeleportSubCommand.Parameters, String> {

  private IslandTeleportParametersArgumentType() {}

  private static final List<String> SUGGESTIONS = List.of("t:", "w:");

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    String[] tokens = builder.getRemaining().split(" ");
    String prevToken = tokens.length > 1 ? tokens[tokens.length - 2].toLowerCase() : "";
    String currentToken = tokens[tokens.length - 1].toLowerCase();
    String first;
    if (tokens.length > 1) {
      first = String.join(" ", Arrays.copyOfRange(tokens, 0, tokens.length - 1)) + " ";
    } else {
      first = "";
    }
    if (builder.getRemainingLowerCase().startsWith("w:")) {
      String startWorldName = currentToken.substring(2);
      SkyIslands.getInstance().getIslandsWorldManager().getWorlds()
          .map(w -> w.getWorldKey().value())
          .filter(name -> name.startsWith(startWorldName))
          .sorted()
          .forEach(name -> builder.suggest(first + "w:" + name));
    } else if (currentToken.startsWith("t:")) {
      String startPlayerName = currentToken.substring(2);
      Bukkit.getOnlinePlayers().stream()
          .map(Player::getName)
          .filter(name -> name.startsWith(startPlayerName))
          .sorted()
          .forEach(name -> builder.suggest(first + "t:" + name));
    } else if (prevToken.isEmpty()) {
      //noinspection DataFlowIssue
      Arrays.stream(Bukkit.getOfflinePlayers())
          .map(OfflinePlayer::getName)
          .filter(name -> name.toLowerCase().startsWith(currentToken))
          .sorted()
          .forEach(builder::suggest);
      SUGGESTIONS.forEach(str -> {
        if (str.startsWith(currentToken)) {
          builder.suggest(str);
        }
      });
    } else if (!prevToken.startsWith("w:") && !prevToken.startsWith("t:") && tokens.length == 2) {
      IslandHelper.getOfflinePlayer(tokens[0]).ifPresent(owner -> {
        SkyIslands.getInstance().getIslandsWorldManager().getIslands(owner.getUniqueId())
            .map(info -> info.island().getOwnerKey().slot())
            .filter(slot -> String.valueOf(slot).startsWith(currentToken))
            .sorted()
            .forEach(slot -> builder.suggest(first + slot));
      });
    } else {
      SUGGESTIONS.forEach(str -> {
        if (currentToken.startsWith(str) && Arrays.stream(tokens).noneMatch(token -> token.startsWith(str))) {
          builder.suggest(first + str);
        }
      });
    }
    return builder.buildFuture();
  }

  @Override
  public TeleportSubCommand.Parameters convert(String nativeType) throws CommandSyntaxException {
    String ownerName = null;
    int ownerSlot = -1;
    String worldName = null;
    int worldIslandId = -1;
    String targetPlayerName = null;
    String[] tokens = nativeType.split(" ");
    char lastToken = '\0';
    // parse tokens
    for (String token : tokens) {
      if (token.startsWith("w:")) {
        worldName = token.substring(2);
        lastToken = 'W';
      } else if (token.startsWith("t:")) {
        targetPlayerName = token.substring(2);
        lastToken = 'T';
      } else if (lastToken == 'W') {
        // assume this is the island world ID
        try {
          worldIslandId = Integer.parseInt(token);
          lastToken = 'I';
        } catch (NumberFormatException ignored) {
          throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(token);
        }
      } else if (lastToken == '\0') {
        // assume this is the owner name
        ownerName = token;
        lastToken = 'O';
      } else if (lastToken == 'O') {
        // assume this is the owner's island ID
        try {
          ownerSlot = Integer.parseInt(token);
          lastToken = 'N';
        } catch (NumberFormatException e) {
          throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(token);
        }
      }
    }

    // determine island
    IslandInfo island;
    if (ownerName != null) {
      String finalOwnerName = ownerName;
      OfflinePlayer owner = IslandHelper.getOfflinePlayer(ownerName)
          .orElseThrow(() -> cmdex("argument.teleport.unknownOwner", Map.of("name", finalOwnerName)));
      if (ownerSlot == -1) {
        island = SkyIslands.getInstance().getIslandsWorldManager().getIslands(owner.getUniqueId())
            .min(Comparator.comparing(info -> info.island().getOwnerKey().slot()))
            .orElseThrow(() -> cmdex("argument.teleport.ownerHasNoIslands"));
      } else {
        island = SkyIslands.getInstance().getIslandsWorldManager().getIsland(new Island.OwnerKey(owner.getUniqueId(), ownerSlot))
            .orElseThrow(() -> cmdex("argument.teleport.notFound", Map.of("player",
                Optional.ofNullable(owner.getPlayer())
                    .map(Player::displayName)
                    .orElse(Component.text(finalOwnerName).color(NamedTextColor.YELLOW))
            )));
      }
    } else if (worldName != null) {
      IslandWorld islandWorld = IslandWorldArgumentType.islandWorld().convert(worldName);
      if (worldIslandId == -1) {
        island = islandWorld.getIslands().findFirst().orElseThrow(() -> cmdex("argument.teleport.worldHasNoIslands",
            Map.of("world", islandWorld.getWorldKey().value())));
      } else {
        island = islandWorld.getIsland(worldIslandId)
            .orElseThrow(() -> cmdex("argument.teleport.notFound"));
      }
    } else {
      throw cmdex("argument.teleport.notEnoughInfo");
    }

    // determine target player
    Player target;
    if (targetPlayerName == null) {
      target = null;
    } else {
      String finalTargetPlayerName = targetPlayerName;
      target = IslandHelper.getPlayer(targetPlayerName)
          .orElseThrow(() -> cmdex("argument.teleport.unknownTarget", Map.of("name", finalTargetPlayerName)));
    }

    return new TeleportSubCommand.Parameters(target, island.island());
  }

  @Override
  public ArgumentType<String> getNativeType() {
    return StringArgumentType.greedyString();
  }

  public static IslandTeleportParametersArgumentType parameters() {
    return new IslandTeleportParametersArgumentType();
  }

  public static TeleportSubCommand.Parameters getParameters(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, TeleportSubCommand.Parameters.class);
  }

}
