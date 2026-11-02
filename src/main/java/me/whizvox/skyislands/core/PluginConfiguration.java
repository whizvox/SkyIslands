package me.whizvox.skyislands.core;

import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.util.DefaultConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static me.whizvox.skyislands.SkyIslands.warning;

@NotNullByDefault
public class PluginConfiguration extends DefaultConfiguration {

    private static final String
        KEY_SAVE_INTERVAL = "skyislands.saveInterval",
        KEY_DEFAULT_WORLD = "skyislands.defaultWorld",
        KEY_DEFAULT_MAX_ISLANDS = "skyislands.defaultMaxIslands";

    private @Nullable World defaultWorld;
    private int saveInterval;
    private int defaultMaxIslands;

    public PluginConfiguration() {
        defaultWorld = null;
        saveInterval = 10;
        defaultMaxIslands = 1_000_000;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int minutes) {
        saveInterval = Math.clamp(minutes, 0, 360);
        getConfiguration().set(KEY_SAVE_INTERVAL, saveInterval);
    }

    public Optional<World> getDefaultWorld() {
        return Optional.ofNullable(defaultWorld);
    }

    public void setDefaultWorld(World world) {
        defaultWorld = world;
        getConfiguration().set(KEY_DEFAULT_WORLD, defaultWorld.getUID().toString());
    }

    public int getDefaultMaxIslands() {
        return defaultMaxIslands;
    }

    public void setDefaultMaxIslands(int max) {
        defaultMaxIslands = Math.clamp(max, 0, Integer.MAX_VALUE);
        getConfiguration().set(KEY_DEFAULT_MAX_ISLANDS, defaultMaxIslands);
    }

    public void load() {
        FileConfiguration config = getConfiguration();

        int saveInterval = config.getInt(KEY_SAVE_INTERVAL);
        this.saveInterval = Math.clamp(saveInterval, 0, 360);
        if (this.saveInterval != saveInterval) {
            config.set(KEY_SAVE_INTERVAL, this.saveInterval);
        }

        String defaultWorldStr = config.getString(KEY_DEFAULT_WORLD);
        if (defaultWorldStr == null || defaultWorldStr.isEmpty()) {
            defaultWorld = null;
        } else {
            boolean loadByName = true;
            if (defaultWorldStr.length() == 36) {
                try {
                    UUID uid = UUID.fromString(defaultWorldStr);
                    defaultWorld = Bukkit.getWorld(uid);
                    if (defaultWorld != null) {
                        loadByName = false;
                    } else {
                        warning("Default sky island world '{0}' was not found", defaultWorldStr);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (loadByName) {
                defaultWorld = Bukkit.getWorld(defaultWorldStr);
                if (defaultWorld == null) {
                    warning("Default sky island '{0}' was not found", defaultWorldStr);
                }
            }
        }
        if (defaultWorld != null && !SkyIslands.getInstance().getIslandsManager().isIslandWorld(defaultWorld)) {
            warning("Default sky island world {0} ({1}) is not actually a sky island world. Resetting to none.",
                defaultWorld.getName(), defaultWorld.getUID());
            defaultWorld = null;
        }

        int defaultMaxIslands = config.getInt(KEY_DEFAULT_MAX_ISLANDS);
        this.defaultMaxIslands = Math.clamp(defaultMaxIslands, 0, Integer.MAX_VALUE);
        if (this.defaultMaxIslands != defaultMaxIslands) {
            config.set(KEY_DEFAULT_MAX_ISLANDS, this.defaultMaxIslands);
        }
    }

    @Override
    public void generateDefaults() {
        set(KEY_SAVE_INTERVAL, 10,
            "Number of minutes between attempts to write sky island data to disk. A value of 0 is a special case, which means the data never gets auto-saved and will only do so upon execution of `/island save` or shutting down the server.",
            "Range: [0, 360]",
            "Default: 10"
        );
        set(KEY_DEFAULT_WORLD, "",
            "Name or UUID of default sky island world. Set to an empty string ('') to set no default.",
            "Default: ''"
        );
        set(KEY_DEFAULT_MAX_ISLANDS, 1_000_000,
            "Default maximum of islands for a sky island world. A value of 0 is a special case, which means there is no default maximum.",
            "Range: [0, 2147483647]",
            "Default: 1000000"
        );
    }

}
