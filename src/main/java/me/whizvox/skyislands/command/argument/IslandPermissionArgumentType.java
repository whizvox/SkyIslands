package me.whizvox.skyislands.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.skyislands.island.IslandPermission;

public class IslandPermissionArgumentType extends EnumArgumentType<IslandPermission> {

  public IslandPermissionArgumentType() {
    super(IslandPermission.class);
  }

  public static IslandPermissionArgumentType islandPermission() {
    return new IslandPermissionArgumentType();
  }

  public static IslandPermission get(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IslandPermission.class);
  }

}
