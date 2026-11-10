package me.whizvox.skyislands.island;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;

@NotNullByDefault
public class IslandGroup implements ConfigurationSerializable {

    public final String name;

    private String mmDisplayName;
    private Component displayName;
    private int maxIslands;
    private int maxMembers;
    private int maxVisitors;

    private IslandGroup(String name, String mmDisplayName, int maxIslands, int maxMembers, int maxVisitors) {
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

    public IslandGroup(Map<String, Object> map) {
        String name = (String) map.get("name");
        assert name != null;
        this(
                name,
                (String) map.getOrDefault("displayName", name),
                (Integer) map.getOrDefault("maxIslands", 0),
                (Integer) map.getOrDefault("maxMembers", 0),
                (Integer) map.getOrDefault("maxVisitors", 0)
        );
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
    public Map<String, Object> serialize() {
        return Map.of(
                "name", name,
                "displayName", mmDisplayName,
                "maxIslands", maxIslands,
                "maxMembers", maxMembers,
                "maxVisitors", maxVisitors
        );
    }

}
