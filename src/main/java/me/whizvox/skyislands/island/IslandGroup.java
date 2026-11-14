package me.whizvox.skyislands.island;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@NotNullByDefault
public class IslandGroup {

  public final String name;
  private String mmDisplayName;
  private Component displayName;
  private int maxIslands;
  private int maxMembers;
  private int maxVisitors;

  public IslandGroup(String name, String mmDisplayName, int maxIslands, int maxMembers, int maxVisitors) {
    this.name = name;
    this.mmDisplayName = mmDisplayName;
    this.displayName = MiniMessage.miniMessage().deserialize(mmDisplayName);
    this.maxIslands = maxIslands;
    this.maxMembers = maxMembers;
    this.maxVisitors = maxVisitors;
  }

  public IslandGroup(String name) {
    this(name, name, 1, 5, 16);
  }

  public String getMiniMessageDisplayName() {
    return mmDisplayName;
  }

  public Component getDisplayName() {
    return displayName;
  }

  public int getMaxIslands() {
    return maxIslands;
  }

  public int getMaxMembers() {
    return maxMembers;
  }

  public int getMaxVisitors() {
    return maxVisitors;
  }

  public void setDisplayName(String mmDisplayName) {
    this.displayName = MiniMessage.miniMessage().deserialize(mmDisplayName);
    this.mmDisplayName = mmDisplayName;
  }

  public void setMaxIslands(int maxIslands) {
    this.maxIslands = maxIslands;
  }

  public void setMaxMembers(int maxMembers) {
    this.maxMembers = maxMembers;
  }

  public void setMaxVisitors(int maxVisitors) {
    this.maxVisitors = maxVisitors;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    IslandGroup that = (IslandGroup) o;
    return maxIslands == that.maxIslands && maxMembers == that.maxMembers && maxVisitors == that.maxVisitors &&
        Objects.equals(name, that.name) && Objects.equals(mmDisplayName, that.mmDisplayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, mmDisplayName, maxIslands, maxMembers, maxVisitors);
  }

  @Override
  public String toString() {
    return "IslandGroup{" +
        "name='" + name + '\'' +
        ", mmDisplayName='" + mmDisplayName + '\'' +
        ", maxIslands=" + maxIslands +
        ", maxMembers=" + maxMembers +
        ", maxVisitors=" + maxVisitors +
        '}';
  }

}
