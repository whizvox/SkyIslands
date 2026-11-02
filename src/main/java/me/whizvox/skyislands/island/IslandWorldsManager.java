package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.skyislands.SkyIslands;
import org.bukkit.World;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.whizvox.skyislands.SkyIslands.info;
import static me.whizvox.skyislands.SkyIslands.warning;

@NotNullByDefault
public class IslandWorldsManager {

    private final Map<UUID, IslandWorld> worlds;
    private @Nullable UUID defaultWorldKey;
    private @Nullable IslandWorld defaultWorld;
    private boolean isSaving;

    public IslandWorldsManager() {
        worlds = new Object2ObjectOpenHashMap<>();
        defaultWorldKey = null;
        defaultWorld = null;
        isSaving = false;
    }

    public boolean isIslandWorld(World world) {
        return worlds.containsKey(world.getUID());
    }

    public Optional<IslandWorld> getDefaultWorld() {
        return Optional.ofNullable(defaultWorld);
    }

    public boolean setDefaultWorld(World world) {
        UUID id = world.getUID();
        if (isIslandWorld(world)) {
            defaultWorldKey = id;
            defaultWorld = worlds.get(id);
            return true;
        }
        return false;
    }

    public Optional<IslandWorld> createWorld(World world, IslandWorldProperties properties) {
        if (!isIslandWorld(world)) {
            IslandWorld islandWorld = new IslandWorld(world, properties);
            worlds.put(world.getUID(), islandWorld);
            return Optional.of(islandWorld);
        }
        return Optional.empty();
    }

    public void load(Path worldsDir) {
        if (Files.isDirectory(worldsDir)) {
            worlds.clear();
            try (var list = Files.list(worldsDir)) {
                list
                    .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        try (Reader reader = new InputStreamReader(Files.newInputStream(path))) {
                            IslandWorld islandWorld = SkyIslands.getInstance().getGson().fromJson(reader,
                                IslandWorld.class);
                            worlds.put(islandWorld.getWorld().getUID(), islandWorld);
                        } catch (IOException e) {
                            warning(e, "Could not read island world from `{0}`", path);
                        }
                    });
            } catch (IOException e) {
                warning(e, "Could not list files from worlds directory `{0}`", worldsDir);
            }
            info("Finished loading {0} island world(s)", worlds.size());
        } else {
            info("Worlds directory `{0}` does not exist, loaded 0 island worlds", worldsDir);
        }
    }

    public boolean save(Path worldsDir) {
        if (isSaving) {
            return false;
        }
        isSaving = true;
        worlds.forEach((id, world) -> {
            Path path = worldsDir.resolve(id + ".json");
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path))) {
                SkyIslands.getInstance().getGson().toJson(world, writer);
            } catch (IOException e) {
                warning(e, "Could not save island world file at `{0}`", path);
            }
        });
        isSaving = false;
        return true;
    }

}
