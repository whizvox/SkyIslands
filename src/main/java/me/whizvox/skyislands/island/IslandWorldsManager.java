package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.core.PluginConfiguration;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static me.whizvox.skyislands.SkyIslands.*;

@NotNullByDefault
public class IslandWorldsManager {

  private final Map<Key, IslandWorld> worlds;
  private final Map<Island.OwnerKey, IslandInfo> ownerIslandsCache;
  private @Nullable IslandWorld defaultWorld;
  private @Nullable Key defaultWorldKey;
  private @Nullable IncompleteIslandWorld incompleteWorld;
  private boolean saving;

  public IslandWorldsManager() {
    worlds = new Object2ObjectOpenHashMap<>();
    ownerIslandsCache = new Object2ObjectOpenHashMap<>();
    defaultWorld = null;
    defaultWorldKey = null;
    incompleteWorld = null;
    saving = false;
  }

  public Optional<IslandWorld> getDefaultWorld() {
    return Optional.ofNullable(defaultWorld);
  }

  public Optional<Key> getDefaultWorldKey() {
    return Optional.ofNullable(defaultWorldKey);
  }

  public Stream<IslandWorld> getWorlds() {
    return worlds.values().stream();
  }

  public Optional<IslandWorld> getIslandWorld(@Nullable Key key) {
    if (key == null) {
      if (defaultWorld == null) {
        return Optional.empty();
      }
      return Optional.of(defaultWorld);
    }
    return Optional.ofNullable(worlds.get(key));
  }

  public Optional<IncompleteIslandWorld> getIncompleteWorld() {
    return Optional.ofNullable(incompleteWorld);
  }

  /**
   * Retrieve a player's island based on their UUID and the slot number of the island.
   * <br>
   * <b>Important to note:</b> This uses a cache to look up the correct value. If an island changes ownership or if a
   * player changes the slot number of one of their islands, you must call {@link #invalidateCache(Island.OwnerKey)}
   * immediately, or this might return the incorrect value!
   * @param key The key to look up
   * @return The {@link IslandInfo} instance corresponding to the specified key
   */
  public Optional<IslandInfo> getIsland(Island.OwnerKey key) {
    IslandInfo res = ownerIslandsCache.get(key);
    if (res != null) {
      return Optional.of(res);
    }
    var op = worlds.values().stream()
        .map(world -> world.getIsland(key))
        .filter(Optional::isPresent)
        .findFirst().orElse(Optional.empty());
    if (op.isPresent()) {
      res = op.get();
      ownerIslandsCache.put(key, res);
      return Optional.of(res);
    }
    return Optional.empty();
  }

  public Optional<IslandInfo> getIsland(Island.WorldKey key) {
    return getIslandWorld(key.world()).flatMap(world -> world.getIsland(key.id()));
  }

  public Stream<IslandInfo> getIslands(UUID owner) {
    return worlds.values().stream().flatMap(world -> world.getIslandsByOwner(owner).stream());
  }

  public boolean isSaving() {
    return saving;
  }

  public boolean setDefaultWorld(Key key) {
    IslandWorld world = worlds.get(key);
    if (world != null) {
      defaultWorld = world;
      defaultWorldKey = key;
      return true;
    }
    return false;
  }

  public boolean addWorld(IslandWorld islandWorld) {
    boolean result = worlds.putIfAbsent(islandWorld.getWorld().key(), islandWorld) == null;
    if (!result || defaultWorld != null) {
      return result;
    }
    defaultWorld = islandWorld;
    defaultWorldKey = islandWorld.getWorld().key();
    return true;
  }

  public void deleteIslandWorld(Key key, boolean deleteFromDisk, Consumer<Component> messageConsumer) {
    final IslandWorld world = worlds.get(key);
    if (world != null) {
      Bukkit.getAsyncScheduler().runNow(SkyIslands.getInstance(), _ -> {
        CompletableFuture<?>[] futures = world.getWorld().getPlayers().stream().map(player -> {
          player.sendMessage(tl("lobby.teleport"));
          return player.teleportAsync(world.getLobby(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }).toArray(value -> new CompletableFuture<?>[value]);
        CompletableFuture.allOf(futures).thenAcceptAsync(_ -> {
          if (deleteFromDisk) {
            Path worldPath = world.getWorld().getWorldPath();
            try (var paths = Files.walk(worldPath)) {
              List<Path> filesToDelete = paths.sorted(Comparator.reverseOrder()).toList();
              for (Path path : filesToDelete) {
                Files.delete(path);
              }
            } catch (IOException e) {
              warning(e, "Could not delete world folder {0}", worldPath);
              messageConsumer.accept(tl("world.delete.errorDeletingFromDisk", Map.of(
                  "world", world.getWorld().getKey(),
                  "error", e.getMessage()
              )));
            }
          }
          messageConsumer.accept(tl("world.delete.success"));
          worlds.remove(key);
        });
      });
    }
  }

  public boolean setIncompleteWorld(IncompleteIslandWorld incompleteWorld) {
    if (this.incompleteWorld == null) {
      this.incompleteWorld = incompleteWorld;
      return true;
    }
    return false;
  }

  public boolean discardIncompleteWorld() {
    if (incompleteWorld != null) {
      incompleteWorld = null;
      return true;
    }
    return false;
  }

  /**
   * Use this when an island owner either transfers ownership to another player or if a player puts their island into
   * another slot. Makes it so {@link #getIsland(Island.OwnerKey)} will return the correct value upon its next call.
   * @param key The key to invalidate
   */
  public void invalidateCache(Island.OwnerKey key) {
    ownerIslandsCache.remove(key);
  }

  /**
   * Deletes all cache data.
   */
  public void invalidateCache() {
    ownerIslandsCache.clear();
  }

  public void load(Path worldsDir, PluginConfiguration pluginConfig) {
    worlds.clear();
    defaultWorld = null;
    defaultWorldKey = null;
    incompleteWorld = null;
    try (var files = Files.walk(worldsDir, 1)) {
      files
          .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
          .forEach(path -> {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
              IslandWorld world = SkyIslands.getInstance().getGson().fromJson(reader, IslandWorld.class);
              Key key = world.getWorld().key();
              worlds.put(key, world);
            } catch (IOException e) {
              severe(e, "Could not read world file at `{0}`", path);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Could not read from worlds directory: " + worldsDir, e);
    }

    pluginConfig.getDefaultWorld().ifPresent(key -> {
      if (!setDefaultWorld(key)) {
        warning("Default world `{0}` not found!", key);
      }
    });
  }

  public boolean save(Path worldsDir, PluginConfiguration pluginConfig) {
    if (saving) {
      return false;
    }
    saving = true;
    synchronized (worlds) {
      if (!Files.isDirectory(worldsDir)) {
        try {
          Files.createDirectories(worldsDir);
        } catch (IOException e) {
          severe(e, "Could not save island worlds to directory: {0}", worldsDir);
          saving = false;
          return false;
        }
      }
      worlds.values().forEach(world -> {
        Path worldPath = worldsDir.resolve(world.getWorld().getUID() + ".json");
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(worldPath), StandardCharsets.UTF_8)) {
          SkyIslands.getInstance().getGson().toJson(world, writer);
          fine("Finished writing island world file at {0}", worldPath);
        } catch (IOException e) {
          severe(e, "Could not save island world {0} at {1}", world.getWorld().key(), worldPath);
        }
      });
    }
    pluginConfig.setDefaultWorld(defaultWorldKey);
    saving = false;
    return true;
  }

}
