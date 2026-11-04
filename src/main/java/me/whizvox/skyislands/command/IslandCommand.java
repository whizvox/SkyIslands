package me.whizvox.skyislands.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.skyislands.command.argument.OfflinePlayerArgumentType;
import me.whizvox.skyislands.island.IslandWorldProperties;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.skyislands.SkyIslands.*;

@NotNullByDefault
public class IslandCommand {

    public static final Permission
        PERMISSION_VERSION = perm("version"),
        PERMISSION_WORLD_CREATE = perm("world.create"),
        PERMISSION_CREATE = perm("island.create"),
        PERMISSION_CREATE_OTHERS = perm("island.create.others");

    private static boolean hasPermission(CommandSourceStack source, Permission permission) {
        return source.getSender().hasPermission(permission);
    }

    private static int getVersion(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(tl("command.version", Map.of("version",
            getInstance().getPluginMeta().getVersion())));
        return SINGLE_SUCCESS;
    }

    public static int createWorld(CommandSender sender, String worldName, IslandWorldProperties properties)
            throws CommandSyntaxException {
        int maxPossibleIslands = (3749998 / properties.getPlotSize()) * (3749998 / properties.getPlotSize());
        if (properties.getMaxIslands() > maxPossibleIslands) {
            properties.setMaxIslands(maxPossibleIslands);
        }
        if (properties.getBuildLimitSize() > properties.getPlotSize()) {
            throw cmdex("argument.buildLimitTooLarge");
        }
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            throw cmdex("argument.worldAlreadyExists");
        }
        NamespacedKey worldKey = key(worldName);
        if (worldKey == null) {
            throw cmdex("argument.invalidWorldName");
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
        getInstance().getIslandsManager().createWorld(world, properties)
            .ifPresentOrElse(
                islandWorld -> sender.sendMessage(
                        tl("createWorld.success", Map.of("world", islandWorld.getWorld().getKey()))
                ),
                () -> sender.sendMessage(tl("createWorld.fail.alreadyIslandWorld"))
            );
        return SINGLE_SUCCESS;
    }

    public static int createIsland(CommandSender sender, @Nullable OfflinePlayer owner) {
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("island")
                .then(Commands.literal("version")
                    .requires(source -> hasPermission(source, PERMISSION_VERSION))
                    .executes(IslandCommand::getVersion)
                )
                .then(Commands.literal("create")
                    .then(Commands.argument("other", OfflinePlayerArgumentType.offlinePlayer())
                        .requires(source -> hasPermission(source, PERMISSION_CREATE_OTHERS))
                        .executes(context -> createIsland(
                            context.getSource().getSender(),
                            OfflinePlayerArgumentType.get(context, "other")
                        ))
                    )
                    .requires(source -> hasPermission(source, PERMISSION_CREATE))
                    .executes(context -> createIsland(context.getSource().getPlayerOrThrow(), null))
                )
                .then(Commands.literal("createworld")
                    .requires(source -> hasPermission(source, PERMISSION_WORLD_CREATE))
                    .then(Commands.argument("worldName", StringArgumentType.word())
                        .then(Commands.argument("plotSize", IntegerArgumentType.integer(1, 10000))
                            .then(Commands.argument("buildLimit", IntegerArgumentType.integer(1, 10000))
                                .executes(context -> createWorld(
                                    context.getSource().getSender(),
                                    StringArgumentType.getString(context, "worldName"),
                                    new IslandWorldProperties(
                                        IntegerArgumentType.getInteger(context, "buildLimit"),
                                        IntegerArgumentType.getInteger(context, "plotSize")
                                    )
                                ))
                            )
                        )
                        .executes(context -> createWorld(
                            context.getSource().getSender(),
                            StringArgumentType.getString(context, "worldName"),
                            new IslandWorldProperties()
                        ))
                    )
                )
                .build()
        );
    }

}
