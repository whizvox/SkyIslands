package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.core.PluginConfiguration;
import me.whizvox.skyislands.island.IslandGroups;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNullByDefault;

import java.lang.reflect.Type;

@NotNullByDefault
public class PluginConfigurationSerializer implements JsonSerializer<PluginConfiguration>, JsonDeserializer<PluginConfiguration> {

  @Override
  public PluginConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject src = json.getAsJsonObject();
    String defaultWorldStr = src.get("defaultWorld").getAsString();
    Key defaultWorld;
    if (defaultWorldStr.isEmpty()) {
      defaultWorld = null;
    } else {
      defaultWorld = NamespacedKey.fromString(defaultWorldStr);
    }
    return new PluginConfiguration(
        context.deserialize(src.get("groups"), IslandGroups.class),
        defaultWorld,
        src.get("defaultMaxIslands").getAsInt(),
        src.get("saveInterval").getAsInt()
    );
  }

  @Override
  public JsonElement serialize(PluginConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject root = new JsonObject();
    root.addProperty("defaultWorld", src.getDefaultWorld().map(Key::asString).orElse(""));
    root.addProperty("saveInterval", src.getSaveInterval());
    root.addProperty("defaultMaxIslands", src.getDefaultMaxIslands());
    root.add("groups", context.serialize(src.getGroups()));
    return root;
  }

}
