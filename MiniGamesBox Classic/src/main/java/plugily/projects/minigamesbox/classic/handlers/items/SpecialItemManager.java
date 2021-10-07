/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.handlers.items;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.util.XMaterial;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class SpecialItemManager {

  private final Map<String, SpecialItem> specialItems = new HashMap<>();
  private final FileConfiguration config;
  private final Main plugin;

  public SpecialItemManager(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "special_items");
    loadSpecialItems();
  }


  private void loadSpecialItems() {
    SpecialItem.getSpecialItems().forEach((key, specialItem) -> {
      if(!"Version".equals(specialItem.getPath())) {
        addItem(key, specialItem.getPath());
      }
    });
    for(String key : config.getKeys(false)) {
      if("Version".equals(key)) {
        continue;
      }
      if(specialItems.containsKey(key)) {
        continue;
      }
      addItem(key, key);
    }
  }

  public void addItem(String key, String path) {
    Material mat = XMaterial.matchXMaterial(config.getString(path + ".material-name", "BEDROCK").toUpperCase()).orElse(XMaterial.BEDROCK).parseMaterial();
    String name = plugin.getChatManager().colorRawMessage(config.getString(path + ".displayname"));
    List<String> lore = config.getStringList(path + ".lore").stream()
        .map(itemLore -> itemLore = plugin.getChatManager().colorRawMessage(itemLore))
        .collect(Collectors.toList());

    SpecialItem.DisplayStage stage = SpecialItem.DisplayStage.LOBBY;
    try {
      stage = SpecialItem.DisplayStage.valueOf(config.getString(path + ".stage").toUpperCase());
    } catch(Exception ex) {
      plugin.getDebugger().debug(Level.WARNING, "Invalid display stage of special item " + path + " in special_items.yml! Please use lobby or spectator!");
    }

    Set<Reward> rewards = new HashSet<>();
    for(String reward : config.getStringList(path + ".execute")) {
      rewards.add(new Reward(new RewardType(path), reward));
    }
    specialItems.put(key, new SpecialItem(path, config.getString(path + ".permission", null), new ItemBuilder(mat).name(name).lore(lore).build(), config.getInt(path + ".slot", -1), stage, rewards));

  }

  /**
   * Returns ItemStack if found
   *
   * @param key of SpecialItem to get value from
   * @return ItemStack
   */
  public ItemStack getSpecialItemStack(String key) {
    if(!specialItems.containsKey(key)) {
      return SpecialItem.INVALID_ITEM.getItemStack();
    }
    return specialItems.get(key).getItemStack();
  }

  /**
   * Returns ItemStack if found
   *
   * @param key of SpecialItem to get value from
   * @return SpecialItem
   */
  @NotNull
  public SpecialItem getSpecialItem(String key) {
    if(!specialItems.containsKey(key)) {
      return SpecialItem.INVALID_ITEM;
    }
    return specialItems.get(key);
  }

  /**
   * Register a new specialitem
   *
   * @param key The key of the specialitem
   */
  public void registerSpecialItem(String key, String path) {
    if(specialItems.containsKey(key)) {
      throw new IllegalStateException("SpecialItem with key " + key + " was already registered");
    }
    addItem(key, path);
  }

  /**
   * Register a new specialitem
   *
   * @param key         The key of the specialitem
   * @param specialItem Contains the path and the default value
   */
  public void registerSpecialItem(String key, SpecialItem specialItem) {
    if(specialItems.containsKey(key)) {
      throw new IllegalStateException("SpecialItem with path " + key + " was already registered");
    }
    specialItems.put(key, specialItem);
  }

  /**
   * Remove specialitems that are not protected
   *
   * @param key The key of the specialitem
   */
  public void unregisterSpecialItem(String key) {
    SpecialItem specialItem = specialItems.get(key);
    if(specialItem == null) {
      return;
    }
    if(specialItem.isProtected()) {
      throw new IllegalStateException("Protected options cannot be removed!");
    }
    specialItems.remove(key);
  }

  public Map<String, SpecialItem> getSpecialItems() {
    return Collections.unmodifiableMap(specialItems);
  }

  @NotNull
  public SpecialItem getRelatedSpecialItem(ItemStack itemStack) {
    for(SpecialItem item : specialItems.values()) {
      if(item.getItemStack().isSimilar(itemStack)) {
        return item;
      }
    }
    return SpecialItem.INVALID_ITEM;
  }


}
