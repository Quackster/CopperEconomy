# CopperEconomy

**Trade with copper—or any item you choose!**  
CopperEconomy lets players cure zombie villagers using a custom resource item (default: `COPPER_INGOT`) and replaces all emerald trades with that item, based on configurable multipliers per profession.

---

## Features

### Custom Cure Mechanic  
Right-click a curing zombie villager with your configured resource (e.g., `COPPER_INGOT`) to trigger special conversion.

### Trade Replacement  
Once cured, the villager’s trades will:
- Replace **emeralds in the input** with `input-multiplier × resource`
- Replace **emeralds in the output** with `output-multiplier × resource`

### Profession-Specific Multipliers  
Each villager profession can have unique input/output multipliers.

### World-Specific Activation  
Enable or disable the feature in selected worlds.

### Custom Sound Effect  
A configurable sound plays when the player marks a zombie villager for copper conversion.

### Screenshots

![](https://i.imgur.com/C2BUsWD.png)

---

## Sample `config.yml`

```yaml
copper-conversion:
  enabled: true
  resource-type: COPPER_INGOT
  sound-effect: ENTITY_PLAYER_BURP
  cure-conversion-time-ticks: 0
  worlds:
    - world
    - world_nether
    - world_the_end
  allowed-professions:
    - ARMORER
    - BUTCHER
    - CARTOGRAPHER
    - CLERIC
    - FARMER
    - FISHERMAN
    - FLETCHER
    - LEATHERWORKER
    - LIBRARIAN
    - MASON
    - SHEPHERD
    - TOOLSMITH
    - WEAPONSMITH
  trade-currency-multipliers:
    ARMORER:
      input-multiplier: 2
      output-multiplier: 1
    BUTCHER:
      input-multiplier: 2
      output-multiplier: 1
    # ...etc for each profession
```

## Settings

``cure-conversion-time-ticks`` when set to 0 will be default conversion time, setting it to 100 will take 5 seconds (1 second = 20 ticks).

``input-multiplier`` is the multipler for emeralds that the Villager would be asking but when converted to copper (or any of your choosing), eg when converted, a trade that would require 5 emeralds would now require 10 copper ingots if the input multiplier was set to 2.

``output-multiplier`` is the multipler for emeralds that the Villager would be selling for copper (or any of your choosing), eg when converted, a trade that would give back 5 emeralds would now give back 10 copper ingots if the input multiplier was set to 2.

### Different Resource Types

You can use any valid Minecraft Material for ``resource-type`` of your choice, such as:

- IRON_INGOT
- GOLD_NUGGET
- DIAMOND
- NETHERITE_SCRAP

###  How To Use
Start curing a zombie villager with a golden apple & weakness potion.

Right-click the converting zombie with your configured resource item (e.g., COPPER_INGOT).

Upon cure, the villager's trades will now use your configured item instead of emeralds.

###  Why Use CopperEconomy?

Add value to underused resources like copper, gold, or iron.

Customize your economy around lore or server theme.

Create a progression-based or class-based trading system.

Lightweight and event-driven — no lag, no commands needed.

### Compatibility
Works with Spigot, Paper, etc.

Supports Minecraft 1.13+

Does not modify naturally spawned villagers — only cured ones
