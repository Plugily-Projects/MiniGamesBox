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

package plugily.projects.minigamesbox.inventory.paged;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.inventory.util.ItemBuilder;
import plugily.projects.minigamesbox.inventory.util.XMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 06.08.2021
 */
public class PagedFastInv implements InventoryHolder {

  private final Map<Integer, Map<Integer, Consumer<InventoryClickEvent>>> pagedItemHandlers = new HashMap<>();
  private final Map<Integer, Inventory> pagedInventories = new HashMap<>();

  private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
  private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
  private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

  private int getPages() {
    return pagedInventories.size();
  }

  private Inventory getInventory(int page) {
    return pagedInventories.get(page);
  }

  private Inventory getNextFreeInventory() {
    for(Map.Entry<Integer, Inventory> invs : pagedInventories.entrySet()) {
      if(invs.getValue().firstEmpty() >= 0) {
        return invs.getValue();
      }
    }
    //create new
    Inventory inventory = giveCleanInv(size, type, title + titleSuffix);
    pagedInventories.put(getPages() + 1, inventory);
    return inventory;
  }

  private int getInventoryPage(Inventory inventory) {
    for(Map.Entry<Integer, Inventory> invs : pagedInventories.entrySet()) {
      if(invs.getValue() == inventory) {
        return invs.getKey();
      }
    }
    //not found
    return 0;
  }

  private Map<Integer, Consumer<InventoryClickEvent>> getItemHandler(int page) {
    return pagedItemHandlers.get(page);
  }

  private Predicate<Player> closeFilter;

  /**
   * Create a new FastInv with a custom size.
   *
   * @param size The size of the inventory.
   */
  public PagedFastInv(int size) {
    this(size, InventoryType.CHEST.getDefaultTitle());
  }

  /**
   * Create a new FastInv with a custom size and title.
   *
   * @param size  The size of the inventory.
   * @param title The title (name) of the inventory.
   */
  public PagedFastInv(int size, String title) {
    this(size, InventoryType.CHEST, title);
  }

  /**
   * Create a new FastInv with a custom type.
   *
   * @param type The type of the inventory.
   */
  public PagedFastInv(InventoryType type) {
    this(Objects.requireNonNull(type, "type"), type.getDefaultTitle());
  }

  /**
   * Create a new FastInv with a custom type and title.
   *
   * @param type  The type of the inventory.
   * @param title The title of the inventory.
   */
  public PagedFastInv(InventoryType type, String title) {
    this(0, Objects.requireNonNull(type, "type"), title);
  }

  private final int size;
  private final InventoryType type;
  private final String title;
  private final String titleSuffix = "-" + (getPages() + 1);

  private PagedFastInv(int size, InventoryType type, String title) {
    this.size = size;
    this.type = type;
    this.title = title;
    Inventory inventory = giveCleanInv(size, type, title + titleSuffix);
    pagedInventories.put(getPages() + 1, inventory);
  }

