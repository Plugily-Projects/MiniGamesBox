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

package plugily.projects.minigamesbox.inventory.paged;

import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * A multi-paged inventory.
 * Register the plugin via {@link fr.mrmicky.fastinv.FastInvManager#register(org.bukkit.plugin.Plugin)} before creating any inventory.
 *
 * @author HSGamer
 */
public class PagedFastInv extends RefreshableFastInv {
    private final List<ItemMap> pages = new ArrayList<>();
    private final AtomicInteger currentPage = new AtomicInteger(0);
    private BiFunction<Integer, ClickableItem, ClickableItem> lastLineSequence = null;

    public PagedFastInv(int size) {
        super(size);
    }

    public PagedFastInv(int size, String title) {
        super(size, title);
    }

    public PagedFastInv(InventoryType type) {
        super(type);
    }

    public PagedFastInv(InventoryType type, String title) {
        super(type, title);
    }

    @Override
    protected ItemMap getItemMap() {
        int page = getCurrentPage();
        if (page < 0) {
            return ItemMap.EMPTY;
        }
        ItemMap map = getPage(page);
        if (lastLineSequence == null) {
            return map;
        }

        // Clone the map and apply the last line sequence
        ItemMap cloneMap = new ItemMap(map);
        int inventorySize = getInventory().getSize();
        int startSlot = inventorySize - getSlotsPerLine();
        for (int slot = startSlot; slot < inventorySize; slot++) {
            ClickableItem item = lastLineSequence.apply(slot - startSlot, cloneMap.getItem(slot));
            if (item != null) {
                cloneMap.setItem(slot, item);
            }
        }
        return cloneMap;
    }

    private int getActualPage(int page) {
        if (pages.isEmpty()) {
            return -1;
        }
        int maxPage = getMaxPage();
        return (page + maxPage) % maxPage;
    }

    /**
     * Get the current page, starting from 0
     *
     * @return current page, or -1 if no pages have been added
     */
    public int getCurrentPage() {
        return getActualPage(currentPage.get());
    }

    /**
     * Set the current page
     *
     * @param page the page
     */
    public void setCurrentPage(int page) {
        currentPage.set(page);
    }

    /**
     * Get the page for displaying.
     * It's actually {@link #getCurrentPage()} but starting from 1.
     *
     * @return the display page
     */
    public int getDisplayPage() {
        return getCurrentPage() + 1;
    }

    /**
     * Get the maximum number of pages
     *
     * @return the maximum number of pages
     */
    public int getMaxPage() {
        return pages.size();
    }

    /**
     * Get the item map for the given page
     *
     * @param page the page
     * @return the item map
     */
    public ItemMap getPage(int page) {
        int actualPage = getActualPage(page);
        if (actualPage < 0) {
            throw new IllegalStateException("No pages have been added to this inventory.");
        }
        return pages.get(actualPage);
    }

    /**
     * Create a new page
     *
     * @return the item map for that new page
     */
    public ItemMap createNewPage() {
        ItemMap map = new ItemMap();
        pages.add(map);
        return map;
    }

    /**
     * Remove the page
     *
     * @param page the page
     */
    public void removePage(int page) {
        int actualPage = getActualPage(page);
        if (actualPage < 0) {
            throw new IllegalStateException("No pages have been added to this inventory.");
        }
        pages.remove(actualPage);
    }

    /**
     * Get the page list
     *
     * @return the list of all item map
     */
    public List<ItemMap> getPages() {
        return pages;
    }

    /**
     * Set the last line sequence.
     * This is used to add items to the last line of the inventory.
     * You can use it to add next-page and previous-page buttons.
     *
     * @param lastLineSequence the last line sequence, gives the hotbar slot (0-8) and the item of the slot and returns the item to add
     */
    public void setLastLineSequence(BiFunction<Integer, ClickableItem, ClickableItem> lastLineSequence) {
        this.lastLineSequence = lastLineSequence;
    }
}
