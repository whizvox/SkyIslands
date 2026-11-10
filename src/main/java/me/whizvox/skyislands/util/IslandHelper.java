package me.whizvox.skyislands.util;

public class IslandHelper {

  public static int getMaxPossibleIslands(int worldBorderSize, int plotSize) {
    return (worldBorderSize / plotSize) * (worldBorderSize / plotSize);
  }

}
