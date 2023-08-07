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

package plugily.projects.minigamesbox.inventory.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * A clickable item
 *
 * @author HSGamer
 */
public interface ClickableItem {
  /**
   * Create a dummy clickable item, with no click consumer
   *
   * @param item the item
   * @return the dummy clickable item
   */
  static ClickableItem of(ItemStack item) {
    return new SimpleClickableItem(item, event -> {
    });
  }

  /**
   * Create a clickable item
   *
   * @param item          the item
   * @param clickConsumer the click consumer
   * @return the clickable item
   */
  static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
    return new SimpleClickableItem(item, clickConsumer);
  }

  /**
   * Get the display item
   *
   * @return the display item
   */
  ItemStack getItem();

  /**
   * Called when the item is clicked
   *
   * @param event the click event
   */
  void onClick(InventoryClickEvent event);

  /**
   * Get the click consumer
   *
   * @return the click consumer
   */
  default Consumer<InventoryClickEvent> getClickConsumer() {
    return this::onClick;
  }
}
