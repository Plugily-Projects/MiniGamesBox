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

package plugily.projects.minigamesbox.classic.kits.basekits;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.api.kit.ability.IKitAbility;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.kits.ability.KitAbility;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class Kit implements IKit {

  private static final PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

  private FileConfiguration kitsConfig;

  private String name;

  private final String key;

  private final ItemStack itemStack;

  private String kitPermission = "";

  private boolean unlockedOnDefault = false;
  private final List<String> description;

  private final HashMap<String, Object> optionalConfiguration = new HashMap<>();

  private HashMap<ItemStack, Integer> kitItems = new HashMap<>();
  private ItemStack kitHelmet;
  private ItemStack kitChestplate;
  private ItemStack kitLeggings;
  private ItemStack kitBoots;
  private final List<KitAbility> kitAbilities = new ArrayList<>();

  public Kit(String key, String name, List<String> description, ItemStack itemStack) {
    this.key = key;
    this.name = name;
    this.kitsConfig = ConfigUtils.getConfig(plugin, "/kits/" + key);
    this.description = description;
    this.itemStack = itemStack;
  }

  @Override
  public boolean isUnlockedByPlayer(Player p) {
    return false;
  }

  @Override
  public boolean isUnlockedOnDefault() {
    return unlockedOnDefault;
  }

  public void setUnlockedOnDefault(boolean unlockedOnDefault) {
    this.unlockedOnDefault = unlockedOnDefault;
  }

  public void addKitItem(ItemStack item, Integer slot) {
    kitItems.put(item, slot);
  }

  @Override
  public HashMap<ItemStack, Integer> getKitItems() {
    return kitItems;
  }

  public void setKitItems(HashMap<ItemStack, Integer> kitItems) {
    this.kitItems = kitItems;
  }


  @Override
  public IPluginMain getPlugin() {
    return plugin;
  }

  /**
   * @return config file of kits
   */
  public FileConfiguration getKitsConfig() {
    return kitsConfig;
  }

  public void saveKitsConfig() {
    ConfigUtils.saveConfig(plugin, kitsConfig, "/kits/" + key);
    kitsConfig = ConfigUtils.getConfig(plugin, "/kits/" + key);
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the object.
   *
   * @param name the new name to set
   */
  public void setName(String name) {
    if(name != null) {
      this.name = name;
    }
  }

  @Override
  public String getKey() {
    if(key.isEmpty()) {
      return name;
    }
    return key;
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack itemStack1 = itemStack;
    itemStack1.setAmount(1);
    return itemStack1;
  }

  @Override
  public ArrayList<String> getDescription() {
    return new ArrayList<>(description);
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    kitItems.forEach((item, slot) -> player.getInventory().setItem(slot, handleItem(player, item)));
    if(kitHelmet != null) player.getInventory().setHelmet(handleItem(player, kitHelmet));
    if(kitChestplate != null) player.getInventory().setChestplate(handleItem(player, kitChestplate));
    if(kitLeggings != null) player.getInventory().setLeggings(handleItem(player, kitLeggings));
    if(kitBoots != null) player.getInventory().setBoots(handleItem(player, kitBoots));
  }

  /**
   * This method allows you to change kit items given to a player from their original form.
   * If nothing is to be changed for any items, then return itemstack straight away.
   *
   * @param player    Player to give the item to
   * @param itemStack The item stack to be handled
   * @return The item stack after being handled
   */
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    return plugin.getKitRegistry().getHandleItem().apply(player, itemStack);
  }

  /**
   * @return Returns the configuration path for the kit
   */
  public String getKitConfigPath() {
    return key;
  }


  @Override
  public ConfigurationSection getKitConfigSection() {
    ConfigurationSection configurationSection = kitsConfig.getConfigurationSection(getKitConfigPath());
    if(configurationSection == null) {
      kitsConfig.createSection(getKitConfigPath());
    }
    return configurationSection;
  }

  public ItemStack getKitHelmet() {
    return kitHelmet;
  }

  public void setKitHelmet(ItemStack kitHelmet) {
    this.kitHelmet = kitHelmet;
  }

  public ItemStack getKitChestplate() {
    return kitChestplate;
  }

  public void setKitChestplate(ItemStack kitChestplate) {
    this.kitChestplate = kitChestplate;
  }

  public ItemStack getKitLeggings() {
    return kitLeggings;
  }

  public void setKitLeggings(ItemStack kitLeggings) {
    this.kitLeggings = kitLeggings;
  }

  public ItemStack getKitBoots() {
    return kitBoots;
  }

  public void setKitBoots(ItemStack kitBoots) {
    this.kitBoots = kitBoots;
  }

  public void setKitPermission(String kitPermission) {
    this.kitPermission = kitPermission;
  }

  public String getKitPermission() {
    return kitPermission;
  }

  @Override
  public Object getOptionalConfiguration(String path, Object defaultValue) {
    return optionalConfiguration.getOrDefault(path, defaultValue);
  }

  @Override
  public Object getOptionalConfiguration(String path) {
    return optionalConfiguration.get(path);
  }

  @Override
  public void addOptionalConfiguration(String path, Object object) {
    optionalConfiguration.put(path, object);
  }

  public List<KitAbility> getAbilities() {
    return kitAbilities;
  }

  public void setAbilities(List<String> list) {
    for(String abilityName : list) {
      try {
        kitAbilities.add(plugin.getKitAbilityManager().getKitAbility(abilityName));
      } catch(IllegalArgumentException exception) {
        plugin.getDebugger().debug(Level.SEVERE, "The kit-ability " + abilityName + " isn't known. Check your kit folder!");
      }
    }
  }

  @Override
  public boolean hasAbility(IKitAbility kitAbility) {
    return kitAbilities.contains(kitAbility);
  }

  public void addKitAbility(KitAbility kitAbility) {
    kitAbilities.add(kitAbility);
  }

  public void removeKitAbility(KitAbility kitAbility) {
    kitAbilities.remove(kitAbility);
  }

  /*
  Use the method with the kit optional configuration path, e.g. restock.material
   */
  public void reStock(Player player) {
  }
}