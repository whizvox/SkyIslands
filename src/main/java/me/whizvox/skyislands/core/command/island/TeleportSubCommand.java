package me.whizvox.skyislands.core.command.island;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandTeleportParametersArgumentType;
import me.whizvox.skyislands.command.util.TranslatableCommandExceptionType;
import me.whizvox.skyislands.island.Island;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.perm;
import static me.whizvox.skyislands.SkyIslands.tl;

public class TeleportSubCommand extends SubCommand {

  public TeleportSubCommand() {
    super("tp", perm("teleport"));
  }

  public static int teleportToIsland(CommandSender sender, Parameters parameters) throws CommandSyntaxException {
    Player target;
    if (parameters.target == null) {
      if (sender instanceof Player player) {
        target = player;
      } else {
        throw new TranslatableCommandExceptionType("permissions.requires.player").create();
      }
    } else {
      target = parameters.target;
    }
    target.teleportAsync(parameters.island.getHome(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(success -> {
      if (success) {
        target.showTitle(Title.title(tl("teleport.success"), Component.empty()));
        if (target != sender) {
          sender.sendMessage(tl("teleport.success.other", Map.of("player", target.displayName())));
        }
      } else {
        if (sender == target) {
          sender.showTitle(Title.title(tl("teleport.fail"), tl("teleport.fail.subtitle")));
        } else {
          sender.sendMessage(tl("teleport.fail.other", Map.of("player", target.displayName())));
        }
      }
    });
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.argument("params", IslandTeleportParametersArgumentType.parameters())
            .executes(context -> teleportToIsland(
                context.getSource().getSender(),
                IslandTeleportParametersArgumentType.getParameters(context, "params")
            ))
        );
  }

  public record Parameters(@Nullable Player target, Island island) {
  }

}