  private Inventory giveCleanInv(int size, InventoryType type, String title) {
    Inventory inventory;

    if(type == InventoryType.CHEST && size > 0) {
      int invSize = getInventorySize(size);
      inventory = Bukkit.createInventory(this, invSize, title);
    } else {
      inventory = Bukkit.createInventory(this, type, title);
    }

    if(inventory.getHolder() != this) {
      throw new IllegalStateException("Inventory holder is not PagedFastInv, found: " + inventory.getHolder());
    }
    //Used symbolic which does not need language support
    for(int i = inventory.getSize() - 9; i == inventory.getSize(); i++) {
      switch(i) {
        case 4:
          if(getPages() + 1 > 1) {
            setItem(inventory, i, new ItemBuilder(XMaterial.matchXMaterial("ARROW").orElse(XMaterial.BEDROCK).parseItem()).name("<-" + getPages()).build(), e -> open(e.getWhoClicked(), inventory));
          }
          break;
        case 5:
          inventory.setItem(i, new ItemBuilder(XMaterial.matchXMaterial("MAP").orElse(XMaterial.BEDROCK).parseItem()).name(Integer.toString(getPages() + 1)).build());
          break;
        case 6:
          if(getPages() + 1 > 1) {
            final Inventory pageBehind = getInventory(getPages());
            setItem(pageBehind, i, new ItemBuilder(XMaterial.matchXMaterial("ARROW").orElse(XMaterial.BEDROCK).parseItem()).name(getPages() + "->").build(), e -> open(e.getWhoClicked(), pageBehind));
          }
          break;
        default:
          inventory.setItem(i, new ItemBuilder(XMaterial.matchXMaterial("GRAY_STAINED_GLASS_PANE").orElse(XMaterial.BEDROCK).parseItem()).name("-").build());
          break;
      }
    }
    return inventory;
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
    Inventory inventory = getNextFreeInventory();
    int slot = inventory.firstEmpty();
    if(slot >= 0) {
      setItem(inventory, slot, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on a specific slot.
   *
   * @param inventory The inventory where it should be added
   * @param slot      The slot where to add the item.
   * @param item      The item to add.
   */
  public void setItem(Inventory inventory, int slot, ItemStack item) {
    setItem(inventory, slot, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on a specific slot.
   *
   * @param page The page where it should be added
   * @param slot The slot where to add the item.
   * @param item The item to add.
   */
  public void setItem(int page, int slot, ItemStack item) {
    setItem(page, slot, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
   *
   * @param inventory The inventory where it should be added
   * @param slot      The slot where to add the item.
   * @param item      The item to add.
   * @param handler   The click handler for the item
   */
  public void setItem(Inventory inventory, int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
    inventory.setItem(slot, item);
    Map<Integer, Consumer<InventoryClickEvent>> secondPart = new HashMap<>();
    secondPart.put(slot, handler);
    if(handler != null) {
      pagedItemHandlers.put(getInventoryPage(inventory), secondPart);
    } else {
      pagedItemHandlers.remove(getInventoryPage(inventory));
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
   *
   * @param page    The page where it should be added
   * @param slot    The slot where to add the item.
   * @param item    The item to add.
   * @param handler The click handler for the item
   */
  public void setItem(int page, int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
    getInventory(page).setItem(slot, item);
    Map<Integer, Consumer<InventoryClickEvent>> secondPart = new HashMap<>();
    secondPart.put(slot, handler);
    if(handler != null) {
      pagedItemHandlers.put(page, secondPart);
    } else {
      pagedItemHandlers.remove(page);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots.
   *
   * @param inventory The inventory where it should be added
   * @param slotFrom  Starting slot to add the item in.
   * @param slotTo    Ending slot to add the item in.
   * @param item      The item to add.
   */
  public void setItems(Inventory inventory, int slotFrom, int slotTo, ItemStack item) {
    setItems(inventory, slotFrom, slotTo, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
   *
   * @param inventory The inventory where it should be added
   * @param slotFrom  Starting slot to put the item in.
   * @param slotTo    Ending slot to put the item in.
   * @param item      The item to add.
   * @param handler   The click handler for the item
   */
  public void setItems(Inventory inventory, int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int i = slotFrom; i <= slotTo; i++) {
      setItem(inventory, i, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots.
   *
   * @param page     The page where it should be added
   * @param slotFrom Starting slot to add the item in.
   * @param slotTo   Ending slot to add the item in.
   * @param item     The item to add.
   */
  public void setItems(int page, int slotFrom, int slotTo, ItemStack item) {
    setItems(page, slotFrom, slotTo, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
   *
   * @param page     The page where it should be added
   * @param slotFrom Starting slot to put the item in.
   * @param slotTo   Ending slot to put the item in.
   * @param item     The item to add.
   * @param handler  The click handler for the item
   */
  public void setItems(int page, int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int i = slotFrom; i <= slotTo; i++) {
      setItem(page, i, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiple slots.
   *
   * @param inventory The inventory where it should be added
   * @param slots     The slots where to add the item
   * @param item      The item to add.
   */
  public void setItems(Inventory inventory, int[] slots, ItemStack item) {
    setItems(inventory, slots, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
   *
   * @param inventory The inventory where it should be added
   * @param slots     The slots where to add the item
   * @param item      The item to add.
   * @param handler   The click handler for the item
   */
  public void setItems(Inventory inventory, int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int slot : slots) {
      setItem(inventory, slot, item, handler);
    }
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiple slots.
   *
   * @param page  The page where it should be added
   * @param slots The slots where to add the item
   * @param item  The item to add.
   */
  public void setItems(int page, int[] slots, ItemStack item) {
    setItems(page, slots, item, null);
  }

  /**
   * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
   *
   * @param page    The page where it should be added
   * @param slots   The slots where to add the item
   * @param item    The item to add.
   * @param handler The click handler for the item
   */
  public void setItems(int page, int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
    for(int slot : slots) {
      setItem(page, slot, item, handler);
    }
  }


  /**
   * Remove an {@link ItemStack} from the inventory.
   *
   * @param inventory The inventory where to remove the item
   * @param slot      The slot where to remove the item
   */
  public void removeItem(Inventory inventory, int slot) {
    pagedInventories.get(getInventoryPage(inventory)).clear(slot);
    pagedItemHandlers.get(getInventoryPage(inventory)).remove(slot);
  }

  /**
   * Remove multiples {@link ItemStack} from the inventory.
   *
   * @param inventory The inventory where to remove the items
   * @param slots     The slots where to remove the items
   */
  public void removeItems(Inventory inventory, int... slots) {
    for(int slot : slots) {
      removeItem(inventory, slot);
    }
  }

  /**
   * Remove an {@link ItemStack} from the inventory.
   *
   * @param page The page where to remove the item
   * @param slot The slot where to remove the item
   */
  public void removeItem(int page, int slot) {
    pagedInventories.get(page).clear(slot);
    pagedItemHandlers.get(page).remove(slot);
  }

  /**
   * Remove multiples {@link ItemStack} from the inventory.
   *
   * @param page  The page where to remove the items
   * @param slots The slots where to remove the items
   */
  public void removeItems(int page, int... slots) {
    for(int slot : slots) {
      removeItem(page, slot);
    }
  }

  /**
   * Remove all {@link ItemStack} from the inventories.
   *
   * @param itemStack The itemStack that should be removed
   */
  public void removeItem(ItemStack itemStack) {
    for(Inventory inventory : pagedInventories.values()) {
      int slot = inventory.first(itemStack);
      if(slot >= 0) {
        removeItem(inventory, slot);
      }
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
    player.openInventory(getInventory(1));
  }

  /**
   * Open the inventory to a player.
   *
   * @param player The player to open the menu.
   */
  public void open(Player player, int page) {
    player.openInventory(getInventory(page));
  }

  /**
   * Open the inventory to a player.
   *
   * @param humanEntity The humanEntity to open the menu.
   * @param inventory   The inventory to open
   */
  public void open(HumanEntity humanEntity, Inventory inventory) {
    humanEntity.openInventory(inventory);
  }

  /**
   * Open the inventory to a player.
   *
   * @param humanEntity The humanEntity to open the menu.
   */
  public void open(HumanEntity humanEntity, int page) {
    humanEntity.openInventory(getInventory(page));
  }

  /**
   * Get borders of the inventory. If the inventory size is under 27, all slots are returned.
   *
   * @return inventory borders
   */
  public int[] getBorders(int page) {
    int size = getInventory(page).getSize();
    return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
  }

  /**
   * Fill the page with items
   *
   * @param stack ItemStack to fill the inventory
   * @param page  page to fill
   */
  public void fill(ItemStack stack, int page) {
    while(getInventory(page).firstEmpty() != -1) {
      setItem(getInventory(page), getInventory(page).firstEmpty(), stack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    }
  }

  /**
   * Fill all pages with items
   *
   * @param stack ItemStack to fill the inventory
   */
  public void fill(ItemStack stack) {
    for(Inventory inventory : pagedInventories.values())
      while(inventory.firstEmpty() != -1) {
        setItem(inventory, inventory.firstEmpty(), stack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
      }
  }

  /**
   * Get corners of the inventory.
   *
   * @return inventory corners
   */
  public int[] getCorners(int page) {
    int size = getInventory(page).getSize();
    return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18 || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
  }

  /**
   * Get the Bukkit inventory.
   *
   * @return The Bukkit inventory.
   */
  @Override
  public Inventory getInventory() {
    return getInventory(1);
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

    Inventory inventory = e.getClickedInventory();
    if(inventory == null) {
      return;
    }

    Consumer<InventoryClickEvent> clickConsumer = pagedItemHandlers.get(getInventoryPage(inventory)).get(e.getRawSlot());

    if(clickConsumer != null) {
      clickConsumer.accept(e);
    }
  }
}