package me.whizvox.skyislands.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public record AnyLocation(double x, double y, double z, float yaw, float pitch) {

  public AnyLocation(double x, double y, double z) {
    this(x, y, z, 0.0F, 0.0F);
  }

  public Location toLocation(@Nullable World world) {
    return new Location(world, x, y, z, yaw, pitch);
  }

  public static AnyLocation from(Location loc) {
    return new AnyLocation(loc.x(), loc.y(), loc.z(), loc.getYaw(), loc.getPitch());
  }

}
