package org.oldskooler.coppereconomy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.oldskooler.coppereconomy.CopperConfig;
import org.oldskooler.coppereconomy.CopperEconomy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Listener class for managing villager curing and copper trade conversion.
 * Players must interact with a curing zombie villager using a copper ingot to enable conversion.
 */
public class VillagerListener implements Listener {
    private final CopperConfig config;
    private final CopperEconomy plugin;

    private final NamespacedKey COPPER_READY;
    private final NamespacedKey ACCEPTS_COPPER;
    /**
     * Constructs the VillagerListener with key bindings for persistent tags.
     *
     * @param plugin The main plugin instance.
     */
    public VillagerListener(CopperEconomy plugin, CopperConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.COPPER_READY = new NamespacedKey(plugin, "copper_ready");
        this.ACCEPTS_COPPER = new NamespacedKey(plugin, "accepts_copper");
    }

    /**
     * Handles right-click interaction with a zombie villager during its curing process.
     * If the player is holding a copper ingot, marks the zombie for copper trade conversion.
     *
     * @param event The player interaction event.
     */
    @EventHandler
    public void onZombieRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ZombieVillager zombie)) return;
        if (!this.config.isEnabled()) return;
        if (!this.config.isWorldAllowed(zombie.getWorld().getName())) return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item.getType() != this.config.getResourceType()) return;
        if (zombie.getPersistentDataContainer().has(this.COPPER_READY, PersistentDataType.BYTE)) return;
        if (!zombie.isConverting()) return;

        zombie.getPersistentDataContainer().set(this.COPPER_READY, PersistentDataType.BYTE, (byte) 1);

        if (this.config.getSoundEffect() != null) {
            zombie.getWorld().playSound(zombie.getLocation(), this.config.getSoundEffect(), 1f, 1f);
        }

        if (this.config.getCureConversionTimeTicks() > 0)
            zombie.setConversionTime(this.config.getCureConversionTimeTicks());

        event.setCancelled(true);
    }

    /**
     * Handles the entity transformation from ZombieVillager to Villager.
     * If the zombie was marked to accept copper, convert its trades accordingly.
     *
     * @param event The transformation event.
     */
    @EventHandler
    public void onCure(EntityTransformEvent event) {
        if (!(event.getEntity() instanceof ZombieVillager zombie)) return;
        if (!(event.getTransformedEntity() instanceof Villager villager)) return;
        if (!zombie.getPersistentDataContainer().has(this.COPPER_READY, PersistentDataType.BYTE)) return;

        if (!this.config.isEnabled()) return;
        if (!this.config.isWorldAllowed(zombie.getWorld().getName())) return;
        if (!this.config.isProfessionAllowed(villager.getProfession())) return;

        villager.getPersistentDataContainer().set(this.ACCEPTS_COPPER, PersistentDataType.BYTE, (byte) 1);

        // Delay the trade conversion slightly to allow the profession to apply its trades
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            convertTradesToCopper(villager);
        }, 1L); // wait 1 tick
    }

    /**
     * Handles when a villager changes profession.
     * If the villager is marked to use copper, convert any new trades added by the profession.
     *
     * @param event The profession change event.
     */
    @EventHandler
    public void onVillagerCareerChange(VillagerCareerChangeEvent event) {
        if (!this.config.isEnabled()) return;
        if (!this.config.isWorldAllowed(event.getEntity().getWorld().getName())) return;
        if (!this.config.isProfessionAllowed(event.getEntity().getProfession())) return;

        var villager = event.getEntity();
        // this.plugin.getLogger().log(Level.INFO, "Converting villager from VillagerCareerChangeEvent " + villager.getUniqueId().toString());

        if (villager.getPersistentDataContainer().has(this.ACCEPTS_COPPER, PersistentDataType.BYTE)) {
            // Delay the trade conversion slightly to allow the profession to apply its trades
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                convertTradesToCopper(villager);
            }, 1L); // wait 1 tick
        }
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!this.config.isEnabled()) return;
        if (!this.config.isWorldAllowed(event.getEntity().getWorld().getName())) return;
        if (!this.config.isProfessionAllowed(((Villager) event.getEntity()).getProfession())) return;

        // this.plugin.getLogger().log(Level.INFO, "Converting villager from VillagerAcquireTradeEvent " + villager.getUniqueId().toString());

        if (villager.getPersistentDataContainer().has(this.ACCEPTS_COPPER, PersistentDataType.BYTE)) {
            // Delay the trade conversion slightly to allow the profession to apply its trades
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                convertTradesToCopper(villager);
            }, 1L); // wait 1 tick
        }
    }

    /**
     * Converts a villager's trades to use copper ingots instead of emeralds.
     * - Any emeralds in the input are replaced with 2x copper ingots.
     * - Any emeralds in the output (result) are replaced 1:1 with copper ingots.
     *
     * @param villager The villager whose trades will be converted.
     */
    private void convertTradesToCopper(Villager villager) {
        double inputMult = this.config.getInputMultiplier(villager.getProfession());
        double outputMult = this.config.getOutputMultiplier(villager.getProfession());

        if (inputMult > 0 || outputMult > 0)
            this.plugin.getLogger().log(Level.INFO, "Converting villager " + villager.getUniqueId().toString() + " with input multi: " + inputMult + ", output multi: " + outputMult + " at " + villager.getLocation());

        try {
            List<MerchantRecipe> newTrades = new ArrayList<>();
            List<MerchantRecipe> originalRecipes = new ArrayList<>(villager.getRecipes());

            for (MerchantRecipe oldRecipe : originalRecipes) {
                List<ItemStack> originalIngredients = new ArrayList<>(oldRecipe.getIngredients());

                // Clone the ingredient list to avoid linked references
                List<ItemStack> newIngredients = new ArrayList<>();

                for (ItemStack ingredient : originalIngredients) {
                    if (ingredient == null) continue; // Safety check
                    Material type = ingredient.getType();

                    if (type == Material.EMERALD && inputMult != 0) {
                        newIngredients.add(new ItemStack(this.config.getResourceType(), (int)(ingredient.getAmount() * inputMult)));
                    } else {
                        newIngredients.add(ingredient.clone());
                    }
                }

                // Clone result to avoid shared references
                ItemStack result = oldRecipe.getResult();
                ItemStack newResult = result.getType() == Material.EMERALD && outputMult != 0
                        ? new ItemStack(this.config.getResourceType(), (int)(result.getAmount() * outputMult))
                        : result.clone();

                MerchantRecipe newRecipe = new MerchantRecipe(
                        newResult,
                        oldRecipe.getMaxUses()
                );

                newRecipe.setIngredients(newIngredients);
                newRecipe.setExperienceReward(oldRecipe.hasExperienceReward());
                newRecipe.setVillagerExperience(oldRecipe.getVillagerExperience());
                newRecipe.setPriceMultiplier(oldRecipe.getPriceMultiplier());

                newTrades.add(newRecipe);
            }

            // Final update after full construction to avoid mid-loop mutation
            villager.setRecipes(newTrades);
        }
        catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred when converting villager trades: " + ex.toString());
        }
    }

}