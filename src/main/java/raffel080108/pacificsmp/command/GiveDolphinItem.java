/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.Util;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class GiveDolphinItem {
    @Command("giveDolphinItem")
    @CommandPermission("pacificsmp.givedolphinitem")
    @AutoComplete("@dolphinNames *")
    public void giveDolphinCommand(BukkitCommandActor sender, @Named("dolphin-type") String dolphinTypeName, @Named("player") @Default("self") Player player) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        DataHandler dataHandler = DataHandler.getInstance();

        String playerName = player.getName();
        if (dolphinTypeName.equals("slot-reroll")) {
            if (player.getInventory().addItem(dataHandler.getRerollItem()).isEmpty())
                sender.reply(miniMessage.deserialize("<green>Successfully gave Slot Reroll item to " + playerName));
            else sender.reply(miniMessage.deserialize("<red>Unable to give Slot Reroll item to " + playerName));

            return;
        }

        DolphinType dolphinType;
        try {
            dolphinType = DolphinType.valueOf(dolphinTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.reply(miniMessage.deserialize("<red>Invalid dolphin-type"));
            return;
        }

        if (player.getInventory().addItem(dataHandler.getDolphinItem(dolphinType)).isEmpty())
            sender.reply(miniMessage.deserialize("<green>Successfully gave " + Util.capitalizeFirstOnly(dolphinTypeName) + " Dolphin to " + playerName));
        else sender.reply(miniMessage.deserialize("<red>Unable to give Dolphin to " + playerName));
    }
}
