/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.DolphinEffects;
import raffel080108.pacificsmp.util.Util;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class GiveDolphin {
    @Command("giveDolphin")
    @CommandPermission("pacificsmp.givedolphin")
    @AutoComplete("@dolphinTypes *")
    public void giveDolphinCommand(BukkitCommandActor sender, @Named("dolphin-type") String dolphinTypeName, @Named("player") @Default("self") Player player) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        DolphinType dolphinType;
        try {
            dolphinType = DolphinType.valueOf(dolphinTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.reply(miniMessage.deserialize("<red>Invalid dolphin-type"));
            return;
        }

        DataHandler dataHandler = DataHandler.getInstance();

        List<DolphinType> activeDolphins = dataHandler.getPlayerActiveDolphins(player);
        String dolphinName = Util.capitalizeFirstOnly(dolphinTypeName) + " Dolphin";
        if (activeDolphins.contains(dolphinType)) {
            sender.reply(miniMessage.deserialize("<red>" + player.getName() + " already has the " + dolphinName));
            return;
        }

        int dolphinCap = dataHandler.getPlayerDolphinCap(player);
        boolean commandBypassCap = dataHandler.doesCommandBypassCap();

        if (activeDolphins.size() >= dolphinCap && !commandBypassCap) {
            sender.reply(miniMessage.deserialize("<red>" + player.getName() + " already has the maximum number of Dolphins (" + dolphinCap + ")"));
            return;
        }

        DolphinEffects.triggerEnable(player, dolphinType);
        dataHandler.addActivePlayerDolphin(player, dolphinType);
        sender.reply(miniMessage.deserialize("<green>Successfully gave " + player.getName() + " the " + dolphinName));
    }
}
