package me.whizvox.skyislands.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public abstract class SubCommand {

  public final String name;
  public final @Nullable Permission permission;

  public SubCommand(String name, @Nullable Permission permission) {
    this.name = name;
    this.permission = permission;
  }

  public SubCommand(String name) {
    this(name, null);
  }

  protected abstract void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder);

  public void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
    var subCommand = Commands.literal(name);
    if (permission != null) {
      subCommand.requires(source -> source.getSender().hasPermission(permission));
    }
    doRegister(subCommand);
    builder.then(subCommand);
  }

  public static boolean hasPermission(CommandSourceStack source, Permission permission) {
    return source.getSender().hasPermission(permission);
  }

}
