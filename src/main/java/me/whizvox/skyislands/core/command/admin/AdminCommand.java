package me.whizvox.skyislands.core.command.admin;

import me.whizvox.skyislands.command.CommandDelegator;

public class AdminCommand extends CommandDelegator {

  public AdminCommand(String name) {
    super(name);
  }

  @Override
  public void addAllSubCommands() {
    add(new ConfigSubCommand());
  }

}
