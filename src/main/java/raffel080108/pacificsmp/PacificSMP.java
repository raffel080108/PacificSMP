/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import raffel080108.pacificsmp.command.*;
import raffel080108.pacificsmp.data.DataHandler;
import raffel080108.pacificsmp.data.DolphinType;
import raffel080108.pacificsmp.listener.PlayerDeath;
import raffel080108.pacificsmp.listener.PlayerInteract;
import raffel080108.pacificsmp.listener.PlayerJoin;
import raffel080108.pacificsmp.listener.PlayerRespawn;
import raffel080108.pacificsmp.util.ActionbarRunnable;
import raffel080108.pacificsmp.util.Util;
import revxrsal.commands.autocomplete.AutoCompleter;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public final class PacificSMP extends JavaPlugin {
    private static PacificSMP pluginInstance;
    private File dataFile;
    private YamlConfiguration dataConfig;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Type playerActiveDolphinsMapType = new TypeToken<LinkedHashMap<UUID, List<DolphinType>>>(){}.getType();
    private Type playerDolphinCapsMapType = new TypeToken<Map<UUID, Integer>>(){}.getType();
    private BukkitTask actionbarTask;

    @Override
    public void onEnable() {
        pluginInstance = this;
        Logger log = getLogger();
        DataHandler dataHandler = DataHandler.getInstance();

        loadConfiguration();

        log.info("Loading data...");
        dataFile = new File(getDataFolder(), "data.yml");
        saveDataFile();

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        String playerActiveDolphinsData = dataConfig.getString("playerActiveDolphins");
        if (playerActiveDolphinsData != null) {
            Map<UUID, List<DolphinType>> playerActiveDolphins = dataHandler.getPlayerActiveDolphins();
            playerActiveDolphins.clear();
            playerActiveDolphins.putAll(gson.fromJson(playerActiveDolphinsData, playerActiveDolphinsMapType));
        } else log.warning("Unable to load data: playerActiveDolphins");

        String playerDolphinCapsData = dataConfig.getString("playerDolphinCaps");
        if (playerDolphinCapsData != null) {
            Map<UUID, Integer> playerDolphinCaps = dataHandler.getPlayerDolphinCaps();
            playerDolphinCaps.clear();
            playerDolphinCaps.putAll(gson.fromJson(playerDolphinCapsData, playerDolphinCapsMapType));
        } else log.warning("Unable to load data: playerDolphinCaps");

        log.info("Registering commands...");
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);
        commandHandler.register(new Reload());
        commandHandler.register(new Help());

        AutoCompleter autoCompleter = commandHandler.getAutoCompleter();
        autoCompleter.registerSuggestion("dolphinNames", (arg, sender, command) -> {
            List<String> suggestions = new ArrayList<>();
            for (DolphinType dolphinType : DolphinType.values())
                suggestions.add(dolphinType.name().toLowerCase());

            suggestions.add("slot-reroll");

            return suggestions;
        });
        autoCompleter.registerSuggestion("dolphinTypes", (arg, sender, command) -> {
            List<String> suggestions = new ArrayList<>();
            for (DolphinType dolphinType : DolphinType.values())
                suggestions.add(dolphinType.name().toLowerCase());

            return suggestions;
        });
        commandHandler.register(new GiveDolphin());
        commandHandler.register(new GiveDolphinItem());
        commandHandler.register(new ClearDolphin());
        commandHandler.register(new SetDolphinSlots());
        commandHandler.register(new DolphinWithdraw());

        commandHandler.enableAdventure();
        commandHandler.registerBrigadier();

        log.info("Registering listeners...");
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new PlayerRespawn(), this);
        pluginManager.registerEvents(new PlayerInteract(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);

        actionbarTask = new ActionbarRunnable().runTaskTimer(this, 0, 1);

        log.info("Registering recipes...");
        NamespacedKey darkRecipeKey = new NamespacedKey(this, "dark-dolphin");
        ShapedRecipe darkRecipe = new ShapedRecipe(darkRecipeKey, dataHandler.getDolphinItem(DolphinType.DARK));
        darkRecipe.shape("123", "242", "321");

        darkRecipe.setIngredient('1', Material.PRISMARINE_SHARD);
        darkRecipe.setIngredient('2', Material.ECHO_SHARD);
        darkRecipe.setIngredient('3', Material.PRISMARINE_CRYSTALS);
        darkRecipe.setIngredient('4', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(darkRecipe);

        NamespacedKey speedRecipeKey = new NamespacedKey(this, "speed-dolphin");
        ShapedRecipe speedRecipe = new ShapedRecipe(speedRecipeKey, dataHandler.getDolphinItem(DolphinType.SPEED));
        speedRecipe.shape("123", "454", "321");
        speedRecipe.setIngredient('1', Material.PRISMARINE_SHARD);
        speedRecipe.setIngredient('2', Material.RABBIT_FOOT);
        speedRecipe.setIngredient('3', Material.PRISMARINE_CRYSTALS);
        speedRecipe.setIngredient('4', Material.DIAMOND_BOOTS);
        speedRecipe.setIngredient('5', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(speedRecipe);
        
        NamespacedKey flameRecipeKey = new NamespacedKey(this, "flame-dolphin");
        ShapedRecipe flameRecipe = new ShapedRecipe(flameRecipeKey, dataHandler.getDolphinItem(DolphinType.FLAME));
        flameRecipe.shape("123", "454", "321");
        flameRecipe.setIngredient('1', Material.PRISMARINE_SHARD);
        flameRecipe.setIngredient('2', Material.GOLDEN_APPLE);
        flameRecipe.setIngredient('3', Material.PRISMARINE_CRYSTALS);
        flameRecipe.setIngredient('4', Material.BLAZE_ROD);
        flameRecipe.setIngredient('5', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(flameRecipe);

        NamespacedKey powerRecipeKey = new NamespacedKey(this, "power-dolphin");
        ShapedRecipe powerRecipe = new ShapedRecipe(powerRecipeKey, dataHandler.getDolphinItem(DolphinType.POWER));
        powerRecipe.shape("123", "242", "321");
        powerRecipe.setIngredient('1', Material.PRISMARINE_SHARD);
        powerRecipe.setIngredient('2', Material.DIAMOND_SWORD);
        powerRecipe.setIngredient('3', Material.PRISMARINE_CRYSTALS);
        powerRecipe.setIngredient('4', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(powerRecipe);

        NamespacedKey healthyRecipeKey = new NamespacedKey(this, "healthy-dolphin");
        ShapedRecipe healthyRecipe = new ShapedRecipe(healthyRecipeKey, dataHandler.getDolphinItem(DolphinType.HEALTHY));
        healthyRecipe.shape("123", "242", "321");
        healthyRecipe.setIngredient('1', Material.PRISMARINE_SHARD);
        healthyRecipe.setIngredient('2', Material.GOLDEN_APPLE);
        healthyRecipe.setIngredient('3', Material.PRISMARINE_CRYSTALS);
        healthyRecipe.setIngredient('4', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(healthyRecipe);

        NamespacedKey rerollRecipeKey = new NamespacedKey(this, "dolphin-slot-reroll");
        ShapedRecipe rerollRecipe = new ShapedRecipe(rerollRecipeKey, dataHandler.getRerollItem());
        rerollRecipe.shape("123", "454", "321");
        rerollRecipe.setIngredient('1', Material.NETHERITE_INGOT);
        rerollRecipe.setIngredient('2', Material.TOTEM_OF_UNDYING);
        rerollRecipe.setIngredient('3', Material.GOLDEN_APPLE);
        rerollRecipe.setIngredient('4', Material.DIAMOND);
        rerollRecipe.setIngredient('5', Material.HEART_OF_THE_SEA);

        Bukkit.addRecipe(rerollRecipe);

        log.info("Plugin initialization complete!");
    }

    @Override
    public void onDisable() {
        Logger log = getLogger();

        log.info("Shutting down...");

        actionbarTask.cancel();

        DataHandler dataHandler = DataHandler.getInstance();
        // Save data
        dataConfig.set("playerActiveDolphins", gson.toJson(dataHandler.getPlayerActiveDolphins()));
        dataConfig.set("playerDolphinCaps", gson.toJson(dataHandler.getPlayerDolphinCaps()));

        saveDataFile();

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            log.severe("An error occurred while attempting to save data:");
            e.printStackTrace();
        }

        log.info("Plugin shutdown complete");
    }

    public static PacificSMP getPluginInstance() {
        return pluginInstance;
    }

    LinkedHashMap<Integer, List<String>> configVersionKeys = new LinkedHashMap<>();

    {
        configVersionKeys.put(2, List.of(
                "dolphin-item-data.dolphin-slot-reroll.item-name",
                "dolphin-item-data.dolphin-slot-reroll.item-lore",
                "dolphin-item-data.dolphin-slot-reroll.active-actionbar-text",
                "dolphin-item-data.dolphin-slot-reroll.custom-model-data"
        ));
        configVersionKeys.put(3, List.of(
                "dolphin-item-data.dolphin-slot-reroll.reroll-title",
                "dolphin-item-data.dolphin-slot-reroll.reroll-subtitle",
                "dolphin-item-data.dolphin-slot-reroll.title-fade-in",
                "dolphin-item-data.dolphin-slot-reroll.title-stay",
                "dolphin-item-data.dolphin-slot-reroll.title-fade-out"
        ));
    }

    private void saveDataFile() {
        if (dataFile == null)
            return;

        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
    }

    public void loadConfiguration() {
        Logger log = getLogger();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        DataHandler dataHandler = DataHandler.getInstance();

        log.info("Loading configuration...");

        saveDefaultConfig();
        reloadConfig();

        FileConfiguration config = getConfig();
        Configuration defaultConfig = config.getDefaults();

        config.setDefaults(new YamlConfiguration());

        int configVersion = config.getInt("config-version", 1);
        if (defaultConfig != null) {
            for (Map.Entry<Integer, List<String>> entry : configVersionKeys.entrySet()) {
                int version = entry.getKey();
                if (version <= configVersion)
                    continue;

                for (String path : entry.getValue()) {
                    config.set(path, defaultConfig.get(path));

                    config.setComments(path, defaultConfig.getComments(path));
                    config.setInlineComments(path, defaultConfig.getInlineComments(path));
                }

                config.set("config-version", version);
                configVersion = version;
            }
            saveConfig();
        }

        ConfigurationSection dolphinItemData = config.getConfigurationSection("dolphin-item-data");
        if (dolphinItemData != null) {
            for (DolphinType dolphinType : DolphinType.values()) {
                String dolphinTypeName = dolphinType.name().toLowerCase();

                ItemStack dolphinItem = new ItemStack(Material.BLUE_DYE);
                ItemMeta meta = dolphinItem.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey(this, "dolphin-item-type"), PersistentDataType.STRING, dolphinTypeName);

                ConfigurationSection dolphinTypeConfig = dolphinItemData.getConfigurationSection(dolphinTypeName);
                if (dolphinTypeConfig == null) {
                    log.warning("Missing or invalid configuration for dolphin-type: " + dolphinTypeName);
                    continue;
                }

                dolphinItem.setItemMeta(getMetaFromConfig(meta, dolphinTypeConfig));
                dataHandler.setDolphinItem(dolphinType, dolphinItem);

                String activeActionbarText = dolphinTypeConfig.getString("active-actionbar-text");
                if (activeActionbarText != null) {
                    activeActionbarText = activeActionbarText.replaceFirst("\\\\", "");
                    dataHandler.setDolphinActionbarText(dolphinType, miniMessage.deserialize(activeActionbarText));
                }
            }

            ConfigurationSection rerollItemConfig = dolphinItemData.getConfigurationSection("dolphin-slot-reroll");
            if (rerollItemConfig != null) {
                ItemStack slotRerollItem = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = slotRerollItem.getItemMeta();
                meta.getPersistentDataContainer().set(Util.getRerollItemKey(), PersistentDataType.BOOLEAN, true);

                slotRerollItem.setItemMeta(getMetaFromConfig(meta, rerollItemConfig));

                dataHandler.setRerollItem(slotRerollItem);
                dataHandler.setRerollRangeMin(rerollItemConfig.getInt("reroll-range-min", 1));
                dataHandler.setRerollRangeMax(rerollItemConfig.getInt("reroll-range-max", 5));
                dataHandler.setRerollTitle(rerollItemConfig.getString("reroll-title", ""));
                dataHandler.setRerollSubtitle(rerollItemConfig.getString("reroll-subtitle", ""));
                dataHandler.setRerollTitleTimes(Title.Times.times(
                        Duration.ofMillis(rerollItemConfig.getLong("title-fade-in", 500)),
                        Duration.ofMillis(rerollItemConfig.getLong("title-stay", 2000)),
                        Duration.ofMillis(rerollItemConfig.getLong("title-fade-out", 500))));
            } else log.warning("Missing or invalid configuration for slot-reroll item");
        } else log.warning("Invalid or missing configuration for: dolphin-item-data");

        dataHandler.setDefaultDolphinCap(config.getInt("dolphin-cap", 2));
        dataHandler.setCommandBypassCap(config.getBoolean("command-bypass-cap", false));
        dataHandler.setActionbarSeparator(miniMessage.deserialize(config.getString("actionbar-separator", "")));
    }

    private ItemMeta getMetaFromConfig(ItemMeta meta, ConfigurationSection itemConfig) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        String itemName = itemConfig.getString("item-name");
        if (itemName != null)
            meta.displayName(miniMessage.deserialize(itemName)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .colorIfAbsent(NamedTextColor.WHITE));

        List<String> loreStrings = itemConfig.getStringList("item-lore");
        if (!loreStrings.isEmpty()) {
            List<Component> lore = new ArrayList<>();

            for (String loreString: loreStrings) {
                lore.add(miniMessage.deserialize(loreString)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .colorIfAbsent(NamedTextColor.WHITE));
            }

            meta.lore(lore);
        }

        int customModelData = itemConfig.getInt("custom-model-data", -1);
        if (customModelData != -1)
            meta.setCustomModelData(customModelData);

        return meta;
    }
}
