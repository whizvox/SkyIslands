package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.*;

public class IslandWorldProperties {

  public static final int
      DEFAULT_BUILD_LIMIT_CHUNKS = 32,
      DEFAULT_PLOT_SIZE_CHUNKS = 50,
      DEFAULT_MAX_ISLANDS = 1_000_000;

  private final int buildLimitSize;
  private final int plotSize;
  private final Set<IslandPermission> defaultPermissions;
  private int maxIslands;

  public IslandWorldProperties(int buildLimitSize, int plotSize, int maxIslands,
                               Collection<IslandPermission> defaultPermissions) {
    this.buildLimitSize = buildLimitSize;
    this.plotSize = plotSize;
    this.maxIslands = maxIslands;
    this.defaultPermissions = new ObjectArraySet<>(defaultPermissions);
  }

  public IslandWorldProperties(int buildLimitSize, int plotSize) {
    this(buildLimitSize, plotSize, DEFAULT_MAX_ISLANDS, List.of());
  }

  public IslandWorldProperties() {
    this(DEFAULT_BUILD_LIMIT_CHUNKS, DEFAULT_PLOT_SIZE_CHUNKS, DEFAULT_MAX_ISLANDS, List.of());
  }

  public int getBuildLimitSize() {
    return buildLimitSize;
  }

  public int getPlotSize() {
    return plotSize;
  }

  public int getMaxIslands() {
    return maxIslands;
  }

  public void setMaxIslands(int maxIslands) {
    this.maxIslands = maxIslands;
  }

  public Set<IslandPermission> getDefaultPermissions() {
    return Collections.unmodifiableSet(defaultPermissions);
  }

  /**
   * Toggle whether a default permission is enabled
   *
   * @param permission The permission to toggle on or off
   * @return true if the permission was set, false if it was removed
   */
  public boolean toggleDefaultPermission(IslandPermission permission) {
    if (!defaultPermissions.remove(permission)) {
      defaultPermissions.add(permission);
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    IslandWorldProperties that = (IslandWorldProperties) o;
    return buildLimitSize == that.buildLimitSize &&
        plotSize == that.plotSize &&
        maxIslands == that.maxIslands &&
        defaultPermissions.equals(that.defaultPermissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(buildLimitSize, plotSize, defaultPermissions, maxIslands);
  }

  @Override
  public String toString() {
    return "IslandWorldProperties{" +
        "buildLimitSize=" + buildLimitSize +
        ", plotSize=" + plotSize +
        ", defaultPermissions=" + defaultPermissions +
        ", maxIslands=" + maxIslands +
        '}';
  }

}
