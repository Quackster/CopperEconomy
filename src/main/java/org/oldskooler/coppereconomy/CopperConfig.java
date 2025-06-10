package org.oldskooler.coppereconomy;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Villager;
import org.oldskooler.coppereconomy.CopperEconomy;

import java.util.logging.Level;

public class CopperConfig {

    private final CopperEconomy plugin;

    public CopperConfig(CopperEconomy plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig(); // Ensure config.yml exists
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("copper-conversion.enabled", true);
    }

    public boolean isWorldAllowed(String worldName) {
        return plugin.getConfig().getStringList("copper-conversion.worlds").contains(worldName);
    }

    /** @noinspection UnstableApiUsage*/
    public boolean isProfessionAllowed(Villager.Profession profession) {
        return plugin.getConfig().getStringList("copper-conversion.allowed-professions")
                .contains(profession.name());
    }

    public Sound getSoundEffect() {
        var sound = plugin.getConfig().getString("copper-conversion.sound-effect", "ENTITY_PLAYER_BURP");

        try {
            if (sound.isBlank()) {
                return null;
            }

            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failure while loading sound: " + sound);
            return null;
        }
    }

    public Material getResourceType() {
        return Material.valueOf(plugin.getConfig().getString("copper-conversion.resource-type", "COPPER_INGOT"));
    }

    public int getCureConversionTimeTicks() {
        return plugin.getConfig().getInt("copper-conversion.cure-conversion-time-ticks", 0);
    }

    public double getInputMultiplier(Villager.Profession profession) {
        String key = "copper-conversion.trade-currency-multipliers." + profession.name() + ".input-multiplier";
        return plugin.getConfig().getDouble(key, 2); // Default to 2
    }

    public double getOutputMultiplier(Villager.Profession profession) {
        String key = "copper-conversion.trade-currency-multipliers." + profession.name() + ".output-multiplier";
        return plugin.getConfig().getDouble(key, 1); // Default to 1
    }
}
