package plugily.projects.minigamesbox.inventory.paged;

import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.IClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.*;
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
    private BiFunction<Integer, IClickableItem, IClickableItem> lastLineSequence = null;

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
    protected Map<Integer, IClickableItem> getClickableItemSlotMap() {
        int page = getCurrentPage();
        if (page < 0) {
            return Collections.emptyMap();
        }
        Map<Integer, IClickableItem> map = new HashMap<>(getPage(page).getItems());
        if (lastLineSequence != null) {
            int inventorySize = getInventory().getSize();
            int startSlot = inventorySize - 9;
            for (int slot = startSlot; slot < inventorySize; slot++) {
                map.put(slot, lastLineSequence.apply(slot - startSlot, map.get(slot)));
            }
        }
        return map;
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
    public void setLastLineSequence(BiFunction<Integer, IClickableItem, IClickableItem> lastLineSequence) {
        this.lastLineSequence = lastLineSequence;
    }
}
