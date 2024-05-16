/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.util;

import org.bukkit.NamespacedKey;
import raffel080108.pacificsmp.PacificSMP;

import java.util.regex.Pattern;

public class Util {
    public static String capitalizeFirstOnly(String string) {
        return Pattern.compile("^.").matcher(string.toLowerCase()).replaceFirst(m -> m.group().toUpperCase());
    }

    public static NamespacedKey getDolphinTypeKey() {
        return new NamespacedKey(PacificSMP.getPluginInstance(), "dolphin-item-type");
    }

    public static NamespacedKey getRerollItemKey() {
        return new NamespacedKey(PacificSMP.getPluginInstance(), "is-slot-reroll-item");
    }
}
