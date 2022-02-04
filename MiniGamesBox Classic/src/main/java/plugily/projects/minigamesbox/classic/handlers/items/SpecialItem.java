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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 05.10.2021
 */
public class SpecialItem {

  private static final Map<String, SpecialItem> specialItems = new HashMap<>();

  static {
    //specialItems.put("KIT_SELECTOR", new SpecialItem("Kit-Selector", false));
    specialItems.put("LOBBY_LEAVE", new SpecialItem("Lobby-Leave", true, null, true));
    specialItems.put("PLAYERS_LIST", new SpecialItem("Player-List", true, null, true));
    specialItems.put("FORCESTART", new SpecialItem("Forcestart", true, null, true));
    specialItems.put("SPECTATOR_SETTINGS", new SpecialItem("Spectator-Settings", true, null, true));
    specialItems.put("SPECTATOR_LEAVE", new SpecialItem("Spectator-Leave", true, null, true));
    specialItems.put("ARENA_SELECTOR", new SpecialItem("Arena-Selector", true, null, true));
    specialItems.put("STATS", new SpecialItem("Stats", true, null, true));
    specialItems.put("BACK_TO_HUB", new SpecialItem("Back-To-Hub", true, null, true));
  }

  private final String path;
  private final String permission;
  private final ItemStack itemStack;
  private int slot;
  private final SpecialItem.DisplayStage displayStage;
  private final boolean protectedOption;
  private final Set<Reward> rewards;
  private final boolean force;

  public SpecialItem(String path, boolean protectedOption, String permission, ItemStack itemStack, int slot, DisplayStage displayStage, Set<Reward> rewards, boolean force) {
    this.path = path;
    this.protectedOption = protectedOption;
    this.permission = permission;
    this.itemStack = itemStack;
    this.slot = slot;
    this.displayStage = displayStage;
    this.rewards = rewards;
    this.force = force;
  }

  public SpecialItem(String path, ItemStack itemStack, int slot, DisplayStage displayStage, Set<Reward> rewards, boolean force) {
    this.path = path;
    this.rewards = rewards;
    this.force = force;
    this.protectedOption = false;
    this.permission = null;
    this.itemStack = itemStack;
    this.slot = slot;
    this.displayStage = displayStage;
  }

  public SpecialItem(String path, String permission, ItemStack itemStack, int slot, DisplayStage displayStage, Set<Reward> rewards, boolean force) {
    this.path = path;
    this.force = force;
    this.protectedOption = false;
    this.permission = permission;
    this.itemStack = itemStack;
    this.slot = slot;
    this.displayStage = displayStage;
    this.rewards = rewards;
  }

  public SpecialItem(String path, Set<Reward> rewards, boolean force) {
    this.path = path;
    this.rewards = rewards;
    this.force = force;
    this.protectedOption = false;
    this.permission = null;
    this.itemStack = XMaterial.BEDROCK.parseItem();
    this.slot = 0;
    this.displayStage = DisplayStage.LOBBY;
  }

  public SpecialItem(String path, boolean protectedOption, Set<Reward> rewards, boolean force) {
    this.path = path;
    this.protectedOption = protectedOption;
    this.rewards = rewards;
    this.force = force;
    this.permission = null;
    this.itemStack = XMaterial.BEDROCK.parseItem();
    this.slot = 0;
    this.displayStage = DisplayStage.LOBBY;
  }

  public String getPath() {
    return path;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public SpecialItem.DisplayStage getDisplayStage() {
    return displayStage;
  }

  public Set<Reward> getRewards() {
    return rewards;
  }

  public String getPermission() {
    return permission;
  }

  public boolean isForce() {
    return force;
  }

  public void setItem(Player player) {
    PlayerInventory playerInventory = player.getInventory();
    if(!force) {
      if(playerInventory.getItem(slot) != null && playerInventory.getItem(slot) != XMaterial.AIR.parseItem()) {
        playerInventory.addItem(itemStack);
        player.updateInventory();
        return;
      }
    }
    playerInventory.remove(itemStack);
    playerInventory.setItem(slot, itemStack);
    player.updateInventory();
  }

  public enum DisplayStage {
    SERVER_JOIN, WAITING_FOR_PLAYERS, ENOUGH_PLAYERS_TO_START, LOBBY, IN_GAME, SPECTATOR, ENDING
  }

  /**
   * @return whether option is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedOption;
  }

  public static Map<String, SpecialItem> getSpecialItems() {
    return Collections.unmodifiableMap(specialItems);
  }

}
