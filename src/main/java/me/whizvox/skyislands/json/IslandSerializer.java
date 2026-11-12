package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.island.Island;
import me.whizvox.skyislands.island.IslandPermission;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNullByDefault;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NotNullByDefault
public class IslandSerializer implements JsonSerializer<Island>, JsonDeserializer<Island> {

  @Override
  public Island deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    JsonObject root = json.getAsJsonObject();
    UUID ownerId = UUID.fromString(root.get("owner").getAsString());
    int ownerSlot = root.get("ownerSlot").getAsInt();
    String worldKeyStr = root.get("world").getAsString();
    Key worldKey = Objects.requireNonNull(NamespacedKey.fromString(worldKeyStr), "Invalid world key: " + worldKeyStr);
    int worldId = root.get("worldId").getAsInt();
    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(root.get("owner").getAsString()));
    List<IslandPermission> visitorPermissions = new ArrayList<>();
    root.getAsJsonArray("visitorPermissions").forEach(permJson -> visitorPermissions.add(IslandPermission.valueOf(permJson.getAsString())));
    List<UUID> members = new ArrayList<>();
    root.getAsJsonArray("members").forEach(uuidJson -> members.add(UUID.fromString(uuidJson.getAsString())));
    List<UUID> coOwners = new ArrayList<>();
    root.getAsJsonArray("coOwners").forEach(uuidJson -> coOwners.add(UUID.fromString(uuidJson.getAsString())));
    LocalDateTime created =
        LocalDateTime.from(Instant.ofEpochSecond(root.get("created").getAsLong()).atZone(ZoneOffset.UTC));
    return new Island(new Island.OwnerKey(ownerId, ownerSlot), new Island.WorldKey(worldKey, worldId), visitorPermissions, members, coOwners, created);
  }

  @Override
  public JsonElement serialize(Island island, Type type, JsonSerializationContext context) {
    JsonObject root = new JsonObject();
    root.addProperty("owner", island.getOwnerKey().player().toString());
    root.addProperty("ownerSlot", island.getOwnerKey().slot());
    root.addProperty("world", island.worldKey.world().asString());
    root.addProperty("worldId", island.worldKey.id());
    JsonArray visitorPermsArr = new JsonArray();
    island.getVisitorPermissions().forEach(perm -> visitorPermsArr.add(perm.name()));
    root.add("visitorPermissions", visitorPermsArr);
    JsonArray membersArr = new JsonArray();
    island.getMembers().forEach(member -> membersArr.add(member.toString()));
    root.add("members", membersArr);
    JsonArray coOwnersArr = new JsonArray();
    island.getCoOwners().forEach(coOwner -> coOwnersArr.add(coOwner.toString()));
    root.add("coOwners", coOwnersArr);
    root.addProperty("created", island.created.toEpochSecond(ZoneOffset.UTC));
    return root;
  }

}
