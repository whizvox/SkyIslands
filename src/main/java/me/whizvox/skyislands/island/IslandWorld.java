package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.skyislands.util.Box2d;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Stream;

public final class IslandWorld {

  private final World world;
  private final IslandWorldProperties properties;
  private final Int2ObjectMap<IslandInfo> islandsIdMap;
  private final Object2ObjectMap<Vector2i, IslandInfo> islandsPositionMap;
  private final Object2ObjectMap<UUID, List<IslandInfo>> islandsOwnersMap;
  private final Map<Island.OwnerKey, IslandInfo> islandsOwnerKeysMap;
  private Location lobby;
  private int lastAssignedId;

  public IslandWorld(World world,
                     IslandWorldProperties properties,
                     List<Island> islands,
                     Location lobby,
                     int lastAssignedId) {
    this.world = world;
    this.properties = properties;
    this.lobby = lobby;
    this.lastAssignedId = lastAssignedId;
    islandsIdMap = new Int2ObjectOpenHashMap<>();
    islandsPositionMap = new Object2ObjectOpenHashMap<>();
    islandsOwnersMap = new Object2ObjectOpenHashMap<>();
    islandsOwnerKeysMap = new Object2ObjectOpenHashMap<>();
    islands.forEach(island -> {
      Vector2i pos = getIslandPosition(island.worldKey.id());
      var areas = getPlotAndBuildAreas(pos);
      IslandInfo info = new IslandInfo(island, areas.first(), areas.second());
      islandsIdMap.put(island.worldKey.id(), info);
      islandsPositionMap.put(pos, info);
      islandsOwnersMap.computeIfAbsent(island.getOwnerKey().player(), _ -> new ArrayList<>()).add(info);
      islandsOwnerKeysMap.put(island.getOwnerKey(), info);
    });
  }

  public IslandWorld(World world, Location lobby, IslandWorldProperties properties) {
    this(world, properties, List.of(), lobby, 0);
  }

  public World getWorld() {
    return world;
  }

  public IslandWorldProperties getProperties() {
    return properties;
  }

  public Stream<IslandInfo> getIslands() {
    return islandsIdMap.values().stream();
  }

  public Location getLobby() {
    return lobby.clone();
  }

  public int getLastAssignedId() {
    return lastAssignedId;
  }

  public Vector2i getIslandPosition(int id) {
    if (id == 0) {
      return new Vector2i(0, 0);
    }
    int radius = (int) ((Math.sqrt(id) - 1) / 2) + 1;
    int offset = (int) Math.pow((radius - 1) * 2 + 1, 2);
    int ringIndex = id - offset;
    int part = ringIndex / (radius * 2);
    return switch (part) {
      case 0 -> new Vector2i(-radius + ringIndex, -radius);
      case 1 -> new Vector2i(radius, -radius + ringIndex % (radius * 2));
      case 2 -> new Vector2i(radius - ringIndex % (radius * 2), radius);
      default -> new Vector2i(-radius, radius - ringIndex % (radius * 2));
    };
  }

  private Pair<Box2d, Box2d> getPlotAndBuildAreas(Vector2i pos) {
    Box2d plotArea = new Box2d(
        pos.x * 16 * properties.getPlotSize(),
        pos.y * 16 * properties.getPlotSize(),
        (pos.x + 1) * 16 * properties.getPlotSize(),
        (pos.y + 1) * 16 * properties.getPlotSize()
    );
    int delta = properties.getPlotSize() - properties.getBuildLimitSize();
    Box2d buildArea = new Box2d(
        plotArea.x1() + delta * 16,
        plotArea.z1() + delta * 16,
        plotArea.x2() - delta * 16,
        plotArea.z2() - delta * 16
    );
    return Pair.of(plotArea, buildArea);
  }

  public Optional<IslandInfo> getIslandIdByLocation(Location loc) {
    if (loc.getWorld().getUID().equals(world.getUID())) {
      Chunk chunk = loc.getChunk();
      Vector2i position = new Vector2i(chunk.getX() / properties.getPlotSize(),
          chunk.getZ() / properties.getPlotSize());
      return Optional.ofNullable(islandsPositionMap.get(position));
    }
    return Optional.empty();
  }

  public List<IslandInfo> getIslandsByOwner(UUID playerId) {
    return Collections.unmodifiableList(islandsOwnersMap.getOrDefault(playerId, List.of()));
  }

  public Optional<IslandInfo> getIsland(int id) {
    return Optional.ofNullable(islandsIdMap.get(id));
  }

  public Optional<IslandInfo> getIsland(Island.OwnerKey key) {
    return Optional.ofNullable(islandsOwnerKeysMap.get(key));
  }

  public boolean setLobby(Location lobby) {
    if (!lobby.getWorld().getUID().equals(world.getUID())) {
      this.lobby = lobby;
      return true;
    }
    return false;
  }

  /**
   * Creates a new island with a specified owner.
   * <br>
   * <b>Important to note!</b> This does NOT check for permissions nor whether the slot is valid.
   *
   * @param owner The owner of the island
   * @param slot The owner's island slot
   * @return The newly-created island, or empty if the maximum number of islands has been reached
   */
  public Optional<IslandInfo> createIsland(Player owner, int slot) {
    int id = lastAssignedId;
    do {
      id++;
    } while (islandsIdMap.containsKey(id));
    if (id > properties.getMaxIslands()) {
      return Optional.empty();
    }
    Vector2i position = getIslandPosition(id);
    // sanity check
    if (islandsPositionMap.containsKey(position)) {
      throw new IllegalStateException(
          "Island position is somehow taken even though the ID is not: id=%d, position=%s".formatted(id, position)
      );
    }
    var areas = getPlotAndBuildAreas(position);
    Island.OwnerKey ok = new Island.OwnerKey(owner.getUniqueId(), slot);
    Island.WorldKey wk = new Island.WorldKey(world.getKey(), id);
    Island island = new Island(ok, wk);
    IslandInfo info = new IslandInfo(island, areas.first(), areas.second());
    islandsIdMap.put(id, info);
    islandsPositionMap.put(position, info);
    islandsOwnersMap.computeIfAbsent(owner.getUniqueId(), _ -> new ArrayList<>()).add(info);
    islandsOwnerKeysMap.put(ok, info);
    lastAssignedId = id;
    return Optional.of(info);
  }

  public boolean deleteIsland(int id) {
    if (islandsIdMap.containsKey(id)) {
      islandsPositionMap.remove(getIslandPosition(id));
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    IslandWorld that = (IslandWorld) o;
    return lastAssignedId == that.lastAssignedId &&
        world.key().equals(that.world.key()) &&
        properties.equals(that.properties) &&
        islandsIdMap.values().stream().allMatch(info -> Objects.equals(info, that.islandsIdMap.get(info.island().worldKey.id()))) &&
        lobby.equals(that.lobby);
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, properties, islandsIdMap, islandsPositionMap, islandsOwnersMap, islandsOwnerKeysMap, lobby, lastAssignedId);
  }

  @Override
  public String toString() {
    return "IslandWorld{" +
        "world=" + world.key() +
        ", properties=" + properties +
        ", islandsIdMap=" + islandsIdMap +
        ", islandsPositionMap=" + islandsPositionMap +
        ", islandsOwnersMap=" + islandsOwnersMap +
        ", islandsOwnerKeysMap=" + islandsOwnerKeysMap +
        ", lobby=" + lobby +
        ", lastAssignedId=" + lastAssignedId +
        '}';
  }

}
