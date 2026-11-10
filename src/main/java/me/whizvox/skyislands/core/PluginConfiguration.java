package me.whizvox.skyislands.core;

import me.whizvox.skyislands.island.IslandGroup;
import me.whizvox.skyislands.island.IslandGroups;
import me.whizvox.skyislands.util.DefaultConfiguration;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@NotNullByDefault
public class PluginConfiguration extends DefaultConfiguration {

    private static final String
        KEY_SAVE_INTERVAL = "skyislands.saveInterval",
        KEY_DEFAULT_WORLD = "skyislands.defaultWorld",
        KEY_DEFAULT_MAX_ISLANDS = "skyislands.defaultMaxIslands";

    private int saveInterval;
    private int defaultMaxIslands;
    private @Nullable Key defaultWorld;
    private final IslandGroups groups;

    public PluginConfiguration() {
        saveInterval = 10;
        defaultMaxIslands = 1_000_000;
        defaultWorld = null;
        groups = new IslandGroups();
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int minutes) {
        saveInterval = Math.clamp(minutes, 0, 360);
        getConfiguration().set(KEY_SAVE_INTERVAL, saveInterval);
    }

    public int getDefaultMaxIslands() {
        return defaultMaxIslands;
    }

    public void setDefaultMaxIslands(int max) {
        defaultMaxIslands = Math.clamp(max, 0, Integer.MAX_VALUE);
        getConfiguration().set(KEY_DEFAULT_MAX_ISLANDS, defaultMaxIslands);
    }

    public Optional<Key> getDefaultWorld() {
        return Optional.ofNullable(defaultWorld);
    }

    public void setDefaultWorld(@Nullable Key defaultWorld) {
        this.defaultWorld = defaultWorld;
    }

    public IslandGroups getGroups() {
        return groups;
    }

    public void load() {
        FileConfiguration config = getConfiguration();

        int saveInterval = config.getInt(KEY_SAVE_INTERVAL);
        this.saveInterval = Math.clamp(saveInterval, 0, 360);
        if (this.saveInterval != saveInterval) {
            config.set(KEY_SAVE_INTERVAL, this.saveInterval);
        }

        int defaultMaxIslands = config.getInt(KEY_DEFAULT_MAX_ISLANDS);
        this.defaultMaxIslands = Math.clamp(defaultMaxIslands, 0, Integer.MAX_VALUE);
        if (this.defaultMaxIslands != defaultMaxIslands) {
            config.set(KEY_DEFAULT_MAX_ISLANDS, this.defaultMaxIslands);
        }

        String defaultWorldStr = config.getString(KEY_DEFAULT_WORLD);
        if (defaultWorldStr != null && defaultWorldStr.isEmpty()) {
            Key key = NamespacedKey.fromString(defaultWorldStr);
            if (key != null) {
                defaultWorld = key;
            } else {
                config.set(KEY_DEFAULT_WORLD, "");
                defaultWorld = null;
            }
        } else {
            defaultWorld = null;
        }

        //noinspection unchecked
        List<IslandGroup> groups = (List<IslandGroup>) config.getList("groups", List.of());
        this.groups.reload(groups);
    }

    @Override
    public void generateDefaults() {
        set(KEY_SAVE_INTERVAL, 10,
            "Number of minutes between attempts to write sky island data to disk. A value of 0 is a special case, which means the data never gets auto-saved and will only do so upon execution of `/island save` or shutting down the server.",
            "Range: [0, 360]",
            "Default: 10"
        );
        set(KEY_DEFAULT_MAX_ISLANDS, 1_000_000,
            "Default maximum of islands for a sky island world. A value of 0 is a special case, which means there is no default maximum.",
            "Range: [0, 2147483647]",
            "Default: 1000000"
        );
        set(KEY_DEFAULT_WORLD, "",
            "Default sky island world. Set to an empty string for none.",
            "Default: ''"
        );
        set("skyislands.group.default.displayName", "default");
        set("skyislands.group.default.maxIslands", 1);
        set("skyislands.group.default.maxMembers", 5);
        set("skyislands.group.default.maxVisitors", 16);
        setComments("skyislands.group", "A list of all groups that are present within the sky islands system. This is meant to only be modified via in-game commands.");
    }

}
