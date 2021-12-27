package plugily.projects.minigamesbox.inventory.paged;

import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.IClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

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
            for (int slot = inventorySize - 9; slot < inventorySize; slot++) {
                map.put(slot, lastLineSequence.apply(slot, map.get(slot)));
            }
        }
        return map;
    }

    private int getActualPage(int page) {
        if (pages.isEmpty()) {
            return -1;
        }
        return page % getMaxPage();
    }

    public int getCurrentPage() {
        return getActualPage(currentPage.get());
    }

    public void setCurrentPage(int page) {
        currentPage.set(page);
    }

    public int getDisplayPage() {
        return getCurrentPage() + 1;
    }

    public int getMaxPage() {
        return pages.size();
    }

    public ItemMap getPage(int page) {
        int actualPage = getActualPage(page);
        if (actualPage < 0) {
            throw new IllegalStateException("No pages have been added to this inventory.");
        }
        return pages.get(actualPage);
    }

    public ItemMap createNewPage() {
        ItemMap map = new ItemMap();
        pages.add(map);
        return map;
    }

    public void removePage(int page) {
        int actualPage = getActualPage(page);
        if (actualPage < 0) {
            throw new IllegalStateException("No pages have been added to this inventory.");
        }
        pages.remove(actualPage);
    }

    public List<ItemMap> getPages() {
        return pages;
    }

    public void setLastLineSequence(BiFunction<Integer, IClickableItem, IClickableItem> lastLineSequence) {
        this.lastLineSequence = lastLineSequence;
    }
}
