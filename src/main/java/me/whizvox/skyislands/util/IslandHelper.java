package me.whizvox.skyislands.util;

import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;

@NotNullByDefault
public class IslandHelper {

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
