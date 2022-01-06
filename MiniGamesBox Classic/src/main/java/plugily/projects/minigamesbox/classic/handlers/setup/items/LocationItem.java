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
import plugily.projects.minigamesbox.classic.utils.items.HandlerItem;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class LocationItem implements ClickableItem {

  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;
  private boolean rightClick = false;
  private boolean leftClick = true;
  private boolean physical = false;


  public LocationItem(ItemStack item, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer) {
    this.item = item;
    this.clickConsumer = clickConsumer;
    this.interactConsumer = interactConsumer;
  }

  public LocationItem(ItemStack item, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer, boolean leftClick, boolean rightClick, boolean physical) {
    this.item = item;
    this.clickConsumer = clickConsumer;
    this.interactConsumer = interactConsumer;
    this.leftClick = leftClick;
    this.rightClick = rightClick;
    this.physical = physical;
  }


  @Override
  public ItemStack getItem() {
    return item;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    if(event.getClick() == ClickType.SHIFT_LEFT) {
      HandlerItem handlerItem = new HandlerItem(new ItemBuilder(item).amount(1).build());
      handlerItem.addDropHandler(dropEvent -> {
        handlerItem.remove();
        dropEvent.getPlayer().getInventory().remove(dropEvent.getItemDrop().getItemStack());
        dropEvent.getItemDrop().remove();
        dropEvent.getPlayer().updateInventory();
      });
      handlerItem.addConsumeHandler(consumeEvent -> consumeEvent.setCancelled(true));
      handlerItem.addInteractHandler(interactEvent -> {
        event.setCancelled(true);
        interactConsumer.accept(interactEvent);
      });
      handlerItem.setLeftClick(leftClick);
      handlerItem.setPhysical(physical);
      handlerItem.setRightClick(rightClick);
      event.getWhoClicked().getInventory().addItem(handlerItem.getItemStack());
    }
    InventoryHolder holder = event.getInventory().getHolder();
    if(holder instanceof RefreshableFastInv) {
      ((RefreshableFastInv) holder).refresh();
    }
    clickConsumer.accept(event);
  }
}
