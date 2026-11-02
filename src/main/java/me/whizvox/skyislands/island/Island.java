package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Island {

    private final Set<IslandPermission> visitorPermissions;
    private final Set<OfflinePlayer> members;
    private final Set<OfflinePlayer> coOwners;
    private final LocalDateTime created;
    private OfflinePlayer owner;

    public Island(OfflinePlayer owner, Collection<IslandPermission> visitorPermissions,
                  Collection<OfflinePlayer> members, Collection<OfflinePlayer> coOwners, LocalDateTime created) {
        this.owner = owner;
        this.visitorPermissions = new ObjectArraySet<>(visitorPermissions);
        this.members = new ObjectArraySet<>(members);
        this.coOwners = new ObjectArraySet<>(coOwners);
        this.created = created;
    }

    public Island(OfflinePlayer owner) {
        this(owner, List.of(), List.of(), List.of(), LocalDateTime.now(ZoneOffset.UTC));
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }

    public Set<IslandPermission> getVisitorPermissions() {
        return Collections.unmodifiableSet(visitorPermissions);
    }

    public Set<OfflinePlayer> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public Set<OfflinePlayer> getCoOwners() {
        return Collections.unmodifiableSet(coOwners);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void toggleVisitorPermission(IslandPermission permission) {
        if (!visitorPermissions.remove(permission)) {
            visitorPermissions.add(permission);
        }
    }

    public void addMember(OfflinePlayer player) {
        members.add(player);
    }

    public void removeMember(OfflinePlayer player) {
        members.remove(player);
    }

    public boolean promoteToCoOwner(OfflinePlayer player) {
        if (members.contains(player)) {
            coOwners.add(player);
            members.remove(player);
            return true;
        }
        return false;
    }

    public boolean demoteToMember(OfflinePlayer player) {
        if (coOwners.contains(player)) {
            coOwners.remove(player);
            members.add(player);
            return true;
        }
        return false;
    }

}
