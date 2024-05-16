/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import raffel080108.pacificsmp.PacificSMP;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Reload {
    @Command("pacificsmp")
    @Subcommand("reload")
    @CommandPermission("pacificsmp.reload")
    public void reloadCommand(BukkitCommandActor sender) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        sender.reply(miniMessage.deserialize("<green>Reloading configuration..."));

        PacificSMP pluginInstance = PacificSMP.getPluginInstance();
        pluginInstance.loadConfiguration();

        pluginInstance.getLogger().info("Configuration reloaded");
        sender.reply(miniMessage.deserialize("<green>Done!"));
    }
}
