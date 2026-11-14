package me.whizvox.skyislands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.whizvox.skyislands.command.util.TranslatableCommandExceptionType;
import me.whizvox.skyislands.core.DefaultMessages;
import me.whizvox.skyislands.core.PluginConfiguration;
import me.whizvox.skyislands.core.command.island.IslandCommand;
import me.whizvox.skyislands.i18n.HybridTranslationStore;
import me.whizvox.skyislands.i18n.Messages;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.island.IslandWorldProperties;
import me.whizvox.skyislands.island.IslandWorldsManager;
import me.whizvox.skyislands.json.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@NotNullByDefault
public final class SkyIslands extends JavaPlugin {

  private final Gson gson;
  private final HybridTranslationStore hybridTranslationStore;
  private final PluginConfiguration pluginConfig;
  private final IslandWorldsManager islandsWorldManager;
  private @Nullable Path langDir, worldsDir;
  private int prevSaveInterval;
  private @Nullable ScheduledTask saveTask;

  public SkyIslands() {
    //noinspection ConstantValue
    assert instance == null : "SkyIslands plugin instance has already been defined";
    instance = this;
    gson = createGson(true);
    hybridTranslationStore = new HybridTranslationStore("skyislands", Locale.ENGLISH, MiniMessage.miniMessage());
    pluginConfig = new PluginConfiguration();
    islandsWorldManager = new IslandWorldsManager();
    langDir = null;
    worldsDir = null;
    prevSaveInterval = -1;
    saveTask = null;
  }

  private void doSave() {
    if (worldsDir != null) {
      if (!islandsWorldManager.save(worldsDir, pluginConfig)) {
        severe("Island worlds are still somehow saving. Did something crash?");
      }
    }
    pluginConfig.save(getDataPath().resolve("config.yml"));
  }

  public Gson getGson() {
    return gson;
  }

  public PluginConfiguration getPluginConfig() {
    return pluginConfig;
  }

  public IslandWorldsManager getIslandsWorldManager() {
    return islandsWorldManager;
  }

  public void reload() {
    // configuration file
    reloadConfig(); // reload configuration file
    pluginConfig.setup(getConfig()); // re-initialize to use the reloaded configuration file
    pluginConfig.generateDefaults(); // generate default values using the reloaded configuration file
    pluginConfig.load(); // reload the plugin-specific configuration values
    pluginConfig.save(getDataPath().resolve("config.yml")); // save in case any invalid values were corrected

    // sky island world file
    if (worldsDir != null && Files.isRegularFile(worldsDir)) {
      islandsWorldManager.load(worldsDir, pluginConfig);
    }

    // language files
    boolean loadTranslations = Files.isDirectory(Objects.requireNonNull(langDir, "Plugin is not initialized"));
    Path fallbackLangPath = langDir.resolve("en.json");
    if (!Files.isRegularFile(fallbackLangPath)) {
      try {
        if (!loadTranslations) {
          Files.createDirectories(langDir);
        }
        var messages = new DefaultMessages();
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(fallbackLangPath))) {
          gson.toJson(messages.getMessages(), writer);
        }
        hybridTranslationStore.registerAll(messages.getMessages(), Locale.ENGLISH);
      } catch (IOException e) {
        throw new RuntimeException("Could not create lang directory at " + langDir, e);
      }
    }
    if (loadTranslations) {
      hybridTranslationStore.reload(langDir);
    }

    // one of the config options deals with the save interval
    if (prevSaveInterval != pluginConfig.getSaveInterval()) {
      prevSaveInterval = pluginConfig.getSaveInterval();
      if (saveTask != null) {
        saveTask.cancel();
        saveTask = null;
        info("Island worlds save task has been cancelled");
      }
      if (prevSaveInterval > 0) {
        saveTask = Bukkit.getAsyncScheduler().runAtFixedRate(this, _ -> doSave(), prevSaveInterval, prevSaveInterval, TimeUnit.MINUTES);
        info("Scheduled island worlds save task to run every {0} minutes", prevSaveInterval);
      }
    }
  }

  @Override
  public void onEnable() {
    langDir = getDataPath().resolve("lang");
    worldsDir = getDataPath().resolve("worlds");
    reload();
    hybridTranslationStore.registerGlobal();

    getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      var islandCommand = new IslandCommand("island");
      islandCommand.addAllSubCommands();
      islandCommand.register(event.registrar());
    });

    // register skyblock island structure
    try (InputStream in = getResource("sky_island_basic.nbt")) {
      if (in != null) {
        Structure islandStructure = Bukkit.getStructureManager().loadStructure(in);
        Bukkit.getStructureManager().registerStructure(
            Objects.requireNonNull(key("sky_island_basic")),
            islandStructure
        );
      } else {
        throw new RuntimeException("Sky island structure file is missing. This should not happen!");
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not load sky island structure", e);
    }
  }

  @Override
  public void onDisable() {
    doSave();
    if (saveTask != null) {
      saveTask.cancel();
      saveTask = null;
    }
  }

  public static Gson createGson(boolean enablePrettyPrinting) {
    var builder = new GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(Location.class, new LocationSerializer())
        .registerTypeAdapter(Messages.class, new MessagesSerializer())
        .registerTypeAdapter(Island.class, new IslandSerializer())
        .registerTypeAdapter(IslandWorldProperties.class, new IslandWorldPropertiesSerializer())
        .registerTypeAdapter(IslandWorld.class, new IslandWorldSerializer());
    if (enablePrettyPrinting) {
      builder.setPrettyPrinting();
    }
    return builder.create();
  }

  @SuppressWarnings("DataFlowIssue")
  private static SkyIslands instance = null;

  public static SkyIslands getInstance() {
    return instance;
  }

  public static void log(Level level, String pattern, Object[] params, @Nullable Throwable throwable) {
    LogRecord record = new LogRecord(level, pattern);
    record.setThrown(throwable);
    record.setParameters(params);
    instance.getLogger().log(record);
  }

  public static void fine(String format, Object... params) {
    instance.getLogger().log(Level.FINE, format, params);
  }

  public static void info(String format, Object... params) {
    instance.getLogger().log(Level.INFO, format, params);
  }

  public static void warning(String format, Object... params) {
    instance.getLogger().log(Level.WARNING, format, params);
  }

  public static void warning(Throwable throwable, String format, Object... params) {
    log(Level.WARNING, format, params, throwable);
  }

  public static void severe(String format, Object... params) {
    log(Level.SEVERE, format, params, null);
  }

  /* Helper methods */

  public static void severe(Throwable throwable, String format, Object... params) {
    log(Level.SEVERE, format, params, throwable);
  }

  public static TranslatableComponent tl(String key) {
    return Component.translatable("skyislands." + key);
  }

  public static Component tl(String key, Map<String, Object> args) {
    if (args.isEmpty()) {
      return tl(key);
    }
    List<ComponentLike> tlArgs = new ArrayList<>(args.size());
    args.forEach((name, value) -> {
      if (value instanceof ComponentLike compLike) {
        tlArgs.add(Argument.component(name, compLike));
      } else {
        tlArgs.add(Argument.string(name, String.valueOf(value)));
      }
    });
    return Component.translatable("skyislands." + key, tlArgs);
  }

  public static CommandSyntaxException cmdex(String key, Map<String, Object> args) {
    return new TranslatableCommandExceptionType(key).create(args);
  }

  public static CommandSyntaxException cmdex(String key) {
    return cmdex(key, Map.of());
  }

  public static Permission perm(String name) {
    return new Permission("skyislands." + name);
  }

  public static @Nullable NamespacedKey key(String name) {
    return NamespacedKey.fromString(name, instance);
  }

}
