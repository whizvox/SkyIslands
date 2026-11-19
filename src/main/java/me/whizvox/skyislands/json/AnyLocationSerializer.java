package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.util.AnyLocation;

import java.lang.reflect.Type;

public class AnyLocationSerializer implements JsonSerializer<AnyLocation>, JsonDeserializer<AnyLocation> {

  @Override
  public AnyLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject src = json.getAsJsonObject();
    return new AnyLocation(
        src.get("x").getAsDouble(),
        src.get("y").getAsDouble(),
        src.get("z").getAsDouble(),
        src.has("yaw") ? src.get("yaw").getAsFloat() : 0.0F,
        src.has("pitch") ? src.get("pitch").getAsFloat() : 0.0F
    );
  }

  @Override
  public JsonElement serialize(AnyLocation src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject root = new JsonObject();
    root.addProperty("x", src.x());
    root.addProperty("y", src.y());
    root.addProperty("z", src.z());
    if (Math.abs(src.yaw()) < 1e6) {
      root.addProperty("yaw", src.yaw());
    }
    if (Math.abs(src.pitch()) < 1e6) {
      root.addProperty("pitch", src.pitch());
    }
    return root;
  }

}
