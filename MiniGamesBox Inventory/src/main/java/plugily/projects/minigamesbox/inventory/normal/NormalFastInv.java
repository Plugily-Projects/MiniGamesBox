package plugily.projects.minigamesbox.inventory.normal;

import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.Map;

/**
 * A normal, single-paged inventory.
 * Register the plugin via {@link fr.mrmicky.fastinv.FastInvManager#register(org.bukkit.plugin.Plugin)} before creating any inventory.
 *
 * @author HSGamer
 */
public class NormalFastInv extends RefreshableFastInv {
    private final ItemMap itemMap = new ItemMap();

    public NormalFastInv(int size) {
        super(size);
    }

    public NormalFastInv(int size, String title) {
        super(size, title);
    }

    public NormalFastInv(InventoryType type) {
        super(type);
    }

    public NormalFastInv(InventoryType type, String title) {
        super(type, title);
    }

    @Override
    protected Map<Integer, ClickableItem> getClickableItemSlotMap() {
        return itemMap.getItems();
    }

    /**
     * Sets the item in the specified slot.
     *
     * @param slot the slot to set the item in
     * @param item the item to set
     */
    public void setItem(int slot, ClickableItem item) {
        itemMap.setItem(slot, item);
    }

    /**
     * Gets the item in the specified slot.
     *
     * @param slot the slot to get the item from
     * @return the item in the specified slot
     */
    public ClickableItem getItem(int slot) {
        return itemMap.getItem(slot);
    }

    /**
     * Get the item map.
     *
     * @return the item map
     */
    public ItemMap getItemMap() {
        return itemMap;
    }
}
