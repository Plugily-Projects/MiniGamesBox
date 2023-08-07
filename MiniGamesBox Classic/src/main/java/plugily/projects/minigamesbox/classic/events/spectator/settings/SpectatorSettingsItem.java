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

package plugily.projects.minigamesbox.classic.events.spectator.settings;

import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;

import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.12.2021
 */
public class SpectatorSettingsItem {

  private final ItemStack itemStack;
  private final int slot;
  private final String permission;
  private final Set<Reward> rewards;
  private final Type type;

  public SpectatorSettingsItem(ItemStack itemStack, int slot, String permission, Set<Reward> rewards, Type type) {
    this.itemStack = itemStack;
    this.slot = slot;
    this.permission = permission;
    this.rewards = rewards;
    this.type = type;
  }

  public SpectatorSettingsItem(ItemStack itemStack, int slot) {
    this.itemStack = itemStack;
    this.slot = slot;
    this.type = null;
    this.permission = null;
    this.rewards = null;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getSlot() {
    return slot;
  }

  public String getPermission() {
    return permission;
  }

  public Set<Reward> getRewards() {
    return rewards;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    DEFAULT_SPEED, SPEED1, SPEED2, SPEED3, SPEED4, AUTO_TELEPORT,
    NIGHT_VISION, FIRST_PERSON_MODE, SPECTATORS_VISIBILITY, NONE
  }

}
