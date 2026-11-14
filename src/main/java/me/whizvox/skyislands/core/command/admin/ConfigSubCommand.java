package me.whizvox.skyislands.core.command.admin;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandWorldArgumentType;
import me.whizvox.skyislands.core.PluginConfiguration;
import me.whizvox.skyislands.island.IslandWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.perm;
import static me.whizvox.skyislands.SkyIslands.tl;

@NotNullByDefault
public class ConfigSubCommand extends SubCommand {

  public ConfigSubCommand() {
    super("config", perm("admin.config"));
  }

  public static int showInfo(CommandSender sender) {
    PluginConfiguration config = SkyIslands.getInstance().getPluginConfig();
    Component groupsComp;
    if (!config.getGroups().getGroups().isEmpty()) {
      groupsComp = Component.join(
          JoinConfiguration.builder().separator(Component.text(", ")).build(),
          config.getGroups().getGroups().stream()
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
    } else {
      groupsComp = tl("general.emptyList");
    }
    sender.sendMessage(tl("admin.config.info", Map.of(
        "save_interval", config.getSaveInterval(),
        "default_max_islands", config.getDefaultMaxIslands(),
        "default_world", config.getDefaultWorld().map(key -> (Object) key).orElse(tl("general.notSet")),
        "groups", groupsComp
    )));
    return SINGLE_SUCCESS;
  }

  public static int setSaveInterval(CommandSender sender, int value) {
    SkyIslands.getInstance().getPluginConfig().setSaveInterval(value);
    sender.sendMessage(tl("admin.config.set.success"));
    return SINGLE_SUCCESS;
  }

  public static int setDefaultMaxIslands(CommandSender sender, int value) {
    SkyIslands.getInstance().getPluginConfig().setDefaultMaxIslands(value);
    sender.sendMessage(tl("admin.config.set.success"));
    return SINGLE_SUCCESS;
  }

  public static int setDefaultWorld(CommandSender sender, @Nullable IslandWorld world) {
    var config = SkyIslands.getInstance().getPluginConfig();
    config.setDefaultWorld(world == null ? null : world.getWorld().key());
    sender.sendMessage(tl("admin.config.set.success"));
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.literal("set")
            .then(Commands.literal("saveInterval")
                .then(Commands.argument("value", IntegerArgumentType.integer(1, 360))
                    .executes(context -> setSaveInterval(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "value")
                    ))
                )
            )
            .then(Commands.literal("defaultMaxIslands")
                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                    .executes(context -> setDefaultMaxIslands(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "value")
                    ))
                )
            )
            .then(Commands.literal("defaultWorld")
                .then(Commands.argument("world", IslandWorldArgumentType.islandWorld())
                    .executes(context -> setDefaultWorld(
                        context.getSource().getSender(),
                        IslandWorldArgumentType.getIslandWorld(context, "world")
                    ))
                )
                .executes(context -> setDefaultWorld(context.getSource().getSender(), null))
            )
        )
        .executes(context -> showInfo(context.getSource().getSender()));
  }

}
