/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;

import java.util.List;

public class Help {
    @Command("pacificsmp")
    @Subcommand("help")
    public void helpCommand(BukkitCommandActor sender) {
        StringBuilder helpMessage = new StringBuilder("----------\nList of commands:\n/pacificsmp help");

        List<String> commandNames = List.of("pacificsmp reload", "givedolphin", "givedolphinitem", "cleardolphin", "dolphinwithdraw", "setdolphinslots");
        for (String commandName : commandNames) {
            boolean hasPermission = true;
            if (sender.isPlayer())
                //noinspection DataFlowIssue
                hasPermission = sender.getAsPlayer().hasPermission("pacificsmp." + commandName);

            if (hasPermission)
                helpMessage.append("\n/").append(commandName);
        }

        helpMessage.append("\n----------");
        sender.reply(helpMessage.toString());
    }
}
