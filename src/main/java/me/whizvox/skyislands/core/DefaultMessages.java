package me.whizvox.skyislands.core;

import me.whizvox.skyislands.i18n.Messages;

public class DefaultMessages {

    private final Messages messages;

    public DefaultMessages() {
        this.messages = new Messages();
        addAll();
    }

    public Messages getMessages() {
        return messages;
    }

    private void minimsg(String key, String message) {
        messages.miniMessageTranslations().put("skyislands." + key, message);
    }

    private void msgfmt(String key, String message) {
        messages.messageFormatTranslations().put("skyislands." + key, message);
    }

    private void addAll() {
        minimsg("command.version", "<bold>[<gradient:blue:aqua>SkyIslands</gradient>]</bold> Version: <yellow><version></yellow>");
        minimsg("create.self", "Successfully created your own island! Run <yellow><click:run_command:'/island home'>/island home</click></yellow> to go there.");
        minimsg("create.other", "Successfully created an island for <aqua><player></aqua>.");
        minimsg("createWorld.fail.alreadyIslandWorld", "<red>That world is already an island world.</red>");
        minimsg("createWorld.success", "Successfully created a new sky island world: <yellow><world></yellow>");
        msgfmt("argument.buildLimitTooLarge", "Island build limit size cannot exceed the plot size");
    }

}
