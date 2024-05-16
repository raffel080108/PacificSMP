/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.util.DolphinEffects;
import raffel080108.pacificsmp.util.Util;

import java.util.List;
import java.util.Random;

public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getItem();
        if (item == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        DataHandler dataHandler = DataHandler.getInstance();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        NamespacedKey dolphinTypeKey = Util.getDolphinTypeKey();

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        Player player = event.getPlayer();
        if (dataContainer.has(dolphinTypeKey)) {
            DolphinType dolphinType;
            try {
                //noinspection DataFlowIssue
                dolphinType = DolphinType.valueOf(dataContainer.get(dolphinTypeKey, PersistentDataType.STRING).toUpperCase());
            } catch (IllegalArgumentException e) {
                return;
            }

            event.setCancelled(true);

            List<DolphinType> activeDolphins = dataHandler.getPlayerActiveDolphins(player);
            if (activeDolphins.contains(dolphinType)) {
                player.sendMessage(miniMessage.deserialize("<red>You already have this Dolphin"));
                return;
            }

            int dolphinCap = dataHandler.getPlayerDolphinCap(player);
            if (activeDolphins.size() >= dolphinCap) {
                player.sendMessage(miniMessage.deserialize("<red>You already have the maximum number of Dolphins (" + dolphinCap + ")"));
                return;
            }

            item.setAmount(item.getAmount() - 1);

            DolphinEffects.triggerEnable(player, dolphinType);
            dataHandler.addActivePlayerDolphin(player, dolphinType);
            player.sendMessage(miniMessage.deserialize("<green>You now have the " + Util.capitalizeFirstOnly(dolphinType.name()) + " Dolphin"));
        } else if (dataContainer.has(Util.getRerollItemKey())) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);

            int playerDolphinCap = dataHandler.getPlayerDolphinCap(player);
            int newCap = playerDolphinCap;
            while (newCap == playerDolphinCap)
                newCap = new Random(System.nanoTime()).nextInt(dataHandler.getRerollRangeMin(), dataHandler.getRerollRangeMax() + 1);

            dataHandler.setPlayerDolphinCap(player, newCap);

            player.sendMessage(miniMessage.deserialize("<green>Your new dolphin-cap is " + newCap));
            player.showTitle(Title.title(dataHandler.getRerollTitle(newCap), dataHandler.getRerollSubtitle(newCap), dataHandler.getRerollTitleTimes()));
        }
    }
}
