package plugily.projects.minigamesbox.inventory.common.item;

import java.util.HashMap;
import java.util.Map;

/**
 * The slot map
 *
 * @author HSGamer
 */
public class ItemMap {
    private final Map<Integer, ClickableItem> items = new HashMap<>();

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
     * Get the item for the given slot
     *
     * @param slot the slot
     * @return the item
     */
    public ClickableItem getItem(int slot) {
        return items.get(slot);
    }

    /**
     * Get the slot map
     *
     * @return the slot map
     */
    public Map<Integer, ClickableItem> getItems() {
        return items;
    }
}
