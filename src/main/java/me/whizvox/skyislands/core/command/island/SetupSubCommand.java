package me.whizvox.skyislands.core.command.island;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.command.SubCommand;
import me.whizvox.skyislands.command.argument.IslandPermissionArgumentType;
import me.whizvox.skyislands.island.IncompleteIslandWorld;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.island.IslandWorldProperties;
import me.whizvox.skyislands.util.ComponentHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.*;

@NotNullByDefault
public class SetupSubCommand extends SubCommand {

  public static final Permission PERMISSION = perm("setup");

  public SetupSubCommand() {
    super("setup", PERMISSION);
  }

  public static int setupWorld(CommandSender sender) throws CommandSyntaxException {
    IncompleteIslandWorld world = new IncompleteIslandWorld();
    if (SkyIslands.getInstance().getIslandsWorldManager().setIncompleteWorld(world)) {
      sender.sendMessage(tl("setup.success"));
    } else {
      throw cmdex("setup.alreadySettingUp");
    }
    return SINGLE_SUCCESS;
  }

  public static int setupShowInfo(CommandSender sender) {
    SkyIslands.getInstance().getIslandsWorldManager().getIncompleteWorld().ifPresentOrElse(world -> {
      Component defaultPermissionsText;
      if (!world.getDefaultPermissions().isEmpty()) {
        defaultPermissionsText = Component.join(
            JoinConfiguration.builder().separator(Component.text(", ")).build(),
            world.getDefaultPermissions().stream()
                .map(ip -> Component.text(ip.name()).color(NamedTextColor.YELLOW))
                .toList()
        );
      } else {
        defaultPermissionsText = tl("general.emptyList");
      }
      sender.sendMessage(tl("setup.info", Map.of(
          "world_name", world.getWorldName().map(name -> (Component) Component.text(name)).orElse(tl("general.notSet")),
          "lobby", world.getLobby().map(ComponentHelper::location).orElse(tl("general.notSet")),
          "world_border", world.getWorldBorderDiameter(),
          "plot", world.getPlotDiameter(),
          "build_area", world.getBuildAreaDiameter(),
          "default_permissions", defaultPermissionsText
      )));
    }, () -> sender.sendMessage(tl("setup.noneFound")));
    return SINGLE_SUCCESS;
  }

  public static int setupShowHelp(CommandSender sender) {
    sender.sendMessage(tl("setup.help"));
    return SINGLE_SUCCESS;
  }

  public static <T> int setupSetProperty(CommandSender sender, T value, BiConsumer<IncompleteIslandWorld, T> setter, Supplier<Component> messageSupplier) {
    SkyIslands.getInstance().getIslandsWorldManager().getIncompleteWorld().ifPresentOrElse(world -> {
      setter.accept(world, value);
      sender.sendMessage(messageSupplier.get());
    }, () -> sender.sendMessage(tl("setup.noneFound")));
    return SINGLE_SUCCESS;
  }

  public static int discardSetupWorld(CommandSender sender) {
    if (SkyIslands.getInstance().getIslandsWorldManager().discardIncompleteWorld()) {
      sender.sendMessage(tl("setup.discard"));
    } else {
      sender.sendMessage(tl("setup.noneFound"));
    }
    return SINGLE_SUCCESS;
  }

