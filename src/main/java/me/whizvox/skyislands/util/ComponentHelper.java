package me.whizvox.skyislands.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

  public static Component joining(String separator, Stream<ComponentLike> stream) {
    List<ComponentLike> list = stream.toList();
    if (list.isEmpty()) {
      return tl("general.emptyList");
    }
    return Component.join(JoinConfiguration.builder().separator(Component.text(separator)).build(), list);
  }

}
