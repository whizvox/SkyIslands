package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandSerializer implements JsonSerializer<Island>, JsonDeserializer<Island> {

    @Override
    public Island deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(root.get("owner").getAsString()));
        List<IslandPermission> visitorPermissions = new ArrayList<>();
        root.getAsJsonArray("visitorPermissions").forEach(permJson -> visitorPermissions.add(IslandPermission.valueOf(permJson.getAsString())));
        List<OfflinePlayer> members = new ArrayList<>();
        root.getAsJsonArray("members").forEach(uuidJson -> members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuidJson.getAsString()))));
        List<OfflinePlayer> coOwners = new ArrayList<>();
        root.getAsJsonArray("coOwners").forEach(uuidJson -> coOwners.add(Bukkit.getOfflinePlayer(UUID.fromString(uuidJson.getAsString()))));
        LocalDateTime created =
            LocalDateTime.from(Instant.ofEpochSecond(root.get("created").getAsLong()).atZone(ZoneOffset.UTC));
        return new Island(owner, visitorPermissions, members, coOwners, created);
    }

    @Override
    public JsonElement serialize(Island island, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("owner", island.getOwner().getUniqueId().toString());
        JsonArray visitorPermsArr = new JsonArray();
        island.getVisitorPermissions().forEach(perm -> visitorPermsArr.add(perm.name()));
        root.add("visitorPermissions", visitorPermsArr);
        JsonArray membersArr = new JsonArray();
        island.getMembers().forEach(member -> membersArr.add(member.getUniqueId().toString()));
        root.add("members", membersArr);
        JsonArray coOwnersArr = new JsonArray();
        island.getCoOwners().forEach(coOwner -> coOwnersArr.add(coOwner.getUniqueId().toString()));
        root.add("coOwners", coOwnersArr);
        root.addProperty("created", island.getCreated().toEpochSecond(ZoneOffset.UTC));
        return root;
    }

}
