package me.whizvox.skyislands.island;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class IslandGroups {

  private final Map<String, IslandGroup> groups;

  public IslandGroups() {
    groups = new Object2ObjectOpenHashMap<>();
  }

  public void reload(Collection<IslandGroup> groups) {
    this.groups.clear();
    groups.forEach(this::addGroup);
  }

  public Collection<IslandGroup> getGroups() {
    return groups.values();
  }

  public Optional<IslandGroup> getGroup(String name) {
    return Optional.ofNullable(groups.get(name));
  }

  public boolean addGroup(IslandGroup group) {
    return groups.putIfAbsent(group.name, group) == null;
  }

  public boolean deleteGroup(String name) {
    return groups.remove(name) != null;
  }

  public Stream<IslandGroup> getGroups(Permissible player) {
    return groups.values().stream()
        .filter(group -> player.hasPermission("skyislands.group." + group.name));
  }

  private int getMax(Permissible player, Function<IslandGroup, Integer> getter) {
    return getGroups(player)
        .map(getter)
        .max(Comparator.naturalOrder())
        .orElse(0);
  }

  public int getMaxIslands(Permissible player) {
    return getMax(player, IslandGroup::getMaxIslands);
  }

  public int getMaxMembers(Permissible player) {
    return getMax(player, IslandGroup::getMaxMembers);
  }

  public int getMaxVisitors(Permissible player) {
    return getMax(player, IslandGroup::getMaxVisitors);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    IslandGroups that = (IslandGroups) o;
    return that.groups.values().containsAll(groups.values());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(groups.values());
  }

  @Override
  public String toString() {
    return "IslandGroups{" +
        "groups=" + groups.values().stream().toList() +
        '}';
  }

}
