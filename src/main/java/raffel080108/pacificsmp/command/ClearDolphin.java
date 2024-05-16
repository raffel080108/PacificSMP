/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.DolphinEffects;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class ClearDolphin {
    @Command("clearDolphin")
    @CommandPermission("pacificsmp.cleardolphin")
    public void clearDolphinCommand(BukkitCommandActor sender, @Named("player") @Default("self") Player player) {
        DataHandler dataHandler = DataHandler.getInstance();
        for (DolphinType dolphinType : dataHandler.getPlayerActiveDolphins(player))
            DolphinEffects.triggerDisable(player, dolphinType);

        List<DolphinType> activeDolphins = dataHandler.getPlayerActiveDolphins(player);
        String playerName = player.getName();

        if (activeDolphins.isEmpty()) {
            sender.reply(MiniMessage.miniMessage().deserialize("<red>" + playerName + " has no active dolphins"));
            return;
        }

        activeDolphins.clear();

        sender.reply(MiniMessage.miniMessage().deserialize("<green>Dolphins have been cleared from " + playerName));
    }
}
