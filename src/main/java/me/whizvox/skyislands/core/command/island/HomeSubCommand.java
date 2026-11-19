package me.whizvox.skyislands.core.command.island;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.island.Island;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;

import static me.whizvox.skyislands.SkyIslands.cmdex;
import static me.whizvox.skyislands.SkyIslands.perm;

@NotNullByDefault
public class HomeSubCommand extends SubCommand {

  public static final Permission
      PERMISSION_SELF = perm("home"),
      PERMISSION_OTHERS = perm("home.others");

  public HomeSubCommand() {
    super("home");
  }

  /**
   * Teleports a target player to their home island.
   * @param sender The entity executing the command
   * @param slot The owner's island slot number. If this is -1, the target's first island will be selected
   * @param target The player who will be teleported to their island. Could be the same as <code>sender</code>
   */
  public static int teleportHome(CommandSender sender, int slot, Player target) throws CommandSyntaxException {
    Island island;
    if (slot == -1) {
      island = SkyIslands.getInstance().getIslandsWorldManager().getFirstIsland(target.getUniqueId())
          .orElseThrow(() -> sender == target ? cmdex("home.noIslands") : cmdex("home.noIslands.other"))
          .island();
    } else {
      island = SkyIslands.getInstance().getIslandsWorldManager().getIsland(new Island.OwnerKey(target.getUniqueId(), slot))
          .orElseThrow(() -> cmdex("argument.teleport.notFound"))
          .island();
    }
    return TeleportSubCommand.teleportToIsland(sender, new TeleportSubCommand.Parameters(target, island));
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.argument("slot", IntegerArgumentType.integer(1))
            .then(Commands.argument("target", ArgumentTypes.player())
                .requires(source -> hasPermission(source, PERMISSION_OTHERS))
                .executes(context -> teleportHome(
                    context.getSource().getSender(),
                    IntegerArgumentType.getInteger(context, "slot"),
                    context.getArgument("target", PlayerSelectorArgumentResolver.class)
                        .resolve(context.getSource())
                        .getFirst()
                ))
            )
            .requires(source -> hasPermission(source, PERMISSION_SELF))
            .executes(context -> teleportHome(
                context.getSource().getSender(),
                IntegerArgumentType.getInteger(context, "slot"),
                context.getSource().getPlayerOrThrow()
            ))
        )
        .requires(source -> hasPermission(source, PERMISSION_SELF))
        .executes(context -> teleportHome(
            context.getSource().getSender(),
            -1,
            context.getSource().getPlayerOrThrow()
        ));
  }

}
