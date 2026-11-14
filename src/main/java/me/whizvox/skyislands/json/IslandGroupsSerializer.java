package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.IslandGroup;
import me.whizvox.skyislands.island.IslandGroups;

import java.lang.reflect.Type;

public class IslandGroupsSerializer implements JsonSerializer<IslandGroups>, JsonDeserializer<IslandGroups> {

  @Override
  public IslandGroups deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonArray src = json.getAsJsonArray();
    IslandGroups dst = new IslandGroups();
    src.forEach(groupJson -> dst.addGroup(context.deserialize(groupJson, IslandGroup.class)));
    return dst;
  }

  @Override
  public JsonElement serialize(IslandGroups src, Type typeOfSrc, JsonSerializationContext context) {
    JsonArray dst = new JsonArray();
    src.getGroups().forEach(group -> dst.add(context.serialize(group)));
    return dst;
  }

}
