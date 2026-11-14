package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.IslandGroup;

import java.lang.reflect.Type;

public class IslandGroupSerializer implements JsonSerializer<IslandGroup>, JsonDeserializer<IslandGroup> {

  @Override
  public IslandGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject src = json.getAsJsonObject();
    IslandGroup group = new IslandGroup(src.get("name").getAsString());
    group.setDisplayName(src.get("displayName").getAsString());
    group.setMaxIslands(src.get("maxIslands").getAsInt());
    group.setMaxMembers(src.get("maxMembers").getAsInt());
    group.setMaxVisitors(src.get("maxVisitors").getAsInt());
    return group;
  }

  @Override
  public JsonElement serialize(IslandGroup src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject root = new JsonObject();
    root.addProperty("name", src.name);
    root.addProperty("displayName", src.getMiniMessageDisplayName());
    root.addProperty("maxIslands", src.getMaxIslands());
    root.addProperty("maxMembers", src.getMaxMembers());
    root.addProperty("maxVisitors", src.getMaxVisitors());
    return root;
  }

}
