/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import raffel080108.pacificsmp.data.DataHandler;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SetDolphinSlots {
    @Command("setdolphinslots")
    @CommandPermission("pacificsmp.setdolphinslots")
    public void setDolphinSlotsCommand(BukkitCommandActor sender, @Named("amount") @Range(min = 1) int newCap, @Named("player") @Default("self") Player player) {
        DataHandler.getInstance().setPlayerDolphinCap(player, newCap);
        sender.reply(MiniMessage.miniMessage().deserialize("<green>Dolphin cap set to " + newCap + " for " + player.getName()));
    }
}
