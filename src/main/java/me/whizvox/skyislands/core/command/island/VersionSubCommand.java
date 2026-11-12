package me.whizvox.skyislands.core.command.island;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.skyislands.command.SubCommand;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.*;

public class VersionSubCommand extends SubCommand {

  public VersionSubCommand() {
    super("version", perm("version"));
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder.executes(context -> {
      context.getSource().getSender().sendMessage(tl(
          "command.version",
          Map.of("version", getInstance().getPluginMeta().getVersion())
      ));
      return SINGLE_SUCCESS;
    });
  }

}
