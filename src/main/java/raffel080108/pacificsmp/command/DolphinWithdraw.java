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
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class DolphinWithdraw {
    @Command("dolphinWithdraw")
    @CommandPermission("pacificsmp.dolphinwithdraw")
    @AutoComplete("@dolphinTypes")
    public void DolphinWithDrawCommand(BukkitCommandActor sender, @Named("dolphin-type") String dolphinTypeName) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        DataHandler dataHandler = DataHandler.getInstance();

        if (!sender.isPlayer()) {
            sender.reply(miniMessage.deserialize("<red>You must be a player to use this command!"));
            return;
        }

        DolphinType dolphinType;
        try {
            dolphinType = DolphinType.valueOf(dolphinTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.reply(miniMessage.deserialize("<red>Invalid dolphin-type"));
            return;
        }

        Player player = sender.getAsPlayer();
        //noinspection DataFlowIssue
        List<DolphinType> activeDolphins = dataHandler.getPlayerActiveDolphins(player);
        if (!activeDolphins.contains(dolphinType)) {
            sender.reply(miniMessage.deserialize("<red>You do not have this Dolphin active"));
            return;
        }

        if (player.getInventory().addItem(dataHandler.getDolphinItem(dolphinType)).isEmpty()) {
            activeDolphins.remove(dolphinType);
            DolphinEffects.triggerDisable(player, dolphinType);

            sender.reply(miniMessage.deserialize("<green>Successfully withdrew " + Util.capitalizeFirstOnly(dolphinTypeName) + " Dolphin"));
        } else sender.reply(miniMessage.deserialize("<red>Unable to withdraw " + Util.capitalizeFirstOnly(dolphinTypeName) + " Dolphin (This is likely because your Inventory is full)"));
    }
}
