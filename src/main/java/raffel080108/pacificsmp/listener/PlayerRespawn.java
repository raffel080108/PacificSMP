/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import raffel080108.pacificsmp.PacificSMP;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.DolphinEffects;

public class PlayerRespawn implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        for (DolphinType dolphinType : DataHandler.getInstance().getPlayerActiveDolphins(player)) {
            if (dolphinType.doesRetriggerOnRespawn()) {
                Bukkit.getScheduler().runTask(PacificSMP.getPluginInstance(), () -> DolphinEffects.triggerEnable(player, dolphinType));
            }
        }
    }
}
