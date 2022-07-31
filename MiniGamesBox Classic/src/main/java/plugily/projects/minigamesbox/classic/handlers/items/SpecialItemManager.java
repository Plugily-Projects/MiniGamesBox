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

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

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
  private final PluginMain plugin;

  public final SpecialItem INVALID_ITEM = new SpecialItem("INVALID", new ItemStack(Material.BEDROCK), -1, SpecialItem.DisplayStage.LOBBY, null, false, false);


  public SpecialItemManager(PluginMain plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "special_items");
    loadSpecialItems();
  }


  private void loadSpecialItems() {
    SpecialItem.getSpecialItems().forEach((key, specialItem) -> {
      if(!"Do-Not-Edit".startsWith(specialItem.getPath())) {
        addItem(key, specialItem.getPath());
      }
    });
    for(String key : config.getKeys(false)) {
      if("Do-Not-Edit".startsWith(key)) {
        continue;
      }
      if(specialItems.values().stream().anyMatch(specialItem -> specialItem.getPath().equalsIgnoreCase(key))) {
        continue;
      }
      addItem(key, key);
    }
  }

  public void addItem(String key, String path) {
    if(!config.contains(path)) {
      return;
    }
    if(config.getBoolean(path + ".disabled", false)) {
      return;
    }
    Material mat = XMaterial.matchXMaterial(config.getString(path + ".material", "BEDROCK").toUpperCase()).orElse(XMaterial.BEDROCK).parseMaterial();
    String name = new MessageBuilder(config.getString(path + ".displayname", "Error!")).build();
    List<String> lore = config.getStringList(path + ".lore").stream()
        .map(itemLore -> itemLore = new MessageBuilder(itemLore).build())
        .collect(Collectors.toList());

    SpecialItem.DisplayStage stage = SpecialItem.DisplayStage.LOBBY;
    try {
      stage = SpecialItem.DisplayStage.valueOf(config.getString(path + ".stage").toUpperCase());
    } catch(Exception ex) {
      plugin.getDebugger().debug(Level.WARNING, "Invalid display stage of special item " + path + " in special_items.yml! Please use SERVER_JOIN, WAITING_FOR_PLAYERS, ENOUGH_PLAYERS_TO_START, LOBBY, IN_GAME, SPECTATOR or ENDING!");
    }

    Set<Reward> rewards = new HashSet<>();
    for(String reward : config.getStringList(path + ".execute")) {
      rewards.add(new Reward(new RewardType(path), reward));
    }
    String permission = config.getString(path + ".permission", null);
    ItemStack itemStack = new ItemBuilder(mat).name(name).lore(lore).build();
    int slot = config.getInt(path + ".slot", -1);
    boolean force = config.getBoolean(path + ".force", true);
    boolean move = config.getBoolean(path + ".move", false);
    specialItems.put(key, new SpecialItem(path, permission, itemStack, slot, stage, rewards, force, move));
    plugin.getDebugger().debug("Loaded SpecialItem with key {0}, permissions {1}, itemstack {2}, slot {3}, stage {4} and reward {5}", key, permission, itemStack, slot, stage, rewards.stream().map(Reward::getExecutableCode).collect(Collectors.toList()));
  }

  /**
   * Returns ItemStack if found
   *
   * @param key of SpecialItem to get value from
   * @return ItemStack
   */
  public ItemStack getSpecialItemStack(String key) {
    if(!specialItems.containsKey(key)) {
      return getInvalidItem().getItemStack();
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
      return getInvalidItem();
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
    addItem(key, specialItem.getPath());
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
      throw new IllegalStateException("Protected specialitem cannot be removed!");
    }
    specialItems.remove(key);
  }

  public Map<String, SpecialItem> getSpecialItems() {
    return Collections.unmodifiableMap(specialItems);
  }

  public List<SpecialItem> getSpecialItemsOfStage(SpecialItem.DisplayStage stage) {
    return Collections.unmodifiableList(getSpecialItems().values().stream().filter(specialItem -> specialItem.getDisplayStage() == stage).collect(Collectors.toList()));
  }

  public void addSpecialItemsOfStage(Player player, SpecialItem.DisplayStage stage) {
    for(SpecialItem specialItem : getSpecialItemsOfStage(stage)) {
      if(specialItem.getPermission() != null && !specialItem.getPermission().equalsIgnoreCase("")) {
        if(!plugin.getBukkitHelper().hasPermission(player, specialItem.getPermission())) {
          continue;
        }
      }
      specialItem.setItem(player);
    }
  }

  public void removeSpecialItemsOfStage(Player player, SpecialItem.DisplayStage stage) {
    for(SpecialItem specialItem : getSpecialItemsOfStage(stage)) {
      player.getInventory().remove(specialItem.getItemStack());
    }
  }

  public void removeSpecialItems(Player player) {
    for(SpecialItem specialItem : getSpecialItems().values()) {
      player.getInventory().remove(specialItem.getItemStack());
    }
  }

  @NotNull
  public SpecialItem getRelatedSpecialItem(ItemStack itemStack) {
    for(SpecialItem item : specialItems.values()) {
      if(item.getItemStack().isSimilar(itemStack)) {
        return item;
      }
      //After server restart items aren't similar on some mc versions as it formats the color codes in a different way, e.g. 1.18
      if(ComplementAccessor.getComplement().getDisplayName(itemStack.getItemMeta()).equalsIgnoreCase(ComplementAccessor.getComplement().getDisplayName(item.getItemStack().getItemMeta()))) {
        return item;
      }
    }
    return getInvalidItem();
  }

  public SpecialItem getInvalidItem() {
    return INVALID_ITEM;
  }
}
