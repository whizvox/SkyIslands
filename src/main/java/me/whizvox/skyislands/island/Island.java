package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@NotNullByDefault
public final class Island {

  private OwnerKey ownerKey;
  public final WorldKey worldKey;
  private final Set<IslandPermission> visitorPermissions;
  private final Set<UUID> members;
  private final Set<UUID> coOwners;
  public final LocalDateTime created;

  public Island(OwnerKey ownerKey, WorldKey worldKey, Collection<IslandPermission> visitorPermissions, Collection<UUID> members, Collection<UUID> coOwners, LocalDateTime created) {
    this.ownerKey = ownerKey;
    this.worldKey = worldKey;
    this.visitorPermissions = new ObjectArraySet<>(visitorPermissions);
    this.members = new ObjectArraySet<>(members);
    this.coOwners = new ObjectArraySet<>(coOwners);
    this.created = created;
  }

  public Island(OwnerKey ownerKey, WorldKey worldKey) {
    this(ownerKey, worldKey, List.of(), List.of(), List.of(), LocalDateTime.now(ZoneOffset.UTC));
  }

  public OwnerKey getOwnerKey() {
    return ownerKey;
  }

  public void setOwnerKey(OwnerKey ownerKey) {
    this.ownerKey = ownerKey;
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

  public record OwnerKey(UUID player, int slot) {
  }

  public record WorldKey(Key world, int id) {
  }

}