  public static int createWorld(CommandSender sender)
      throws CommandSyntaxException {
    IncompleteIslandWorld setupWorld = SkyIslands.getInstance().getIslandsWorldManager().getIncompleteWorld()
        .orElseThrow(() -> cmdex("setup.noneFound"));
    String worldName = setupWorld.getWorldName().orElseThrow(() -> cmdex("setup.create.worldNameNotSet"));
    Location lobby = setupWorld.getLobby().orElseThrow(() -> cmdex("setup.create.lobbyNotSet"));
    NamespacedKey worldKey = key(worldName);
    if (worldKey == null) {
      throw cmdex("setup.create.invalidWorldName");
    }
    World world = Bukkit.getWorld(worldKey);
    if (world != null) {
      throw cmdex("setup.create.worldNameConflict");
    }
    if (SkyIslands.getInstance().getIslandsWorldManager().getIslandWorld(worldKey).isPresent()) {
      throw new IllegalStateException("Island world exists in plugin registry, but is not a loaded world: " + worldKey);
    }
    world = Objects.requireNonNull(
        new WorldCreator(worldKey)
            .type(WorldType.NORMAL)
            .biomeProvider(new BiomeProvider() {
              @Override
              public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
                return Biome.PLAINS;
              }
              @Override
              public List<Biome> getBiomes(WorldInfo worldInfo) {
                return List.of(Biome.PLAINS);
              }
            })
            .generator(new ChunkGenerator() {})
            .createWorld(),
        "Failed to create sky island world"
    );
    IslandWorld islandWorld = new IslandWorld(world, lobby, new IslandWorldProperties(
        setupWorld.getBuildAreaDiameter(),
        setupWorld.getPlotDiameter(),
        setupWorld.getMaxIslands(),
        setupWorld.getDefaultPermissions()
    ));
    if (getInstance().getIslandsWorldManager().addWorld(islandWorld)) {
      sender.sendMessage(tl("setup.create.success", Map.of("world", islandWorld.getWorld().getKey())));
    } else {
      Bukkit.unloadWorld(world, false);
      throw new IllegalStateException("Could not create new island world as the key is already used: " + worldKey);
    }
    return SINGLE_SUCCESS;
  }

  @Override
  protected void doRegister(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder
        .then(Commands.literal("info")
            .executes(context -> setupShowInfo(context.getSource().getSender()))
        )
        .then(Commands.literal("help")
            .executes(context -> setupShowHelp(context.getSource().getSender()))
        )
        .then(Commands.literal("set")
            .then(Commands.literal("name")
                .then(Commands.argument("worldName", StringArgumentType.word())
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        StringArgumentType.getString(context, "worldName"),
                        IncompleteIslandWorld::setWorldName,
                        () -> tl("setup.set.worldName")
                    ))
                )
            )
            .then(Commands.literal("lobby")
                .then(Commands.argument("location", ArgumentTypes.finePosition())
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        context.getArgument("location", FinePositionResolver.class).resolve(context.getSource()).toLocation(context.getSource().getPlayerOrThrow().getWorld()),
                        IncompleteIslandWorld::setLobby,
                        () -> tl("setup.set.lobby")
                    ))
                )
            )
            .then(Commands.literal("worldBorder")
                .then(Commands.argument("diameter", IntegerArgumentType.integer(1, IncompleteIslandWorld.MAX_WORLD_BORDER_CHUNKS))
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "diameter"),
                        IncompleteIslandWorld::setWorldBorderDiameter,
                        () -> tl("setup.set.worldBorder")
                    ))
                )
            )
            .then(Commands.literal("plot")
                .then(Commands.argument("diameter", IntegerArgumentType.integer(1, 1000))
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "diameter"),
                        IncompleteIslandWorld::setPlotDiameter,
                        () -> tl("setup.set.plotSize")
                    ))
                )
            )
            .then(Commands.literal("buildArea")
                .then(Commands.argument("diameter", IntegerArgumentType.integer(1, 1000))
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "diameter"),
                        IncompleteIslandWorld::setBuildAreaDiameter,
                        () -> tl("setup.set.worldBorder")
                    ))
                )
            )
            .then(Commands.literal("maxIslands")
                .then(Commands.argument("diameter", IntegerArgumentType.integer(1))
                    .executes(context -> setupSetProperty(
                        context.getSource().getSender(),
                        IntegerArgumentType.getInteger(context, "diameter"),
                        IncompleteIslandWorld::setMaxIslands,
                        () -> tl("setup.set.maxIslands")
                    ))
                )
            )
        )
        .then(Commands.literal("toggle")
            .then(Commands.argument("permission", IslandPermissionArgumentType.islandPermission())
                .executes(context -> setupSetProperty(
                    context.getSource().getSender(),
                    IslandPermissionArgumentType.get(context, "permission"),
                    IncompleteIslandWorld::toggleDefaultPermission,
                    () -> tl("setup.set.defaultPermission")
                ))
            )
        )
        .then(Commands.literal("discard")
            .executes(context -> discardSetupWorld(context.getSource().getSender()))
        )
        .then(Commands.literal("create")
            .executes(context -> createWorld(context.getSource().getSender()))
        )
        .executes(context -> setupWorld(context.getSource().getSender()));
  }

}
