/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.DolphinEffects;

import java.util.List;
import java.util.Random;

public class PlayerDeath implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (player.getKiller() == null)
            return;

        DataHandler dataHandler = DataHandler.getInstance();
        List<DolphinType> activePlayerDolphins = dataHandler.getPlayerActiveDolphins(player);
        if (activePlayerDolphins.isEmpty())
            return;

        DolphinType dolphinToRemove = activePlayerDolphins.get(new Random(System.nanoTime()).nextInt(activePlayerDolphins.size()));
        Location deathLocation = player.getLocation();

        DolphinEffects.triggerDisable(player, dolphinToRemove);
        dataHandler.removeActivePlayerDolphin(player, dolphinToRemove);
        deathLocation.getWorld().dropItemNaturally(deathLocation, dataHandler.getDolphinItem(dolphinToRemove));
    }
}
