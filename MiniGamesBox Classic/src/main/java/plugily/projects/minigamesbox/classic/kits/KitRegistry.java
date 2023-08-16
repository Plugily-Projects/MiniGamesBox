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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.kits.free.EmptyKit;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitRegistry {

  private final List<Kit> kits = new java.util.ArrayList<>();
  private Kit defaultKit;
  private final PluginMain plugin;

  public KitRegistry(PluginMain plugin) {
    this.plugin = plugin;
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public void registerKit(Kit kit) {
    ConfigurationSection configurationSection = kit.getKitsConfig().createSection(kit.getKey());
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as kits are disabled");
      return;
    }
    if(kits.contains(kit)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as its already registered");
      return;
    }

    if(!configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " is disabled by kits.yml");
      return;
    }

    initializeKitConfig(kit);
    kit.saveKitsConfig();
    plugin.getDebugger().debug("Kit " + kit.getKey() + "'s config files have been initialized");

    loadKitConfig(kit);
    plugin.getDebugger().debug("Kit " + kit.getKey() + "'s config files have been loaded");

    plugin.getDebugger().debug("Registered {0} kit", kit.getKey());
    kits.add(kit);
  }

  public void initializeKitConfig(Kit kit) {
    ConfigurationSection configurationSection = kit.getKitsConfig().getConfigurationSection(kit.getKey());

    if (configurationSection == null) {
      configurationSection = kit.getKitsConfig().createSection(kit.getKey());
    }

    if (!configurationSection.contains("enabled")) {
      configurationSection.set("enabled", true);
    }

    AtomicInteger currentItem = new AtomicInteger();
    ConfigurationSection inventoryConfigurationSection = configurationSection.getConfigurationSection("Inventory");
    if (inventoryConfigurationSection != null) {
      kit.getKitItems().forEach((item, slot) -> {
        ConfigurationSection itemConfigurationSection = inventoryConfigurationSection.createSection(String.valueOf(currentItem.get()));

        ConfigurationSection itemStackConfigurationSection = itemConfigurationSection.createSection("item");

        XItemStack.serialize(item, itemStackConfigurationSection);
        itemConfigurationSection.set("slot", slot);
        currentItem.getAndIncrement();
      });
    }
  }

  public void loadKitConfig(Kit kit) {
    ConfigurationSection configurationSection = kit.getKitsConfig().getConfigurationSection(kit.getKey());
    assert configurationSection != null;

    kit.getKitItems().clear();
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
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public Kit getDefaultKit() {
    if(defaultKit == null) {
      setDefaultKit(new EmptyKit());
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
