package me.whizvox.skyislands.core.command.admin;

import me.whizvox.skyislands.command.CommandDelegator;

public class AdminCommand extends CommandDelegator {

  public AdminCommand(String name) {
    super(name);
  }

  @Override
  public void addAllSubCommands() {
    add(new ConfigSubCommand());
    add(new GroupSubCommand());
    add(new ReloadSubCommand());
    add(new WorldSubCommand());
    add(new VersionSubCommand());
    add(new SetupSubCommand());
  }

}
