package me.whizvox.skyislands.core.command.island;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandWorldArgumentType;
import me.whizvox.skyislands.island.IslandInfo;
import me.whizvox.skyislands.island.IslandWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.*;

public class CreateSubCommand extends SubCommand {

  public static final Permission
      PERMISSION_SELF = perm("island.create"),
      PERMISSION_OTHERS = perm("island.create.others");

  public CreateSubCommand() {
    super("create", null);
  }

  public static int createIsland(CommandSender sender, Player owner, @Nullable IslandWorld world) throws CommandSyntaxException {
    if (world == null) {
      world = SkyIslands.getInstance().getIslandsWorldManager().getDefaultWorld()
          .orElseThrow(() -> {
            if (SkyIslands.getInstance().getIslandsWorldManager().getWorlds().findAny().isPresent()) {
              return cmdex("create.noDefaultWorld");
            }
            return cmdex("create.noWorlds");
          });
    }
    int maxIslands = SkyIslands.getInstance().getPluginConfig().getGroups().getMaxIslands(owner);
    if (maxIslands == 0) {
      throw cmdex("create.noIslandsAllowed");
    }
    List<IslandInfo> ownedIslands = getInstance().getIslandsWorldManager().getIslands(owner.getUniqueId()).toList();
    if (ownedIslands.size() >= maxIslands) {
      throw cmdex("create.maxPlayerIslands", Map.of("value", maxIslands));
    }
    int nextSlot = 1;
    if (!ownedIslands.isEmpty()) {
      boolean keepSearching = true;
      searchLoop:
      while (keepSearching) {
        for (IslandInfo info : ownedIslands) {
          if (info.island().getOwnerKey().slot() == nextSlot) {
            nextSlot++;
            continue searchLoop;
          }
        }
        keepSearching = false;
      }
    }
    world.createIsland(owner, nextSlot).ifPresentOrElse(_ -> {
      if (sender != owner) {
        sender.sendMessage(tl("create.success.self"));
      } else {
        sender.sendMessage(tl("create.success.other"));
      }
    }, () -> sender.sendMessage(tl("create.maxWorldIslands")));
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.argument("world", IslandWorldArgumentType.islandWorld())
            .then(Commands.argument("other", ArgumentTypes.player())
                .requires(source -> hasPermission(source, PERMISSION_OTHERS))
                .executes(context -> createIsland(
                    context.getSource().getSender(),
                    context.getArgument("other", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst(),
                    IslandWorldArgumentType.getIslandWorld(context, "world")
                ))
            )
            .requires(source -> hasPermission(source, PERMISSION_SELF))
            .executes(context -> createIsland(
                context.getSource().getSender(),
                context.getSource().getPlayerOrThrow(),
                IslandWorldArgumentType.getIslandWorld(context, "world")
            ))
        )
        .requires(source -> hasPermission(source, PERMISSION_SELF))
        .executes(context -> createIsland(
            context.getSource().getSender(),
            context.getSource().getPlayerOrThrow(),
            null
        ));
  }

}
