package me.whizvox.skyislands.command.util;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;

import java.util.Map;

import static me.whizvox.skyislands.SkyIslands.tl;

public record TranslatableCommandExceptionType(String key) implements CommandExceptionType {

    private static final MessageComponentSerializer SERIALIZER = MessageComponentSerializer.message();

    private Message translate(Map<String, Object> args) {
        Component tlComp = tl(key, args);
        return SERIALIZER.serialize(tlComp);
    }

    public CommandSyntaxException create(Map<String, Object> args, ImmutableStringReader reader) {
        return new CommandSyntaxException(this, translate(args), reader.getString(), reader.getCursor());
    }

    public CommandSyntaxException create(ImmutableStringReader reader) {
        return create(Map.of(), reader);
    }

    public CommandSyntaxException create(Map<String, Object> args) {
        return new CommandSyntaxException(this, translate(args));
    }

    public CommandSyntaxException create() {
        return create(Map.of());
    }

}
