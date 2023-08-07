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
 * A simple clickable item contains a {@link ItemStack} and a {@link Consumer}
 */
public class SimpleClickableItem implements ClickableItem {
  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickConsumer;

  /**
   * Constructor
   *
   * @param item          the display item
   * @param clickConsumer the consumer to be called when the item is clicked
   */
  public SimpleClickableItem(ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
    this.item = item;
    this.clickConsumer = clickConsumer;
  }

  @Override
  public ItemStack getItem() {
    return item;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    clickConsumer.accept(event);
  }

}
