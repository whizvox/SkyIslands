package me.whizvox.skyislands.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.Map;

import static me.whizvox.skyislands.SkyIslands.tl;

public class ComponentHelper {

  public static Component location(Location loc) {
    return tl("general.location", Map.of(
        "x", "%.1f".formatted(loc.x()),
        "y", "%.1f".formatted(loc.y()),
        "z", "%.1f".formatted(loc.z()),
        "yaw", "%.1f".formatted(loc.getYaw()),
        "pitch", "%.1f".formatted(loc.getPitch()),
        "world_name", loc.getWorld().key()
    ));
  }

}
