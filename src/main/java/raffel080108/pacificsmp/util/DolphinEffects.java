/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import raffel080108.pacificsmp.data.DolphinType;

public class DolphinEffects {
    public static void triggerEnable(Player player, DolphinType dolphinType) {
        switch (dolphinType) {
            case DARK -> player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));
            case SPEED -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, 0, false, false));
            }
            case FLAME -> player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));
            case POWER -> player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, PotionEffect.INFINITE_DURATION, 0, false, false));
            case HEALTHY -> {
                AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attributeInstance != null)
                    attributeInstance.setBaseValue(attributeInstance.getBaseValue() + 6);
            }
        }
    }

    public static void triggerDisable(Player player, DolphinType dolphinType) {
        switch (dolphinType) {
            case DARK -> player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            case SPEED -> {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
            }
            case FLAME -> player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            case POWER -> player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            case HEALTHY -> {
                AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attributeInstance != null)
                    attributeInstance.setBaseValue(attributeInstance.getBaseValue() - 6);
            }
        }
    }
}
