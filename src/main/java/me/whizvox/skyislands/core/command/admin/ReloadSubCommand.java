package me.whizvox.skyislands.core.command.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import org.jetbrains.annotations.NotNullByDefault;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.perm;
import static me.whizvox.skyislands.SkyIslands.tl;

@NotNullByDefault
public class ReloadSubCommand extends SubCommand {

  public ReloadSubCommand() {
    super("reload", perm("admin.reload"));
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder.executes(context -> {
      SkyIslands.getInstance().reload();
      context.getSource().getSender().sendMessage(tl("admin.reload"));
      return SINGLE_SUCCESS;
    });
  }

}
