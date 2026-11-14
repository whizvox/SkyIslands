package me.whizvox.skyislands.command;

import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandDelegator {

  public final String name;
  private final List<SubCommand> subCommands;

  public CommandDelegator(String name) {
    this.name = name;
    subCommands = new ArrayList<>();
  }

  protected void add(SubCommand command) {
    subCommands.add(command);
  }

  public abstract void addAllSubCommands();

  public void register(Commands commands) {
    var mainCommand = Commands.literal(name);
    subCommands.forEach(sub -> sub.register(mainCommand));
    commands.register(mainCommand.build());
  }

}
