package me.whizvox.skyislands.test;

import com.google.gson.Gson;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandPermission;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.island.IslandWorldProperties;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSerializationTests {

  private static final Gson GSON = SkyIslands.createGson(false);

  @BeforeEach
  void setUp() {
    MockBukkit.mock();
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  private static Island island1(@Nullable World world) {
    return new Island(
        new Island.OwnerKey(UUID.fromString("935cfc25-954c-4050-b73e-3174c6ce97d0"), 1),
        new Island.WorldKey(world == null ? NamespacedKey.fromString("skyislands:skyblock") : world.key(), 1)
    );
  }

  private static Island island2(@Nullable World world) {
    return new Island(
        new Island.OwnerKey(UUID.fromString("cc98c56d-4bb3-41e2-aeb5-8d754a3c8cba"), 16),
        new Island.WorldKey(world == null ? NamespacedKey.fromString("skyislands:nether_world") : world.key(), 16792),
        List.of(IslandPermission.BUILD, IslandPermission.HURT_HOSTILE_ENTITIES, IslandPermission.INTERACT_ENTITIES, IslandPermission.INTERACT_CONTAINERS),
        List.of(UUID.fromString("7fc234f6-59b2-4e2d-a7b1-6eebe24cc2ed"), UUID.fromString("a862f697-3e57-4dc6-b9bb-9225088e5143"), UUID.fromString("f8a6606b-a51f-4f77-b73c-779e506bd630")),
        List.of(UUID.fromString("e2bb1c43-7d20-4244-a928-1bcc3d843abf"), UUID.fromString("cf0ff18a-2e92-4f9f-801f-855b8d087756")),
        LocalDateTime.of(2026, 11, 12, 10, 36, 12)
    );
  }

  @Test
  void json_island() {
    Island o1 = island1(null);
    Island o2 = island2(null);
    assertEquals(o1, GSON.fromJson(GSON.toJson(o1), Island.class));
    assertEquals(o2, GSON.fromJson(GSON.toJson(o2), Island.class));
  }

  @Test
  void json_islandWorldProperties() {
    IslandWorldProperties o1 = new IslandWorldProperties();
    IslandWorldProperties o2 = new IslandWorldProperties(1000, 10000, 1_000_002, List.of(IslandPermission.BUILD, IslandPermission.INTERACT_BLOCKS, IslandPermission.HURT_OTHER_ENTITIES));
    assertEquals(o1, GSON.fromJson(GSON.toJson(o1), IslandWorldProperties.class));
    assertEquals(o2, GSON.fromJson(GSON.toJson(o2), IslandWorldProperties.class));
  }

  @Test
  void json_location() {
    World world = MockBukkit.getMock().addSimpleWorld("world");
    Location o1 = new Location(world, 0, 0, 0);
    Location o2 = new Location(world, 500.23, -1234556.554, -64);
    Location o3 = new Location(world, 500.23, -1234556.554, -64, 127.668F, 32.98F);
    assertEquals(o1, GSON.fromJson(GSON.toJson(o1), Location.class));
    assertEquals(o2, GSON.fromJson(GSON.toJson(o2), Location.class));
    assertEquals(o3, GSON.fromJson(GSON.toJson(o3), Location.class));
  }

  @Test
  void json_islandWorld() {
    World world = MockBukkit.getMock().addSimpleWorld("world");
    Island i1 = island1(world);
    Island i2 = island2(world);
    IslandWorld o1 = new IslandWorld(world, new Location(world, 0, 0, 0), new IslandWorldProperties());
    IslandWorld o2 = new IslandWorld(world, new IslandWorldProperties(100, 1000, 1_000_001, List.of(IslandPermission.BUILD, IslandPermission.HURT_FRIENDLY_ENTITIES, IslandPermission.INTERACT_CONTAINERS)), List.of(i1, i2), new Location(world, 50, 40, 11), 2);
    assertEquals(o1, GSON.fromJson(GSON.toJson(o1), IslandWorld.class));
    assertEquals(o2, GSON.fromJson(GSON.toJson(o2), IslandWorld.class));
  }

}
