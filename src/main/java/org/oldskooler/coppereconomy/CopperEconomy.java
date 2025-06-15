package org.oldskooler.coppereconomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.oldskooler.coppereconomy.listeners.VillagerListener;

import java.util.logging.Logger;

/**
 * A Bukkit plugin that allows cured villagers to accept copper ingots
 * instead of emeralds for trades, at double the emerald cost.
 *
 * <p>Flow:
 * <ul>
 *     <li>Zombie villager is cured (weakness potion + golden apple).</li>
 *     <li>Right-click cured villager with copper ingot.</li>
 *     <li>Their trades convert to use copper instead of emeralds, at 2x price.</li>
 * </ul>
 */
public class CopperEconomy extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 26173;

    private Logger logger;
    private Metrics metrics;
    private CopperConfig config;

    @Override
    public void onEnable() {
        metrics = new Metrics(this, BSTATS_PLUGIN_ID);

        config = new CopperConfig(this);
        logger = getLogger();

        getServer().getPluginManager().registerEvents(new VillagerListener(this), this);
    }

    @Override
    public void onDisable() { }

    public CopperConfig getSettings() {
        return config;
    }
}