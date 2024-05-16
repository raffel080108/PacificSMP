/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;

public class ActionbarRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            DataHandler dataHandler = DataHandler.getInstance();
            Component actionBarText = Component.text("");

            boolean useSeparator = false;
            for (DolphinType dolphinType : dataHandler.getPlayerActiveDolphins(player)) {
                if (useSeparator)
                    actionBarText = actionBarText.append(dataHandler.getActionbarSeparator());
                else useSeparator = true;

                actionBarText = actionBarText.append(dataHandler.getDolphinActionbarText(dolphinType));
            }

            player.sendActionBar(actionBarText);
        }

    }
}
