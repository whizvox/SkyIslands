package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.skyislands.util.IslandHelper;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@NotNullByDefault
public class IncompleteIslandWorld {

  public static final int
      MAX_WORLD_BORDER_BLOCKS = 59999968,
      MAX_WORLD_BORDER_CHUNKS = MAX_WORLD_BORDER_BLOCKS >> 4;

  private @Nullable String worldName;
  private @Nullable Location lobby;
  private int plotDiameter;
  private int buildAreaDiameter;
  private int maxIslands;
  private int worldBorderDiameter;
  private final Set<IslandPermission> defaultPermissions;

  public IncompleteIslandWorld() {
    worldName = null;
    lobby = null;
    plotDiameter = 50;
    buildAreaDiameter = 32;
    maxIslands = 1_000_000;
    worldBorderDiameter = MAX_WORLD_BORDER_CHUNKS;
    defaultPermissions = new ObjectArraySet<>();
  }

  public Optional<String> getWorldName() {
    return Optional.ofNullable(worldName);
  }

  public Optional<Location> getLobby() {
    return Optional.ofNullable(lobby);
  }

  public int getPlotDiameter() {
    return plotDiameter;
  }

  public int getBuildAreaDiameter() {
    return buildAreaDiameter;
  }

  public int getMaxIslands() {
    return maxIslands;
  }

  public int getWorldBorderDiameter() {
    return worldBorderDiameter;
  }

  public Collection<IslandPermission> getDefaultPermissions() {
    return Collections.unmodifiableSet(defaultPermissions);
  }

  public void setWorldName(String worldName) {
    this.worldName = Objects.requireNonNull(worldName);
  }

  public void setLobby(Location lobby) {
    this.lobby = Objects.requireNonNull(lobby);
  }

  public void setPlotDiameter(int plotDiameter) {
    this.plotDiameter = Math.clamp(plotDiameter, buildAreaDiameter, 1000);
    setMaxIslands(maxIslands);
  }

  public void setBuildAreaDiameter(int buildAreaDiameter) {
    this.buildAreaDiameter = Math.clamp(buildAreaDiameter, 1, plotDiameter);
  }

  public void setMaxIslands(int maxIslands) {
    this.maxIslands = Math.clamp(maxIslands, 1, IslandHelper.getMaxPossibleIslands(worldBorderDiameter, plotDiameter));
  }

  public void setWorldBorderDiameter(int worldBorderDiameter) {
    this.worldBorderDiameter = Math.clamp(worldBorderDiameter, plotDiameter, MAX_WORLD_BORDER_CHUNKS);
    setMaxIslands(maxIslands);
  }

  public boolean toggleDefaultPermission(IslandPermission permission) {
    if (!defaultPermissions.remove(permission)) {
      defaultPermissions.add(permission);
      return true;
    }
    return false;
  }

}
