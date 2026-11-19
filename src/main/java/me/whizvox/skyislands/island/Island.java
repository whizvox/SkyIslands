package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.skyislands.util.AnyLocation;
import me.whizvox.skyislands.util.Lazy;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@NotNullByDefault
public final class Island {

  private OwnerKey ownerKey;
  public final WorldKey worldKey;
  private final Set<IslandPermission> visitorPermissions;
  private final Set<UUID> members;
  private final Set<UUID> coOwners;
  private AnyLocation home;
  private final Lazy<Location> homeLazy;
  public final LocalDateTime created;

  public Island(OwnerKey ownerKey, WorldKey worldKey, AnyLocation home, Collection<IslandPermission> visitorPermissions, Collection<UUID> members, Collection<UUID> coOwners, LocalDateTime created) {
    this.ownerKey = ownerKey;
    this.worldKey = worldKey;
    this.home = home;
    this.homeLazy = Lazy.of(() -> this.home.toLocation(Objects.requireNonNull(Bukkit.getWorld(worldKey.world), "Could not find world: " + worldKey.world)));
    this.visitorPermissions = new ObjectArraySet<>(visitorPermissions);
    this.members = new ObjectArraySet<>(members);
    this.coOwners = new ObjectArraySet<>(coOwners);
    this.created = created;
  }

  public Island(OwnerKey ownerKey, WorldKey worldKey, AnyLocation home) {
    this(ownerKey, worldKey, home, List.of(), List.of(), List.of(), LocalDateTime.now(ZoneOffset.UTC));
  }

  public OwnerKey getOwnerKey() {
    return ownerKey;
  }

  public void setOwnerKey(OwnerKey ownerKey) {
    this.ownerKey = ownerKey;
  }

  public AnyLocation getAnyHome() {
    return home;
  }

  public Location getHome() {
    return homeLazy.get().clone();
  }

  public void setHome(AnyLocation home) {
    this.home = home;
    this.homeLazy.clear();
  }

  public Set<IslandPermission> getVisitorPermissions() {
    return Collections.unmodifiableSet(visitorPermissions);
  }

  public Set<UUID> getMembers() {
    return Collections.unmodifiableSet(members);
  }

  public Set<UUID> getCoOwners() {
    return Collections.unmodifiableSet(coOwners);
  }

  public void toggleVisitorPermission(IslandPermission permission) {
    if (!visitorPermissions.remove(permission)) {
      visitorPermissions.add(permission);
    }
  }

  public void addMember(OfflinePlayer player) {
    members.add(player.getUniqueId());
  }

  public void removeMember(OfflinePlayer player) {
    members.remove(player.getUniqueId());
  }

  public boolean promoteToCoOwner(OfflinePlayer player) {
    UUID id = player.getUniqueId();
    if (members.contains(id)) {
      coOwners.add(id);
      members.remove(id);
      return true;
    }
    return false;
  }

  public boolean demoteToMember(OfflinePlayer player) {
    UUID id = player.getUniqueId();
    if (coOwners.contains(id)) {
      coOwners.remove(id);
      members.add(id);
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Island island = (Island) o;
    return ownerKey.equals(island.ownerKey) &&
        worldKey.equals(island.worldKey) &&
        home.equals(island.home) &&
        visitorPermissions.equals(island.visitorPermissions) &&
        members.equals(island.members) &&
        coOwners.equals(island.coOwners) &&
        created.truncatedTo(ChronoUnit.SECONDS).equals(island.created.truncatedTo(ChronoUnit.SECONDS));
  }

  @Override
  public int hashCode() {
    return Objects.hash(ownerKey, worldKey, home, visitorPermissions, members, coOwners, created);
  }

  @Override
  public String toString() {
    return "Island{" +
        "ownerKey=" + ownerKey +
        ", worldKey=" + worldKey +
        ", home=" + home +
        ", visitorPermissions=" + visitorPermissions +
        ", members=" + members +
        ", coOwners=" + coOwners +
        ", created=" + created +
        '}';
  }

  public record OwnerKey(UUID player, int slot) {
  }

  public record WorldKey(Key world, int id) {
  }

}
