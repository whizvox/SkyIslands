package me.whizvox.skyislands.core;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.whizvox.skyislands.command.util.TranslatableCommandExceptionType;

public class CommandExceptions {

    public static final TranslatableCommandExceptionType NOT_PLAYER = new TranslatableCommandExceptionType(
        "skyislands.exception.notPlayer");

    private CommandExceptions() {
    }

    public static CommandSyntaxException notPlayer() {
        return NOT_PLAYER.create();
    }

}
