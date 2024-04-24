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
import plugily.projects.minigamesbox.api.kit.HandleItem;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.api.kit.IKitRegistry;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.kits.free.EmptyKit;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitRegistry implements IKitRegistry {

  private static HandleItem handleItem;
  public final List<IKit> kits = new java.util.ArrayList<>();
  public final PluginMain plugin;
  private IKit defaultKit;

  public KitRegistry(PluginMain plugin) {
    this.plugin = plugin;
  }

  @Override
  public HandleItem getHandleItem() {
    return handleItem;
  }

  @Override
  public void setHandleItem(HandleItem handleItem) {
    KitRegistry.handleItem = handleItem;
  }

  @Override
  public void registerKit(IKit kit) {
    if (!plugin.getConfigPreferences().getOption("KITS")) {
      plugin.getDebugger().performance("Kit", "Kits are disabled, thus registerKit method will not be ran.");
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as kits are disabled");
      return;
    }
    if (kits.contains(kit)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " can't be added as its already registered");
      return;
    }

    ConfigurationSection configurationSection = kit.getKitConfigSection();
    if (configurationSection != null && !configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit.getKey() + " is disabled by kit file");
      return;
    }

    plugin.getDebugger().debug("Registered {0} kit", kit.getKey());
    kits.add(kit);
  }

  @Override
  public void registerKits(List<String> optionalConfigurations) {
    if (!plugin.getConfigPreferences().getOption("KITS")) {
      plugin.getDebugger().performance("Kit", "Kits are disabled, thus registerKits method will not be ran.");
      return;
    }
    try {
      if (!new File(plugin.getDataFolder() + File.separator + "kits").exists()) {
        new File(plugin.getDataFolder() + "/kits").mkdir();
      }
      File[] kitsFiles = new File(plugin.getDataFolder() + File.separator + "kits").listFiles();
      if (kitsFiles == null || kitsFiles.length == 0) {
        plugin.getDebugger().debug(Level.SEVERE, "No kits found in kits folder, but kits are enabled in config.yml.");
        plugin.getDebugger().debug(Level.SEVERE, "Please add kits to kits folder and restart the server.");
        plugin.onDisable();
        return;
      }
      for (File file : kitsFiles) {
        plugin.getDebugger().debug(Level.INFO, "Trying to load " + ConfigUtils.removeExtension(file.getName()));
        FileConfiguration kitsConfig = ConfigUtils.getConfig(plugin, "/kits/" + ConfigUtils.removeExtension(file.getName()));
        loadKitConfig(ConfigUtils.removeExtension(file.getName()), kitsConfig, optionalConfigurations);
      }
    } catch (Exception exception) {
      plugin.getDebugger().debug(Level.WARNING, "ERROR IN LOADING KITS");
      exception.printStackTrace();
    }
  }

  /**
   * Loads the configuration for a kit.
   *
   * @param kitsConfig the yml of the kit to load the configuration for
   */
  public void loadKitConfig(String kit_key, FileConfiguration kitsConfig, List<String> optionalConfigurations) {
    plugin.getDebugger().debug(Level.INFO, "Loading Kit " + kit_key + " ...");

    if (!kitsConfig.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit_key + " is disabled by kit file");
      return;
    }

    String kit_name = kitsConfig.getString("name", kit_key);
    List<String> kit_description = kitsConfig.getStringList("description");

    ItemStack itemStack = XMaterial.BEDROCK.parseItem();
    if (kitsConfig.getConfigurationSection("display_item") != null) {
      itemStack = XItemStack.deserialize(kitsConfig.getConfigurationSection("display_item"));
    }

    String kitType = kitsConfig.getString("kit_type");

    if (kitType == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is null.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
      return;
    }

    Kit kit;

    switch (kitType) {
      case "free": {
        kit = new FreeKit(kit_key, kit_name, kit_description, itemStack);
        break;
      }
      case "level": {
        kit = new LevelKit(kit_key, kit_name, kit_description, itemStack);
        ((LevelKit) kit).setLevel(kitsConfig.getInt("required-level"));
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

    if (kitsConfig.getString("unlockedOnDefault") == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have an unlockedOnDefault configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " will not be loaded.");
      return;
    }
    kit.setUnlockedOnDefault(kitsConfig.getBoolean("unlockedOnDefault"));

    HashMap<ItemStack, Integer> kitItems = new HashMap<>();

    ConfigurationSection inventoryConfigurationSection = kitsConfig.getConfigurationSection("inventory");
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
    } else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an inventory configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any inventory items.");
    }


    ConfigurationSection armourConfigurationSection = kitsConfig.getConfigurationSection("armour");
    if (armourConfigurationSection != null) {

      ConfigurationSection helmetConfigurationSection = armourConfigurationSection.getConfigurationSection("helmet");
      if (helmetConfigurationSection != null) {
        kit.setKitHelmet(XItemStack.deserialize(helmetConfigurationSection));
      }

      ConfigurationSection chestplateConfigurationSection = armourConfigurationSection.getConfigurationSection("chestplate");
      if (chestplateConfigurationSection != null) {
        kit.setKitChestplate(XItemStack.deserialize(chestplateConfigurationSection));
      }

      ConfigurationSection leggingsConfigurationSection = armourConfigurationSection.getConfigurationSection("leggings");
      if (leggingsConfigurationSection != null) {
        kit.setKitLeggings(XItemStack.deserialize(leggingsConfigurationSection));
      }

      ConfigurationSection bootsConfigurationSection = armourConfigurationSection.getConfigurationSection("boots");
      if (bootsConfigurationSection != null) {
        kit.setKitBoots(XItemStack.deserialize(bootsConfigurationSection));
      }
    } else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an armour configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any armour items.");
    }

    List<String> kit_actions = kitsConfig.getStringList("abilities");
    kit.setAbilities(kit_actions);

    if (optionalConfigurations != null) {
      optionalConfigurations.forEach((configuration) -> {
        if (kitsConfig.contains(configuration)) {
          kit.addOptionalConfiguration(configuration, kitsConfig.get(configuration));
          plugin.getDebugger().debug("Kit " + kit.getKey() + " has optional configuration " + configuration);
        }
      });
    }

    plugin.getDebugger().debug("Kit " + kit.getKey() + " loaded.");
    kits.add(kit);
  }

  @Override
  public IKit getDefaultKit() {
    if (defaultKit == null) {
      setDefaultKit(new EmptyKit("default", "default"));
    }
    plugin.getDebugger().debug("getDefaultKit is {0}", defaultKit.getName());
    return defaultKit;
  }

  @Override
  public void setDefaultKit(IKit defaultKit) {
    plugin.getDebugger().debug("DefaultKit set to {0}", defaultKit.getName());
    this.defaultKit = defaultKit;
  }

  @Override
  public void setDefaultKit(String defaultKitName) {
    String defaultKitKey = plugin.getConfig().getString("Kit.Default", defaultKitName);
    AtomicReference<IKit> defaultKit = new AtomicReference<>(getKitByKey(defaultKitKey));
    if (defaultKit.get() == null) {
      if (getKits().isEmpty()) {
        plugin.getDebugger().debug(Level.SEVERE, "Default kit set is not found and there are no available kits.");
        plugin.getDebugger().debug(Level.SEVERE, "Please add kits to the 'kits' folder and restart the server");
        plugin.onDisable();
        return;
      }
      getKits().stream().filter(IKit::isUnlockedOnDefault).findFirst().ifPresent(defaultKit::set);
      if (defaultKit.get() == null) {
        plugin.getDebugger().debug(Level.SEVERE, "Default kit set is not found and there are no available free kits.");
        plugin.getDebugger().debug(Level.SEVERE, "Please add free kits to the 'kits' folder and restart the server");
        plugin.onDisable();
        return;
      }
      plugin.getDebugger().debug("Default kit {0} not found, using {1}", defaultKitKey, defaultKit.get().getKey());
    }
    this.plugin.getDebugger().debug("DefaultKit set to {0}", defaultKit.get().getName());
    this.defaultKit = defaultKit.get();
  }


  @Override
  public List<IKit> getKits() {
    return kits;
  }

  @Override
  public IKit getKitByKey(String key) {
    for (IKit kit : kits) {
      if (kit.getKey().equalsIgnoreCase(key)) {
        return kit;
      }
    }
    return null;
  }


  @Override
  public IKit getKitByName(String key) {
    for (IKit kit : kits) {
      if (kit.getName().equalsIgnoreCase(key)) {
        return kit;
      }
    }
    return null;
  }

}
