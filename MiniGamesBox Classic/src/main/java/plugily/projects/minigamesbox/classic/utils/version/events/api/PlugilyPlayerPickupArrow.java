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

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;

public class PlugilyPlayerPickupArrow extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Item item;
  private final Projectile arrow;
  private final Player player;
  private final int remaining;
  private final boolean flyAtPlayer;

  public PlugilyPlayerPickupArrow(Player player, Item item, Projectile arrow, int remaining, boolean flyAtPlayer) {
    super(false);
    this.player = player;
    this.item = item;
    this.arrow = arrow;
    this.remaining = remaining;
    this.flyAtPlayer = flyAtPlayer;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public int getRemaining() {
    return remaining;
  }

  public Projectile getArrow() {
    return arrow;
  }

  public Item getItem() {
    return item;
  }

  public boolean isFlyAtPlayer() {
    return flyAtPlayer;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }


}
