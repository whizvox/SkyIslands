package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.IslandPermission;
import me.whizvox.skyislands.island.IslandWorldProperties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IslandWorldPropertiesSerializer implements JsonSerializer<IslandWorldProperties>,
    JsonDeserializer<IslandWorldProperties> {

    @Override
    public IslandWorldProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        int buildLimitSize = root.get("buildLimitSize").getAsInt();
        int plotSize = root.get("plotSize").getAsInt();
        int maxIslands = root.get("maxIslands").getAsInt();
        List<IslandPermission> defaultPermissions = new ArrayList<>();
        root.getAsJsonArray("defaultPermissions").forEach(permJson ->
            defaultPermissions.add(IslandPermission.valueOf(permJson.getAsString()))
        );
        return new IslandWorldProperties(buildLimitSize, plotSize, maxIslands, defaultPermissions);
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
        return root;
    }

}
