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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The slot map
 *
 * @author HSGamer
 */
public class ItemMap {
  /**
   * The empty item map
   */
  public static final ItemMap EMPTY = new ItemMap(Collections.emptyMap());

  private final Map<Integer, ClickableItem> items;
  private ClickableItem defaultItem = null;

  /**
   * Create an empty slot map
   */
  public ItemMap() {
    this.items = new HashMap<>();
  }

  /**
   * Create a new slot map
   *
   * @param items the items
   */
  public ItemMap(Map<Integer, ClickableItem> items) {
    this.items = items;
  }

  /**
   * Create a new slot map
   *
   * @param items       the items
   * @param defaultItem the default item
   */
  public ItemMap(Map<Integer, ClickableItem> items, ClickableItem defaultItem) {
    this.items = items;
    this.defaultItem = defaultItem;
  }

  /**
   * Clone the slot map
   *
   * @param itemMap the slot map
   */
  public ItemMap(ItemMap itemMap) {
    this.items = new HashMap<>(itemMap.getItems());
    this.defaultItem = itemMap.getDefaultItem();
  }

  /**
   * Set the item for the given slot
   *
   * @param slot the slot
   * @param item the item
   */
  public void setItem(int slot, ClickableItem item) {
    items.put(slot, item);
  }


  /**
   * Add the item for next free slot
   *
   * @param item the item
   */
  public void addItem(ClickableItem item) {
    for(int i = 0; i < 54; i++) {
      if(!items.containsKey(i)) {
        items.put(i, item);
        return;
      }
    }
  }


  /**
   * Get the item for the given slot
   *
   * @param slot the slot
   * @return the item
   */
  public ClickableItem getItem(int slot) {
    return items.get(slot);
  }

  /**
   * Get the item for the given slot, if the slot is not set, return the default item
   *
   * @param slot the slot
   * @return the item
   */
  public ClickableItem getItemOrDefault(int slot) {
    return items.getOrDefault(slot, defaultItem);
  }

  /**
   * Get the slot map
   *
   * @return the slot map
   */
  public Map<Integer, ClickableItem> getItems() {
    return items;
  }

  /**
   * Get the default item
   *
   * @return the default item
   */
  public ClickableItem getDefaultItem() {
    return defaultItem;
  }

  /**
   * Set the default item
   *
   * @param defaultItem the default item
   */
  public void setDefaultItem(ClickableItem defaultItem) {
    this.defaultItem = defaultItem;
  }
}
