package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.IslandPermission;
import me.whizvox.skyislands.island.IslandWorldProperties;
import org.bukkit.NamespacedKey;
import org.joml.Vector3i;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IslandWorldPropertiesSerializer implements JsonSerializer<IslandWorldProperties>,
    JsonDeserializer<IslandWorldProperties> {

    @Override
    public IslandWorldProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        List<IslandPermission> defaultPermissions = new ArrayList<>();
        root.getAsJsonArray("defaultPermissions").forEach(permJson ->
            defaultPermissions.add(IslandPermission.valueOf(permJson.getAsString()))
        );
        return new IslandWorldProperties(
            root.get("buildLimitSize").getAsInt(),
            root.get("plotSize").getAsInt(),
            root.get("maxIslands").getAsInt(),
            defaultPermissions,
            NamespacedKey.fromString(root.get("structure").getAsString()),
            new Vector3i(
                root.get("structureOffsetX").getAsInt(),
                root.get("structureOffsetY").getAsInt(),
                root.get("structureOffsetZ").getAsInt()
            )
        );
    }

    @Override
    public JsonElement serialize(IslandWorldProperties src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("buildLimitSize", src.getBuildLimitSize());
        root.addProperty("plotSize", src.getPlotSize());
        root.addProperty("maxIslands", src.getMaxIslands());
        JsonArray defPermsArr = new JsonArray();
        src.getDefaultPermissions().forEach(perm -> defPermsArr.add(perm.name()));
        root.add("defaultPermissions", defPermsArr);
        root.addProperty("structure", src.getStructureKey().asString());
        root.addProperty("structureOffsetX", src.getStructureOffset().x);
        root.addProperty("structureOffsetY", src.getStructureOffset().y);
        root.addProperty("structureOffsetZ", src.getStructureOffset().z);
        return root;
    }

}
