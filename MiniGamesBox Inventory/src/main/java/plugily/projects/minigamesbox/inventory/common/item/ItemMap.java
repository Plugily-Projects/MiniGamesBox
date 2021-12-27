package plugily.projects.minigamesbox.inventory.common.item;

import java.util.HashMap;
import java.util.Map;

/**
 * The slot map
 *
 * @author HSGamer
 */
public class ItemMap {
    private final Map<Integer, IClickableItem> items = new HashMap<>();

    /**
     * Set the item for the given slot
     *
     * @param slot the slot
     * @param item the item
     */
    public void setItem(int slot, IClickableItem item) {
        items.put(slot, item);
    }

    /**
     * Get the item for the given slot
     *
     * @param slot the slot
     * @return the item
     */
    public IClickableItem getItem(int slot) {
        return items.get(slot);
    }

    /**
     * Get the slot map
     *
     * @return the slot map
     */
    public Map<Integer, IClickableItem> getItems() {
        return items;
    }
}
