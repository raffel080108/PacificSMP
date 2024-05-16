/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.data;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import raffel080108.pacificsmp.PacificSMP;
import raffel080108.pacificsmp.util.DolphinEffects;
import raffel080108.pacificsmp.util.Util;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public class DataHandler {
    private static DataHandler dataHandlerInstance;
    private Map<DolphinType, ItemStack> dolphinItems = new HashMap<>();
    private Map<DolphinType, Component> dolphinActionbarText = new HashMap<>();
    private Map<UUID, Integer> playerDolphinCaps = new HashMap<>();
    private LinkedHashMap<UUID, List<DolphinType>> playerActiveDolphins = new LinkedHashMap<>();
    private int defaultDolphinCap = 2;
    private boolean commandBypassCap = false;
    private Component actionbarSeparator = Component.text("");
    private ItemStack rerollItem;
    private int rerollRangeMin = 1;
    private int rerollRangeMax = 5;
    private String rerollTitle = "";
    private String rerollSubtitle = "";
    private Title.Times rerollTitleTimes = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500));

    private DataHandler() {}

    // Generate default values
    {
        ItemStack rerollItemDefault = new ItemStack(Material.ECHO_SHARD);
        ItemMeta rerollItemMeta = rerollItemDefault.getItemMeta();
        rerollItemMeta.displayName(Component.text("Dolphin Slot Reroll")
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE));
        rerollItemMeta.lore(
                List.of(
                        Component.text("Will re-roll your dolphin slots amount").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                        Component.text("to be a random number between 1 to 5").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                ));

        rerollItemMeta.getPersistentDataContainer().set(Util.getRerollItemKey(), PersistentDataType.BOOLEAN, true);
        rerollItemMeta.setCustomModelData(1005);
        rerollItemDefault.setItemMeta(rerollItemMeta);
        rerollItem = rerollItemDefault;

        for (DolphinType dolphinType : DolphinType.values()) {
            String dolphinTypeName = Util.capitalizeFirstOnly(dolphinType.name());
            ItemStack defaultDolphinItem = new ItemStack(Material.BLUE_DYE);
            ItemMeta dolphinItemMeta = defaultDolphinItem.getItemMeta();
            dolphinItemMeta.displayName(Component.text(dolphinTypeName + " Dolphin")
                    .color(NamedTextColor.WHITE));
            dolphinItemMeta.getPersistentDataContainer().set(Util.getDolphinTypeKey(), PersistentDataType.STRING, dolphinTypeName.toLowerCase());
            defaultDolphinItem.setItemMeta(dolphinItemMeta);

            dolphinItems.put(dolphinType, defaultDolphinItem);
        }
    }

    public static DataHandler getInstance() {
        if (dataHandlerInstance == null)
            dataHandlerInstance = new DataHandler();

        return dataHandlerInstance;
    }

    public void setDolphinItem(DolphinType dolphinType, ItemStack item) {
        dolphinItems.put(dolphinType, item);
    }

    public ItemStack getDolphinItem(DolphinType dolphinType) {
        return dolphinItems.get(dolphinType);
    }

    public LinkedHashMap<UUID, List<DolphinType>> getPlayerActiveDolphins() {
        return playerActiveDolphins;
    }

    public void addActivePlayerDolphin(Player player, DolphinType dolphinType) {
        List<DolphinType> activeDolphins = getPlayerActiveDolphins(player);

        if (!activeDolphins.contains(dolphinType))
            activeDolphins.add(dolphinType);
    }

    public void removeActivePlayerDolphin(Player player, DolphinType dolphinType) {
        playerActiveDolphins.get(player.getUniqueId()).remove(dolphinType);
    }

    public List<DolphinType> getPlayerActiveDolphins(Player player) {
        UUID uuid = player.getUniqueId();
        playerActiveDolphins.computeIfAbsent(uuid, k -> new ArrayList<>());

        return playerActiveDolphins.get(uuid);
    }

    public Map<UUID, Integer> getPlayerDolphinCaps() {
        return playerDolphinCaps;
    }

    public void setPlayerDolphinCap(Player player, int cap) {
        Logger log = PacificSMP.getPluginInstance().getLogger();
        playerDolphinCaps.put(player.getUniqueId(), cap);

        List<DolphinType> activeDolphins = getPlayerActiveDolphins(player);
        int activeDolphinsAmount = activeDolphins.size();
        if (activeDolphinsAmount <= cap)
            return;

        Location location = player.getLocation();
        boolean isFirstRemoval = true;
        int i = activeDolphinsAmount;
        while (i > cap) {
            log.info(String.valueOf(i));
            int index = i - 1;

            DolphinType dolphinType = activeDolphins.get(index);
            ItemStack dolphinItem = getDolphinItem(dolphinType);

            activeDolphins.remove(index);
            DolphinEffects.triggerDisable(player, dolphinType);

            if (!player.getInventory().addItem(dolphinItem).isEmpty())
                location.getWorld().dropItemNaturally(location, dolphinItem);

            if (isFirstRemoval)
                player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1, 0.8F));
            isFirstRemoval = false;
            i--;
        }
    }

    public int getPlayerDolphinCap(Player player) {
        return playerDolphinCaps.getOrDefault(player.getUniqueId(), defaultDolphinCap);
    }

    public Component getDolphinActionbarText(DolphinType dolphinType) {
        Component actionbarText = dolphinActionbarText.get(dolphinType);

        if (actionbarText != null)
            return actionbarText;

        return Component.text(dolphinType.name().substring(0, 1).toUpperCase()).color(NamedTextColor.WHITE);
    }

    public void setDolphinActionbarText(DolphinType dolphinType, Component text) {
        dolphinActionbarText.put(dolphinType, text);
    }

    public int getDefaultDolphinCap() {
        return defaultDolphinCap;
    }

    public void setDefaultDolphinCap(int defaultDolphinCap) {
        this.defaultDolphinCap = defaultDolphinCap;
    }

    public boolean doesCommandBypassCap() {
        return commandBypassCap;
    }

    public void setCommandBypassCap(boolean commandBypassCap) {
        this.commandBypassCap = commandBypassCap;
    }

    public Component getActionbarSeparator() {
        return actionbarSeparator;
    }

    public void setActionbarSeparator(Component actionbarSeparator) {
        this.actionbarSeparator = actionbarSeparator;
    }

    public ItemStack getRerollItem() {
        return rerollItem;
    }

    public void setRerollItem(ItemStack rerollItem) {
        this.rerollItem = rerollItem;
    }

    public int getRerollRangeMin() {
        return rerollRangeMin;
    }

    public void setRerollRangeMin(int rerollRangeMin) {
        this.rerollRangeMin = rerollRangeMin;
    }

    public int getRerollRangeMax() {
        return rerollRangeMax;
    }

    public void setRerollRangeMax(int rerollRangeMax) {
        this.rerollRangeMax = rerollRangeMax;
    }

    public Component getRerollTitle(int newCap) {
        return MiniMessage.miniMessage().deserialize(rerollTitle, Placeholder.unparsed("new-slots", String.valueOf(newCap)));
    }

    public void setRerollTitle(String rerollTitle) {
        this.rerollTitle = rerollTitle;
    }

    public Component getRerollSubtitle(int newCap) {
        return MiniMessage.miniMessage().deserialize(rerollSubtitle, Placeholder.unparsed("new-slots", String.valueOf(newCap)));
    }

    public void setRerollSubtitle(String rerollSubtitle) {
        this.rerollSubtitle = rerollSubtitle;
    }

    public Title.Times getRerollTitleTimes() {
        return rerollTitleTimes;
    }

    public void setRerollTitleTimes(Title.Times rerollTitleTimes) {
        this.rerollTitleTimes = rerollTitleTimes;
    }
}
