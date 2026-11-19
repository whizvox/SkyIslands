package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.util.Lazy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.joml.Vector3i;

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
  private NamespacedKey structureKey;
  private final Vector3i structureOffset;
  private final Lazy<Structure> structure;

  public IslandWorldProperties(int buildLimitSize, int plotSize, int maxIslands,
                               Collection<IslandPermission> defaultPermissions, NamespacedKey structureKey,
                               Vector3i structureOffset) {
    this.buildLimitSize = buildLimitSize;
    this.plotSize = plotSize;
    this.maxIslands = maxIslands;
    this.defaultPermissions = new ObjectArraySet<>(defaultPermissions);
    this.structureKey = structureKey;
    this.structureOffset = structureOffset;
    structure = Lazy.of(() -> Objects.requireNonNull(Bukkit.getStructureManager().getStructure(this.structureKey), "Could not find structure: " + structureKey));
  }

  public IslandWorldProperties(int buildLimitSize, int plotSize) {
    this(buildLimitSize, plotSize, DEFAULT_MAX_ISLANDS, List.of(), SkyIslands.getInstance().getDefaultStructureKey(), new Vector3i(0, 0, 0));
  }

  public IslandWorldProperties() {
    this(DEFAULT_BUILD_LIMIT_CHUNKS, DEFAULT_PLOT_SIZE_CHUNKS, DEFAULT_MAX_ISLANDS, List.of(),
        SkyIslands.getInstance().getDefaultStructureKey(), new Vector3i(0, 0 ,0));
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

  public NamespacedKey getStructureKey() {
    return structureKey;
  }

  public Structure getStructure() {
    return structure.get();
  }

  public Vector3i getStructureOffset() {
    return new Vector3i(structureOffset);
  }

  public Set<IslandPermission> getDefaultPermissions() {
    return Collections.unmodifiableSet(defaultPermissions);
  }

  public void setMaxIslands(int maxIslands) {
    this.maxIslands = maxIslands;
  }

  public void setStructure(NamespacedKey structureKey, Vector3i offset) {
    this.structure.clear();
    this.structureKey = structureKey;
    structureOffset.set(offset);
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
    return buildLimitSize == that.buildLimitSize && plotSize == that.plotSize && maxIslands == that.maxIslands &&
        Objects.equals(defaultPermissions, that.defaultPermissions) && Objects.equals(structure, that.structure) &&
        Objects.equals(structureOffset, that.structureOffset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(buildLimitSize, plotSize, defaultPermissions, maxIslands, structure, structureOffset);
  }

  @Override
  public String toString() {
    return "IslandWorldProperties{" +
        "buildLimitSize=" + buildLimitSize +
        ", plotSize=" + plotSize +
        ", defaultPermissions=" + defaultPermissions +
        ", maxIslands=" + maxIslands +
        ", structure=" + structure +
        ", structureOffset=" + structureOffset +
        '}';
  }

}
