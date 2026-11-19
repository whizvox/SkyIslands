package me.whizvox.skyislands.util;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@NotNullByDefault
public class IslandHelper {

  private static <T extends OfflinePlayer> Optional<T> getPlayer(String playerStr, Function<UUID, Optional<T>> uuidGetter, Function<String, Optional<T>> nameGetter) {
    if (playerStr.length() == 36) {
      try {
        UUID playerId = UUID.fromString(playerStr);
        var player = uuidGetter.apply(playerId);
        if (player.isPresent()) {
          return player;
        }
      } catch (IllegalArgumentException ignored) {}
    }
    return nameGetter.apply(playerStr);
  }

  public static Optional<OfflinePlayer> getOfflinePlayer(String playerStr) {
    return getPlayer(
        playerStr,
        uuid -> Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getUniqueId().equals(uuid)).findAny(),
        name -> Optional.ofNullable(Bukkit.getOfflinePlayerIfCached(name))
    );
  }

  public static Optional<Player> getPlayer(String playerStr) {
    return getPlayer(
        playerStr,
        uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)),
        name -> Optional.ofNullable(Bukkit.getPlayerExact(name))
    );
  }

  public static int getMaxPossibleIslands(int worldBorderSize, int plotSize) {
    return (worldBorderSize / plotSize) * (worldBorderSize / plotSize);
  }

  public static WorldCreator getVoidWorldCreator(NamespacedKey key) {
    return new WorldCreator(key)
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
        .generator(new ChunkGenerator() {});
  }

}
