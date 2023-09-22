/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.classic.kits;

import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.kits.free.EmptyKit;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitRegistry {

  final List<Kit> kits = new java.util.ArrayList<>();
  private Kit defaultKit;
  private final PluginMain plugin;
  private final FileConfiguration kitsConfig;

  public KitRegistry(PluginMain plugin) {
    this.plugin = plugin;
    kitsConfig = ConfigUtils.getConfig(plugin, "kits");
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public void registerKit(Kit kit) {
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as kits are disabled");
      return;
    }
    if(kits.contains(kit)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as its already registered");
      return;
    }

    ConfigurationSection configurationSection = kit.getKitConfigSection();
    if(configurationSection != null && !configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " is disabled by kits.yml");
      return;
    }

    // kit.setupKitItems();
    // initializeKitConfig(kit);
    // kit.saveKitsConfig();
    // plugin.getDebugger().debug("Kit " + kit.getKey() + "'s config files have been initialized");

    loadKitConfig(kit);
    plugin.getDebugger().debug("Kit " + kit.getKey() + "'s config files have been loaded");

    plugin.getDebugger().debug("Registered {0} kit", kit.getKey());
    kits.add(kit);
  }

  public void registerKits() {
    if (kitsConfig.getConfigurationSection("kit") == null) {
      plugin.getDebugger().debug(Level.SEVERE, "No kits configuration section was found, skipping loading kits");
      return;
    }

    for (String key : kitsConfig.getConfigurationSection("kit").getKeys(false)) {
      System.out.println("Key: " + key);
      loadKitConfig(key);
    }
  }

  public void initializeKitConfig(Kit kit) {
    ConfigurationSection configurationSection = kit.getKitConfigSection();

    if (!configurationSection.contains("enabled")) {
      configurationSection.set("enabled", true);
    }

    if (kit instanceof LevelKit) {
      if (!configurationSection.contains("required-level")) {
        configurationSection.set("required-level", ((LevelKit) kit).getLevel());
      }
    }

    AtomicInteger currentItem = new AtomicInteger();
    ConfigurationSection inventoryConfigurationSection = configurationSection.getConfigurationSection("Inventory");
    if (inventoryConfigurationSection == null) {
      inventoryConfigurationSection = configurationSection.createSection("Inventory");
      final ConfigurationSection finalInventoryConfigurationSection = inventoryConfigurationSection;

      kit.getKitItems().forEach((item, slot) -> {
        ConfigurationSection itemConfigurationSection = finalInventoryConfigurationSection.createSection(String.valueOf(currentItem.get()));

        ConfigurationSection itemStackConfigurationSection = itemConfigurationSection.createSection("item");

        XItemStack.serialize(item, itemStackConfigurationSection);
        itemConfigurationSection.set("slot", slot);
        currentItem.getAndIncrement();
      });
    }


    ConfigurationSection armourConfigurationSection = configurationSection.getConfigurationSection("Armour");
    if (armourConfigurationSection == null) {
      armourConfigurationSection = configurationSection.createSection("Armour");

      if (kit.getKitHelmet() != null) {
        ConfigurationSection helmetConfigurationSection = armourConfigurationSection.createSection("Helmet");
        XItemStack.serialize(kit.getKitHelmet(), helmetConfigurationSection);
      }

      if (kit.getKitChestplate() != null) {
        ConfigurationSection chestplateConfigurationSection = armourConfigurationSection.createSection("Chestplate");
        XItemStack.serialize(kit.getKitChestplate(), chestplateConfigurationSection);
      }

      if (kit.getKitLeggings() != null) {
        ConfigurationSection leggingsConfigurationSection = armourConfigurationSection.createSection("Leggings");
        XItemStack.serialize(kit.getKitLeggings(), leggingsConfigurationSection);
      }

      if (kit.getKitBoots() != null) {
        ConfigurationSection bootsConfigurationSection = armourConfigurationSection.createSection("Boots");
        XItemStack.serialize(kit.getKitBoots(), bootsConfigurationSection);
      }
    }
  }

  public void loadKitConfig(Kit kit) {
    return;
  }

  private void loadKitConfig(String kit_key) {
    ConfigurationSection configurationSection = kitsConfig.getConfigurationSection("kit." + kit_key);

    if (configurationSection == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have any configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
      return;
    }

    String kit_name = configurationSection.getString("name", kit_key);


    if (configurationSection.getConfigurationSection("display_item") == null) {
      configurationSection.set("display_item", XItemStack.serialize(new ItemStack(Material.BEDROCK)));
    }

    ItemStack itemStack = XItemStack.deserialize(Objects.requireNonNull(configurationSection.getConfigurationSection("display_item")));

    if(!configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit_key + " is disabled by kits.yml");
      return;
    }

    String kitType = configurationSection.getString("kit_type");

    if (kitType == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is null.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
      return;
    }

    Kit kit;

    switch (kitType) {
      case "free": {
        kit = new FreeKit(kit_key, kit_name, itemStack);
        break;
      }
      case "level": {
        kit = new LevelKit(kit_key, kit_name, itemStack);
        ((LevelKit) kit).setLevel(configurationSection.getInt("required-level"));
        break;
      }
      case "premium": {
        kit = new PremiumKit(kit_key, kit_name, itemStack);
        break;
      }
      default: {
        plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is not recognised.");
        plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
        return;
      }
    }

    HashMap<ItemStack, Integer> kitItems = new HashMap<>();

    ConfigurationSection inventoryConfigurationSection = configurationSection.getConfigurationSection("Inventory");
    if (inventoryConfigurationSection != null) {
      inventoryConfigurationSection.getKeys(false).forEach((k) -> {

        ConfigurationSection itemConfigurationSection = inventoryConfigurationSection.getConfigurationSection(k);
        assert itemConfigurationSection != null;

        ConfigurationSection itemStackConfigurationSection = itemConfigurationSection.getConfigurationSection("item");
        assert itemStackConfigurationSection != null;
        ItemStack item = XItemStack.deserialize(itemStackConfigurationSection);
        Integer slot = itemConfigurationSection.getInt("slot");

        kitItems.put(item, slot);
      });
      kit.setKitItems(kitItems);
    }
    else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an inventory configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any inventory items.");
    }


    ConfigurationSection armourConfigurationSection = configurationSection.getConfigurationSection("Armour");
    if (armourConfigurationSection != null) {

      ConfigurationSection helmetConfigurationSection = armourConfigurationSection.getConfigurationSection("Helmet");
      if (helmetConfigurationSection != null) {
        kit.setKitHelmet(XItemStack.deserialize(helmetConfigurationSection));
      }

      ConfigurationSection chestplateConfigurationSection = armourConfigurationSection.getConfigurationSection("Chestplate");
      if (chestplateConfigurationSection != null) {
        kit.setKitChestplate(XItemStack.deserialize(chestplateConfigurationSection));
      }

      ConfigurationSection leggingsConfigurationSection = armourConfigurationSection.getConfigurationSection("Leggings");
      if (leggingsConfigurationSection != null) {
        kit.setKitLeggings(XItemStack.deserialize(leggingsConfigurationSection));
      }

      ConfigurationSection bootsConfigurationSection = armourConfigurationSection.getConfigurationSection("Boots");
      if (bootsConfigurationSection != null) {
        kit.setKitBoots(XItemStack.deserialize(bootsConfigurationSection));
      }
    }
    else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an armour configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any armour items.");
    }
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public Kit getDefaultKit() {
    if(defaultKit == null) {
      setDefaultKit(new EmptyKit("default", "default"));
    }
    plugin.getDebugger().debug("getDefaultKit is {0}", defaultKit.getName());
    return defaultKit;
  }

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  public void setDefaultKit(Kit defaultKit) {
    plugin.getDebugger().debug("DefaultKit set to {0}", defaultKit.getName());
    this.defaultKit = defaultKit;
  }

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  public List<Kit> getKits() {
    return kits;
  }

}
