package me.whizvox.skyislands.core.command.island;

import me.whizvox.skyislands.command.CommandDelegator;

public class IslandCommand extends CommandDelegator {

  public IslandCommand(String name) {
    super(name);
  }

  public void addAllSubCommands() {
    add(new VersionSubCommand());
    add(new SetupSubCommand());
    add(new CreateSubCommand());
  }

}
