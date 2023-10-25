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
import com.cryptomorin.xseries.XMaterial;
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

import java.util.*;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitRegistry {

  public final List<Kit> kits = new java.util.ArrayList<>();
  private Kit defaultKit;
  public final PluginMain plugin;
  public FileConfiguration kitsConfig;
  private static HandleItem handleItem;

  public KitRegistry(PluginMain plugin) {
    this.plugin = plugin;
    kitsConfig = ConfigUtils.getConfig(plugin, "kits");
  }

  /**
   * Method for registering clone and empty kit
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

    plugin.getDebugger().debug("Registered {0} kit", kit.getKey());
    kits.add(kit);
  }

  /**
   * Registers the kits by loading their configurations.
   */
  public void registerKits(List<String> optionalConfigurations) {

    for(String key : kitsConfig.getKeys(false)) {
      if(!Objects.equals(key, "Do-Not-Edit")) {
        loadKitConfig(key, optionalConfigurations);
      }
    }
  }

  /**
   * Loads the configuration for a kit.
   *
   * @param kit_key the key of the kit to load the configuration for
   */
  public void loadKitConfig(String kit_key, List<String> optionalConfigurations) {
    plugin.getDebugger().debug(Level.SEVERE, "Loading Kit " + kit_key + " ...");
    ConfigurationSection configurationSection = kitsConfig.getConfigurationSection(kit_key);

    if(configurationSection == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have any configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
      return;
    }
    if(!configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit_key + " is disabled by kits.yml");
      return;
    }

    String kit_name = configurationSection.getString("name", kit_key);
    List<String> kit_description = configurationSection.getStringList("description");

    ItemStack itemStack = XMaterial.BEDROCK.parseItem();
    if(configurationSection.getConfigurationSection("display_item") != null) {
      itemStack = XItemStack.deserialize(configurationSection.getConfigurationSection("display_item"));
    }

    String kitType = configurationSection.getString("kit_type");

    if(kitType == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is null.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
      return;
    }

    Kit kit;

    switch(kitType) {
      case "free": {
        kit = new FreeKit(kit_key, kit_name, kit_description, itemStack);
        break;
      }
      case "level": {
        kit = new LevelKit(kit_key, kit_name, kit_description, itemStack);
        ((LevelKit) kit).setLevel(configurationSection.getInt("required-level"));
        break;
      }
      case "premium": {
        kit = new PremiumKit(kit_key, kit_name, kit_description, itemStack);
        break;
      }
      default: {
        plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is not recognised.");
        plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
        return;
      }
    }

    if(configurationSection.getString("unlockedOnDefault") == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have an unlockedOnDefault configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
      return;
    }
    kit.setUnlockedOnDefault(configurationSection.getBoolean("unlockedOnDefault"));

    HashMap<ItemStack, Integer> kitItems = new HashMap<>();

    ConfigurationSection inventoryConfigurationSection = configurationSection.getConfigurationSection("inventory");
    if(inventoryConfigurationSection != null) {
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
    } else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an inventory configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any inventory items.");
    }


    ConfigurationSection armourConfigurationSection = configurationSection.getConfigurationSection("armour");
    if(armourConfigurationSection != null) {

      ConfigurationSection helmetConfigurationSection = armourConfigurationSection.getConfigurationSection("helmet");
      if(helmetConfigurationSection != null) {
        kit.setKitHelmet(XItemStack.deserialize(helmetConfigurationSection));
      }

      ConfigurationSection chestplateConfigurationSection = armourConfigurationSection.getConfigurationSection("chestplate");
      if(chestplateConfigurationSection != null) {
        kit.setKitChestplate(XItemStack.deserialize(chestplateConfigurationSection));
      }

      ConfigurationSection leggingsConfigurationSection = armourConfigurationSection.getConfigurationSection("leggings");
      if(leggingsConfigurationSection != null) {
        kit.setKitLeggings(XItemStack.deserialize(leggingsConfigurationSection));
      }

      ConfigurationSection bootsConfigurationSection = armourConfigurationSection.getConfigurationSection("boots");
      if(bootsConfigurationSection != null) {
        kit.setKitBoots(XItemStack.deserialize(bootsConfigurationSection));
      }
    } else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an armour configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any armour items.");
    }

    List<String> kit_actions = configurationSection.getStringList("abilities");
    kit.setAbilities(kit_actions);

    if(configurationSection.getBoolean("default_kit", false)) {
      this.setDefaultKit(kit);
      plugin.getDebugger().debug("Default kit set to " + kit.getKey());
    }

    if(optionalConfigurations != null) {
      optionalConfigurations.forEach((k) -> {
        if(configurationSection.contains(k)) {
          kit.addOptionalConfiguration(k, configurationSection.get(k));
          plugin.getDebugger().debug("Kit " + kit.getKey() + " has optional configuration " + k);
        }
      });
    }

    plugin.getDebugger().debug("Kit " + kit.getKey() + " loaded.");
    kits.add(kit);
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

  public static HandleItem getHandleItem() {
    return handleItem;
  }

  public void setHandleItem(HandleItem handleItem) {
    KitRegistry.handleItem = handleItem;
  }

}
