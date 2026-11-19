package me.whizvox.skyislands.core.command.admin;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandWorldArgumentType;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.util.ComponentHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.joml.Vector3i;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.perm;
import static me.whizvox.skyislands.SkyIslands.tl;

public class WorldSubCommand extends SubCommand {

  public WorldSubCommand() {
    super("world", perm("admin.world"));
  }

  public static int listWorlds(CommandSender sender) {
    Component worldsList = ComponentHelper.joining(", ",
        SkyIslands.getInstance().getIslandsWorldManager().getWorlds()
            .map(iw -> iw.getWorld().key().value())
            .sorted()
            .map(name -> tl("general.islandWorld", Map.of("name", name))
                .clickEvent(ClickEvent.runCommand("skyislands:islandadmin world info " + name))
            )
    );
    sender.sendMessage(tl("admin.world.list", Map.of("worlds", worldsList)));
    return SINGLE_SUCCESS;
  }

  public static int displayWorldInfo(CommandSender sender, IslandWorld world) {
    Component permsList = ComponentHelper.joining(", ",
        world.getProperties().getDefaultPermissions().stream()
            .map(permission -> permission.str)
            .sorted()
            .map(str -> Component.text(str).color(NamedTextColor.YELLOW))
    );
    Vector3i offset = world.getProperties().getStructureOffset();
    sender.sendMessage(tl("admin.world.info", Map.of(
        "key", world.getWorld().key(),
        "uuid", world.getWorld().getUID(),
        "lobby", ComponentHelper.location(world.getLobby()),
        "island_count", world.getIslands().count(),
        "max_islands", world.getProperties().getMaxIslands(),
        "plot", world.getProperties().getPlotSize(),
        "build_area", world.getProperties().getBuildLimitSize(),
        "default_permissions", permsList,
        "structure", world.getProperties().getStructureKey(),
        "structure_offset", tl("general.xyz", Map.of("x", offset.x, "y", offset.y, "z", offset.z))
    )));
    return SINGLE_SUCCESS;
  }

  public static int setStructureOffset(CommandSender sender, IslandWorld world, Vector3i offset) {
    world.getProperties().setStructure(world.getProperties().getStructureKey(), offset);
    sender.sendMessage(tl("admin.world.set.structureOffset", Map.of("value", ComponentHelper.xyz(offset.x, offset.y, offset.z))));
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.literal("list")
            .executes(context -> listWorlds(context.getSource().getSender()))
        )
        .then(Commands.literal("info")
            .then(Commands.argument("world", IslandWorldArgumentType.islandWorld())
                .executes(context -> displayWorldInfo(
                    context.getSource().getSender(),
                    IslandWorldArgumentType.getIslandWorld(context, "world")
                ))
            )
        )
        .then(Commands.literal("set")
            .then(Commands.argument("world", IslandWorldArgumentType.islandWorld())
                .then(Commands.literal("structureOffset")
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> setStructureOffset(
                                    context.getSource().getSender(),
                                    IslandWorldArgumentType.getIslandWorld(context, "world"),
                                    new Vector3i(
                                        IntegerArgumentType.getInteger(context, "x"),
                                        IntegerArgumentType.getInteger(context, "y"),
                                        IntegerArgumentType.getInteger(context, "z")
                                    )
                                ))
                            )
                        )
                    )
                )
            )
        );
  }

}
