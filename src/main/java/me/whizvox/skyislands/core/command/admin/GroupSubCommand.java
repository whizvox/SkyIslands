package me.whizvox.skyislands.core.command.admin;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandGroupArgumentType;
import me.whizvox.skyislands.island.IslandGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.*;

public class GroupSubCommand extends SubCommand {

  public GroupSubCommand() {
    super("group", perm("admin.config"));
  }

  public static int listGroups(CommandSender sender) {
    Component groupsList = Component.join(
        JoinConfiguration.builder().separator(Component.text(", ")).build(),
        SkyIslands.getInstance().getPluginConfig().getGroups().getGroups().stream()
            .sorted(Comparator.comparing(o -> o.name))
            .map(group -> tl("general.islandGroup", Map.of(
                "name", group.name,
                "display_name", group.getDisplayName(),
                "max_islands", group.getMaxIslands(),
                "max_members", group.getMaxMembers(),
                "max_visitors", group.getMaxVisitors()
            )))
            .toList()
    );
    sender.sendMessage(tl("admin.group.list", Map.of("groups", groupsList)));
    return SINGLE_SUCCESS;
  }

  public static int addGroup(CommandSender sender, String name) throws CommandSyntaxException {
    if (!SkyIslands.getInstance().getPluginConfig().getGroups().addGroup(new IslandGroup(name))) {
      throw cmdex("admin.group.add.conflict");
    }
    sender.sendMessage(tl("admin.group.add.success"));
    return SINGLE_SUCCESS;
  }

  public static int editGroupDisplayName(CommandSender sender, IslandGroup group, String displayName) throws CommandSyntaxException {
    String oldDisplayName = group.getMiniMessageDisplayName();
    group.setDisplayName(displayName);
    if (PlainTextComponentSerializer.plainText().serialize(group.getDisplayName()).isEmpty()) {
      group.setDisplayName(oldDisplayName);
      throw cmdex("admin.group.edit.displayName.cannotBeEmpty");
    }
    sender.sendMessage(tl("admin.group.edit.displayName.success", Map.of("group", group.name, "value", group.getDisplayName())));
    return SINGLE_SUCCESS;
  }

  public static int setMaxIslands(CommandSender sender, IslandGroup group, int value) {
    group.setMaxIslands(value);
    sender.sendMessage(tl("admin.group.edit.maxIslands.success", Map.of("group", group.name, "value", value)));
    return SINGLE_SUCCESS;
  }

  public static int setMaxMembers(CommandSender sender, IslandGroup group, int value) {
    group.setMaxMembers(value);
    sender.sendMessage(tl("admin.group.edit.maxMembers.success", Map.of("group", group.name, "value", value)));
    return SINGLE_SUCCESS;
  }

  public static int setMaxVisitors(CommandSender sender, IslandGroup group, int value) {
    group.setMaxVisitors(value);
    sender.sendMessage(tl("admin.group.edit.maxVisitors.success", Map.of("group", group.name, "value", value)));
    return SINGLE_SUCCESS;
  }

  public static int deleteGroup(CommandSender sender, IslandGroup group) {
    if (!SkyIslands.getInstance().getPluginConfig().getGroups().deleteGroup(group.name)) {
      throw new IllegalStateException("Somehow could not delete island group " + group.name);
    }
    sender.sendMessage(tl("admin.group.delete.success", Map.of("name", group.name)));
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.literal("list")
            .executes(context -> listGroups(context.getSource().getSender()))
        )
        .then(Commands.literal("add")
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(context -> addGroup(
                    context.getSource().getSender(),
                    StringArgumentType.getString(context, "name")
                ))
            )
        )
        .then(Commands.literal("edit")
            .then(Commands.argument("group", IslandGroupArgumentType.islandGroup())
                .then(Commands.literal("displayName")
                    .then(Commands.argument("displayname", StringArgumentType.greedyString())
                        .executes(context -> editGroupDisplayName(
                            context.getSource().getSender(),
                            IslandGroupArgumentType.getIslandGroup(context, "group"),
                            StringArgumentType.getString(context, "displayname")
                        ))
                    )
                )
                .then(Commands.literal("maxIslands")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 1000))
                        .executes(context -> setMaxIslands(
                            context.getSource().getSender(),
                            IslandGroupArgumentType.getIslandGroup(context, "group"),
                            IntegerArgumentType.getInteger(context, "value")
                        ))
                    )
                )
                .then(Commands.literal("maxMembers")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 1000))
                        .executes(context -> setMaxMembers(
                            context.getSource().getSender(),
                            IslandGroupArgumentType.getIslandGroup(context, "group"),
                            IntegerArgumentType.getInteger(context, "value")
                        ))
                    )
                )
                .then(Commands.literal("maxVisitors")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 1000))
                        .executes(context -> setMaxVisitors(
                            context.getSource().getSender(),
                            IslandGroupArgumentType.getIslandGroup(context, "group"),
                            IntegerArgumentType.getInteger(context, "value")
                        ))
                    )
                )
            )
        )
        .then(Commands.literal("delete")
            .then(Commands.argument("group", IslandGroupArgumentType.islandGroup())
                .executes(context -> deleteGroup(
                    context.getSource().getSender(),
                    IslandGroupArgumentType.getIslandGroup(context, "group"))
                )
            )
        )
        .executes(context -> listGroups(context.getSource().getSender()));
  }

}
