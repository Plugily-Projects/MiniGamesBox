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

package plugily.projects.minigamesbox.classic.handlers.setup.items;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.12.2021
 */
public class CountItem implements ClickableItem {

  private int count;
  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickConsumer;

  /**
   * Constructor
   *
   * @param item          the display item
   * @param clickConsumer the consumer to be called when the item is clicked
   * @param count         the count the item should have
   */
  public CountItem(ItemStack item, int count, Consumer<InventoryClickEvent> clickConsumer) {
    this.item = item;
    this.clickConsumer = clickConsumer;
    this.count = count;
  }

  /**
   * Constructor
   *
   * @param item          the display item
   * @param clickConsumer the consumer to be called when the item is clicked
   */
  public CountItem(ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
    this.item = item;
    this.clickConsumer = clickConsumer;
    this.count = item.getAmount();
  }


  @Override
  public ItemStack getItem() {
    item.setAmount(count);
    return item;
  }


  @Override
  public void onClick(InventoryClickEvent event) {
    if(event.getClick() == ClickType.LEFT) {
      count++;
    }
    if(event.getClick() == ClickType.RIGHT) {
      count--;
    }
    if(count > 64 || count < 1) {
      event.getWhoClicked().sendMessage("§c§l✖ §cWarning | Please do not set amount lower than 1 or higher 64!");
      count = 1;
    }
    InventoryHolder holder = event.getInventory().getHolder();
    if(holder instanceof RefreshableFastInv) {
      ((RefreshableFastInv) holder).refresh();
    }
    clickConsumer.accept(event);
  }

}