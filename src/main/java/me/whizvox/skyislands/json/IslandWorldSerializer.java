package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.SkyIslands;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.island.IslandWorldProperties;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IslandWorldSerializer implements JsonSerializer<IslandWorld>, JsonDeserializer<IslandWorld> {

  @Override
  public IslandWorld deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject root = json.getAsJsonObject();
    NamespacedKey key = SkyIslands.key(root.get("world").getAsString());
    IslandWorldProperties properties = context.deserialize(root.get("properties"), IslandWorldProperties.class);
    List<Island> islands = new ArrayList<>();
    root.getAsJsonArray("islands").forEach(islandJson -> islands.add(context.deserialize(islandJson, Island.class)));
    Location lobby = context.deserialize(root.get("lobby"), Location.class);
    int lastAssignedId = root.get("lastAssignedId").getAsInt();
    return new IslandWorld(key, properties, islands, lobby, lastAssignedId);
  }

  @Override
  public JsonElement serialize(IslandWorld src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject root = new JsonObject();
    root.addProperty("world", src.getWorld().getKey().toString());
    root.add("properties", context.serialize(src.getProperties()));
    JsonArray islandsArr = new JsonArray();
    src.getIslands().forEach(info -> islandsArr.add(context.serialize(info.island())));
    root.add("islands", islandsArr);
    root.add("lobby", context.serialize(src.getLobby()));
    root.addProperty("lastAssignedId", src.getLastAssignedId());
    return root;
  }

}
