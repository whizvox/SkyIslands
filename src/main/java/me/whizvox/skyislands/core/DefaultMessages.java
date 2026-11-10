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
        minimsg("create.worldAlreadyExists", "There already exists a sky island world: <yellow><world></yellow>");
        minimsg("create.worldNameConflict", "There already exists a world with that name");
        minimsg("create.noIslandsAllowed", "You are not allowed to create any sky islands.");
        minimsg("create.maxIslandsReached", "Maximum number of islands reached: <yellow><value></yellow>");
        minimsg("setup.success", "A new sky island world has been set up. Run <click:run_command:'/island setup help'><yellow>/island setup help</yellow></click> for more information.");
        minimsg("setup.alreadySettingUp", "A sky island world is already being set up.");
        minimsg("setup.alreadyExists", "A sky island world already exists. You cannot have more than one per server.");
        minimsg("setup.noneFound", "No sky island world is currently being set up.");
        minimsg("setup.set.worldName", "World name has been updated");
        minimsg("setup.set.lobby", "Lobby location has been updated");
        minimsg("setup.set.plotDiameter", "Plot size has been updated");
        minimsg("setup.set.buildAreaDiameter", "Build area size has been updated");
        minimsg("setup.set.maxIslands", "Max island count has been updated");
        minimsg("setup.set.worldBorderDiameter", "World border size has been updated");
        minimsg("setup.set.defaultPermission", "Default visitor permission has been toggled");
        minimsg("setup.info", """
            <gray>=-=</gray> <aqua><b>Information for Sky Island World</b></aqua> <gray>=-=</gray>
            <click:suggest_command:'/island setup set name '><green>[+]</green></click> World Name: <yellow><world_name></yellow>
            <click:suggest_command:'/island setup set lobby '><green>[+]</green></click> Lobby Location: <lobby>
            <click:suggest_command:'/island setup set worldBorder '><green>[+]</green></click> World Border Diameter: <yellow><world_border></yellow> chunks
            <click:suggest_command:'/island setup set plot '><green>[+]</green></click> Plot Diameter: <yellow><plot></yellow> chunks
            <click:suggest_command:'/island setup set buildArea '><green>[+]</green></click> Build Area Diameter: <yellow><build_area></yellow>
            <click:suggest_command:'/island setup toggle '><green>[+]</green></click> Default Permissions: <default_permissions>""");
        minimsg("setup.help", """
            <gray>=-=</gray> <aqua><b>Sky Island World Setup Commands</b></aqua> <gray>=-=</gray>
            <gray>-</gray> Base Command: <yellow>/island setup <gold>...</gold></yellow>
            <gray>-</gray> <yellow>set worldName <gold><string></gold></yellow>: <red>(required)</red> World name
            <gray>-</gray> <yellow>set lobby <gold><location></gold></yellow>: <red>(required)</red> Fallback teleport location
            <gray>-</gray> <yellow>set worldBorder <gold><integer></gold></yellow>: Size of the world border
            <gray>-</gray> <yellow>set plot <gold><integer></gold></yellow>: Size of an island plot
            <gray>-</gray> <yellow>set buildArea <gold><integer></gold></yellow>: Size of the build area in a plot
            <gray>-</gray> <yellow>set maxIslands <gold><integer></gold></yellow>: Max number of islands allowed in a world
            <gray>-</gray> <yellow>toggle <gold><permission></gold></yellow>: Default visitor permissions
            <gray>-</gray> <yellow>info</yellow>: Display all information about the world
            <gray>-</gray> <yellow>create</yellow>: Attempt to create the world""");
        minimsg("general.emptyList", "<italic>(empty)</italic>");
        minimsg("general.notSet", "<red>NOT SET</red>");
        minimsg("general.location", "<yellow><x></yellow>, <yellow><y></yellow>, <yellow><z></yellow> (<aqua><yaw></aqua>, <aqua><pitch></aqua>) in <yellow><world_name></yellow>");
        minimsg("setup.create.worldNameNotSet", "World name is not set. You should run <click:suggest_command:'/island setup set name '><i><green>/island setup set name <gold>\\<worldName></gold></green></i></click>");
        minimsg("setup.create.lobbyNotSet", "Lobby is not set. Run <click:suggest_command:'/island setup set lobby '><yellow>/island setup set lobby <gold>\\<location></gold></yellow></click>.");
        minimsg("setup.create.invalidWorldName", "World name is not valid. You may only use lowercase letters, numbers, and underscores (i.e. overworld, sky_islands, nether2).");
        minimsg("setup.create.success", "Successfully created a new sky island world: <yellow><world></yellow>");
        minimsg("world.delete.success", "Successfully deleted sky island world");
        minimsg("world.delete.errorDeletingFromDisk", "Encountered an error trying to delete the world folder of <yellow><world></yellow>:<newline><italic><error></italic><newline>Check the logs for more details.");
        minimsg("lobby.teleport", "Teleporting to lobby...");
        minimsg("argument.buildLimitTooLarge", "Island build limit size cannot exceed the plot size");
        minimsg("argument.worldAlreadyExists", "That world already exists");
        minimsg("argument.invalidWorldName", "Invalid world name, must only contain lowercase letters, numbers, and/or underscores (_)");
        minimsg("argument.unknownEnumValue", "Unknown value, must be one of the following: <values>");
    }

}
