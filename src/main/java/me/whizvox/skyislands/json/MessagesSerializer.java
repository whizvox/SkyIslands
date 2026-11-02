package me.whizvox.skyislands.json;

import com.google.gson.*;
import me.whizvox.skyislands.util.i18n.Messages;

import java.lang.reflect.Type;

public class MessagesSerializer implements JsonSerializer<Messages>, JsonDeserializer<Messages> {

    @Override
    public Messages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Messages dst = new Messages();
        JsonObject root = json.getAsJsonObject();
        root.getAsJsonObject("miniMessage").entrySet().forEach(entry ->
            dst.miniMessageTranslations().put(entry.getKey(), entry.getValue().getAsString())
        );
        root.getAsJsonObject("messageFormat").entrySet().forEach(entry ->
            dst.messageFormatTranslations().put(entry.getKey(), entry.getValue().getAsString())
        );
        return dst;
    }

    @Override
    public JsonElement serialize(Messages src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        JsonObject componentsObj = new JsonObject();
        src.miniMessageTranslations().forEach(componentsObj::addProperty);
        root.add("miniMessage", componentsObj);
        JsonObject formatsObj = new JsonObject();
        src.messageFormatTranslations().forEach(formatsObj::addProperty);
        root.add("messageFormat", formatsObj);
        return root;
    }

}
