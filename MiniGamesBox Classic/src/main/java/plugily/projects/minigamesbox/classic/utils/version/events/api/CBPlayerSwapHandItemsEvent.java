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

package plugily.projects.minigamesbox.classic.utils.version.events.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CBPlayerSwapHandItemsEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final ItemStack mainHandItem;
  private final ItemStack offHandItem;
  private final Player player;

  public CBPlayerSwapHandItemsEvent(Player player, ItemStack mainHandItem, ItemStack offHandItem) {
    super(false);
    this.player = player;
    this.mainHandItem = mainHandItem;
    this.offHandItem = offHandItem;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public ItemStack getMainHandItem() {
    return mainHandItem;
  }

  public ItemStack getOffHandItem() {
    return offHandItem;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }



}
