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

package plugily.projects.minigamesbox.inventory.normal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky
 * @version 3.0.3
 */
public class FastInv implements InventoryHolder {

  private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
  private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
  private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
  private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

  private final Inventory inventory;

  private Predicate<Player> closeFilter;

  /**
   * Create a new FastInv with a custom size.
   *
   * @param size The size of the inventory.
   */
  public FastInv(int size) {
    this(size, InventoryType.CHEST.getDefaultTitle());
  }

  /**
   * Create a new FastInv with a custom size and title.
   *
   * @param size  The size of the inventory.
   * @param title The title (name) of the inventory.
   */
  public FastInv(int size, String title) {
    this(size, InventoryType.CHEST, title);
  }

  /**
   * Create a new FastInv with a custom type.
   *
   * @param type The type of the inventory.
   */
  public FastInv(InventoryType type) {
    this(Objects.requireNonNull(type, "type"), type.getDefaultTitle());
  }

  /**
   * Create a new FastInv with a custom type and title.
   *
   * @param type  The type of the inventory.
   * @param title The title of the inventory.
   */
  public FastInv(InventoryType type, String title) {
    this(0, Objects.requireNonNull(type, "type"), title);
  }

  private FastInv(int size, InventoryType type, String title) {

    if(type == InventoryType.CHEST && size > 0) {
      int invSize = getInventorySize(size);
      this.inventory = Bukkit.createInventory(this, invSize, title);
    } else {
      this.inventory = Bukkit.createInventory(this, type, title);
    }

    if(this.inventory.getHolder() != this) {
      throw new IllegalStateException("Inventory holder is not FastInv, found: " + this.inventory.getHolder());
    }
  }


  private int getInventorySize(int size) {
    if(size <= 9) {
      return 9;
    }
    if(size <= 9 * 2) {
      return 9 * 2;
    }
    if(size <= 9 * 3) {
      return 9 * 3;
    }
    if(size <= 9 * 4) {
      return 9 * 4;
    }
    if(size <= 9 * 5) {
      return 9 * 5;
    }
    if(size <= 9 * 6) {
      return 9 * 6;
    }
    return 9;
  }


  protected void onOpen(InventoryOpenEvent event) {
  }

  protected void onClick(InventoryClickEvent event) {
  }

  protected void onClose(InventoryCloseEvent event) {
  }

  /**
   * Add an {@link ItemStack} to the inventory on the first empty slot.
   *
   * @param item The ItemStack to add
   */
  public void addItem(ItemStack item) {
    addItem(item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
   *
   * @param item    The item to add.
   * @param handler The the click handler for the item.
   */
  public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
    int slot = this.inventory.firstEmpty();
    if(slot >= 0) {
      setItem(slot, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on a specific slot.
   *
   * @param slot The slot where to add the item.
   * @param item The item to add.
   */
  public void setItem(int slot, ItemStack item) {
    setItem(slot, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
   *
   * @param slot    The slot where to add the item.
   * @param item    The item to add.
   * @param handler The click handler for the item
   */
  public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
    this.inventory.setItem(slot, item);

    if(handler != null) {
      this.itemHandlers.put(slot, handler);
    } else {
      this.itemHandlers.remove(slot);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots.
   *
   * @param slotFrom Starting slot to add the item in.
   * @param slotTo   Ending slot to add the item in.
   * @param item     The item to add.
   */
  public void setItems(int slotFrom, int slotTo, ItemStack item) {
    setItems(slotFrom, slotTo, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
   *
   * @param slotFrom Starting slot to put the item in.
   * @param slotTo   Ending slot to put the item in.
   * @param item     The item to add.
   * @param handler  The click handler for the item
   */
  public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int i = slotFrom; i <= slotTo; i++) {
      setItem(i, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiple slots.
   *
   * @param slots The slots where to add the item
   * @param item  The item to add.
   */
  public void setItems(int[] slots, ItemStack item) {
    setItems(slots, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
   *
   * @param slots   The slots where to add the item
   * @param item    The item to add.
   * @param handler The click handler for the item
   */
  public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int slot : slots) {
      setItem(slot, item, handler);
    }
  }

  /**
   * Remove an {@link ItemStack} from the inventory.
   *
   * @param slot The slot where to remove the item
   */
  public void removeItem(int slot) {
    this.inventory.clear(slot);
    this.itemHandlers.remove(slot);
  }

  /**
   * Remove multiples {@link ItemStack} from the inventory.
   *
   * @param slots The slots where to remove the items
   */
  public void removeItems(int... slots) {
    for(int slot : slots) {
      removeItem(slot);
    }
  }

  /**
   * Add a close filter to prevent players from closing the inventory.
   * To prevent a player from closing the inventory the predicate should return {@code true}.
   *
   * @param closeFilter The close filter
   */
  public void setCloseFilter(Predicate<Player> closeFilter) {
    this.closeFilter = closeFilter;
  }

  /**
   * Add a handler to handle inventory open.
   *
   * @param openHandler The handler to add.
   */
  public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
    this.openHandlers.add(openHandler);
  }

  /**
   * Add a handler to handle inventory close.
   *
   * @param closeHandler The handler to add
   */
  public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
    this.closeHandlers.add(closeHandler);
  }

  /**
   * Add a handler to handle inventory click.
   *
   * @param clickHandler The handler to add.
   */
  public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
    this.clickHandlers.add(clickHandler);
  }


  /**
   * Remove all handlers which handles inventory open.
   */
  public void removeOpenHandlers() {
    this.openHandlers.clear();
  }

  /**
   * Remove all handlers which handles inventory close.
   */
  public void removeCloseHandlers() {
    this.closeHandlers.clear();
  }

  /**
   * Remove all handlers which handles inventory click.
   */
  public void removeClickHandlers() {
    this.clickHandlers.clear();
  }


  /**
   * Open the inventory to a player.
   *
   * @param player The player to open the menu.
   */
  public void open(Player player) {
    player.openInventory(this.inventory);
  }

  /**
   * Get borders of the inventory. If the inventory size is under 27, all slots are returned.
   *
   * @return inventory borders
   */
  public int[] getBorders() {
    int size = this.inventory.getSize();
    return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
  }

  /**
   * Get corners of the inventory.
   *
   * @return inventory corners
   */
  public int[] getCorners() {
    int size = this.inventory.getSize();
    return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18 || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
  }


  /**
   * Fill the inventory with items
   *
   * @param stack ItemStack to fill the inventory
   */
  public void fill(ItemStack stack) {
    while(this.inventory.firstEmpty() != -1) {
      setItem(this.inventory.firstEmpty(), stack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    }
  }

  /**
   * Get the Bukkit inventory.
   *
   * @return The Bukkit inventory.
   */
  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  void handleOpen(InventoryOpenEvent e) {
    onOpen(e);

    this.openHandlers.forEach(c -> c.accept(e));
  }

  boolean handleClose(InventoryCloseEvent e) {
    onClose(e);

    this.closeHandlers.forEach(c -> c.accept(e));

    return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
  }

  void handleClick(InventoryClickEvent e) {
    onClick(e);

    this.clickHandlers.forEach(c -> c.accept(e));

    Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

    if(clickConsumer != null) {
      clickConsumer.accept(e);
    }
  }
}
