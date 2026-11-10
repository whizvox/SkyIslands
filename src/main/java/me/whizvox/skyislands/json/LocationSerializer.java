package me.whizvox.skyislands.json;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        World world = Bukkit.getWorld(UUID.fromString(root.get("world").getAsString()));
        double x = root.get("x").getAsDouble();
        double y = root.get("y").getAsDouble();
        double z = root.get("z").getAsDouble();
        float pitch = root.get("pitch").getAsFloat();
        float yaw = root.get("yaw").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("world", src.getWorld().getUID().toString());
        root.addProperty("x", src.x());
        root.addProperty("y", src.y());
        root.addProperty("z", src.z());
        root.addProperty("pitch", src.getPitch());
        root.addProperty("yaw", src.getYaw());
        return root;
    }

}
