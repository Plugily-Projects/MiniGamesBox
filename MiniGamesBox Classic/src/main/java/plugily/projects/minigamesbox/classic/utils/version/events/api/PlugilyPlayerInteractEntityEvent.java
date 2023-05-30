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

package plugily.projects.minigamesbox.classic.utils.version.events.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

public class PlugilyPlayerInteractEntityEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final EquipmentSlot equipmentSlot;
  private final Entity rightClicked;


  public PlugilyPlayerInteractEntityEvent(Player player, EquipmentSlot equipmentSlot, Entity rightClicked) {
    super(false);
    this.player = player;
    this.equipmentSlot = equipmentSlot;
    this.rightClicked = rightClicked;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public EquipmentSlot getHand() {
    return equipmentSlot;
  }

  public Entity getRightClicked() {
    return rightClicked;
  }
}
