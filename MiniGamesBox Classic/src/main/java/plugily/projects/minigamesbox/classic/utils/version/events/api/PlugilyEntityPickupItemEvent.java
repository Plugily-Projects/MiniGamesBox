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

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public class PlugilyEntityPickupItemEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final LivingEntity entity;
  private final Item item;
  private final int remaining;

  public PlugilyEntityPickupItemEvent(LivingEntity entity, Item item, int remaining) {
    super(false);
    this.entity = entity;
    this.item = item;
    this.remaining = remaining;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public Item getItem() {
    return item;
  }

  public int getRemaining() {
    return remaining;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }


}
