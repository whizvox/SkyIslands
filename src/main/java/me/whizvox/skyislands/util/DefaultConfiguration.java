package me.whizvox.skyislands.util;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.whizvox.skyislands.SkyIslands.warning;

@NotNullByDefault
public abstract class DefaultConfiguration {

    private @Nullable Configuration oldConfig;
    private @Nullable FileConfiguration newConfig;

    public DefaultConfiguration() {
        oldConfig = null;
        newConfig = null;
    }

    public void setup(Configuration config) {
        oldConfig = config;
        newConfig = new YamlConfiguration();
    }

    protected final void setComments(String path, String... comments) {
        if (newConfig != null) {
            newConfig.setComments(path, List.of(comments));
        }
    }

    protected final void set(String path, @Nullable Object value, String... comments) {
        if (oldConfig != null && newConfig != null) {
            if (oldConfig.contains(path)) {
                newConfig.set(path, oldConfig.get(path));
            } else {
                newConfig.set(path, value);
            }
            List<String> actualComments = new ArrayList<>();
            for (String comment : comments) {
                actualComments.addAll(StringHelper.wordWrap(comment, 80));
            }
            newConfig.setComments(path, actualComments);
        }
    }

    public FileConfiguration getConfiguration() {
        return Objects.requireNonNull(newConfig, "Configuration has not been initialized");
    }

    public void save(Path outputFile) {
        if (newConfig != null) {
            try {
                newConfig.save(outputFile.toFile());
            } catch (IOException e) {
                warning(e, "Could not save configuration file", e);
            }
        } else {
            warning("Could not save configuration file as it has not been initialized yet");
        }
    }

    public abstract void generateDefaults();

}
