package plugily.projects.minigamesbox.inventory.common.item;

import java.util.HashMap;
import java.util.Map;

public class ItemMap {
    private final Map<Integer, IClickableItem> items = new HashMap<>();

    public void setItem(int slot, IClickableItem item) {
        items.put(slot, item);
    }

    public IClickableItem getItem(int slot) {
        return items.get(slot);
    }

    public Map<Integer, IClickableItem> getItems() {
        return items;
    }
}
