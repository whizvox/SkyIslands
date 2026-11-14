package me.whizvox.skyislands.core;

import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.island.IslandGroups;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static me.whizvox.skyislands.SkyIslands.severe;

@NotNullByDefault
public class PluginConfiguration {

  private int saveInterval;
  private int defaultMaxIslands;
  private @Nullable Key defaultWorld;
  private final IslandGroups groups;

  public PluginConfiguration(IslandGroups groups, @Nullable Key defaultWorld, int defaultMaxIslands, int saveInterval) {
    this.groups = groups;
    this.defaultWorld = defaultWorld;
    this.defaultMaxIslands = defaultMaxIslands;
    this.saveInterval = saveInterval;
  }

  public PluginConfiguration() {
    saveInterval = 10;
    defaultMaxIslands = 1_000_000;
    defaultWorld = null;
    groups = new IslandGroups();
  }

  public int getSaveInterval() {
    return saveInterval;
  }

  public void setSaveInterval(int minutes) {
    saveInterval = Math.clamp(minutes, 0, 360);
  }

  public int getDefaultMaxIslands() {
    return defaultMaxIslands;
  }

  public void setDefaultMaxIslands(int max) {
    defaultMaxIslands = Math.clamp(max, 0, Integer.MAX_VALUE);
  }

  public Optional<Key> getDefaultWorld() {
    return Optional.ofNullable(defaultWorld);
  }

  public void setDefaultWorld(@Nullable Key defaultWorld) {
    this.defaultWorld = defaultWorld;
  }

  public IslandGroups getGroups() {
    return groups;
  }

  public void load(Path filePath) {
    try {
      PluginConfiguration other = SkyIslands.readJson(filePath, PluginConfiguration.class);
      setSaveInterval(other.saveInterval);
      setDefaultMaxIslands(other.defaultMaxIslands);
      setDefaultWorld(other.defaultWorld);
      groups.reload(other.groups.getGroups());
    } catch (IOException e) {
      severe(e, "Could not load configuration file from {0}", filePath);
    }
  }

  public void save(Path filePath) {
    try {
      SkyIslands.writeJson(filePath, this);
    } catch (IOException e) {
      severe(e, "Could not save plugin configuration to {0}", filePath);
    }
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PluginConfiguration that = (PluginConfiguration) o;
    return saveInterval == that.saveInterval && defaultMaxIslands == that.defaultMaxIslands &&
        Objects.equals(defaultWorld, that.defaultWorld) && Objects.equals(groups, that.groups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(saveInterval, defaultMaxIslands, defaultWorld, groups);
  }

  @Override
  public String toString() {
    return "PluginConfiguration{" +
        "saveInterval=" + saveInterval +
        ", defaultMaxIslands=" + defaultMaxIslands +
        ", defaultWorld=" + defaultWorld +
        ", groups=" + groups +
        '}';
  }

}
