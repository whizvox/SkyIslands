package me.whizvox.skyislands.json;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandWorld;
import me.whizvox.skyislands.island.IslandWorldProperties;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class IslandWorldSerializer implements JsonSerializer<IslandWorld>, JsonDeserializer<IslandWorld> {

    @Override
    public IslandWorld deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        World world = Bukkit.getWorld(UUID.fromString(root.get("world").getAsString()));
        IslandWorldProperties properties = context.deserialize(root.get("properties"), IslandWorldProperties.class);
        Int2ObjectMap<Island> islands = new Int2ObjectArrayMap<>();
        root.getAsJsonArray("islands").forEach(islandJson -> {
            JsonObject islandObj = islandJson.getAsJsonObject();
            int id = islandObj.get("id").getAsInt();
            Island island = context.deserialize(islandObj.get("island"), Island.class);
            islands.put(id, island);
        });
        int lastAssignedId = root.get("lastAssignedId").getAsInt();
        return new IslandWorld(world, properties, islands, lastAssignedId);
    }

    @Override
    public JsonElement serialize(IslandWorld src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("world", src.getWorld().getUID().toString());
        root.add("properties", context.serialize(src.getProperties()));
        JsonArray islandsArr = new JsonArray();
        src.getIslands().forEach(info -> {
            JsonObject islandJson = new JsonObject();
            islandJson.addProperty("id", info.id());
            islandJson.add("island", context.serialize(info.island()));
            islandsArr.add(islandJson);
        });
        root.add("islands", islandsArr);
        root.addProperty("lastAssignedId", src.getLastAssignedId());
        return root;
    }

}
