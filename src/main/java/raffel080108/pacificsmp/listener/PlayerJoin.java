/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import raffel080108.pacificsmp.PacificSMP;
import raffel080108.pacificsmp.data.DolphinType;

public class PlayerJoin implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PacificSMP pluginInstance = PacificSMP.getPluginInstance();
        Player player = event.getPlayer();

        for (DolphinType dolphinType : DolphinType.values())
            player.discoverRecipe(new NamespacedKey(pluginInstance, dolphinType.name().toLowerCase() + "-dolphin"));

        player.discoverRecipe(new NamespacedKey(pluginInstance, "dolphin-slot-reroll"));
    }
}
