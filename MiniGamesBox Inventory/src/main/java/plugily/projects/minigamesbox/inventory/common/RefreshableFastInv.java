package plugily.projects.minigamesbox.inventory.common;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.item.IClickableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A refreshable inventory
 *
 * @author HSGamer
 */
public abstract class RefreshableFastInv extends FastInv {
    protected final List<Integer> borderSlots = new ArrayList<>();
    protected final List<Integer> cornerSlots = new ArrayList<>();
    protected boolean isForceRefresh = false;
    protected IClickableItem borderItem = null;
    protected IClickableItem cornerItem = null;
    protected IClickableItem defaultItem = null;

    public RefreshableFastInv(int size) {
        super(size);
        initSlots();
    }

    public RefreshableFastInv(int size, String title) {
        super(size, title);
        initSlots();
    }

    public RefreshableFastInv(InventoryType type) {
        super(type);
        initSlots();
    }

    public RefreshableFastInv(InventoryType type, String title) {
        super(type, title);
        initSlots();
    }

    private void initSlots() {
        for (int slot : getBorders()) {
            borderSlots.add(slot);
        }
        for (int slot : getCorners()) {
            cornerSlots.add(slot);
        }
    }

    /**
     * Get the item slot map
     *
     * @return the item slot map
     */
    protected abstract Map<Integer, IClickableItem> getClickableItemSlotMap();

    /**
     * Refresh the inventory.
     * Should be called when initializing and after changing the item slot map.
     */
    public void refresh() {
        Map<Integer, IClickableItem> clickableItemSlotMap = getClickableItemSlotMap();

        int inventorySize = getInventory().getSize();
        for (int i = 0; i < inventorySize; i++) {
            if (clickableItemSlotMap.containsKey(i)) {
                IClickableItem clickableItem = clickableItemSlotMap.get(i);
                setItem(i, clickableItem.getItem(), clickableItem.getClickConsumer());
            } else if (cornerItem != null && cornerSlots.contains(i)) {
                setItem(i, cornerItem.getItem(), cornerItem.getClickConsumer());
            } else if (borderItem != null && borderSlots.contains(i)) {
                setItem(i, borderItem.getItem(), borderItem.getClickConsumer());
            } else if (defaultItem != null) {
                setItem(i, defaultItem.getItem(), defaultItem.getClickConsumer());
            } else {
                removeItem(i);
            }
        }

        if (isForceRefresh) {
            getInventory().getViewers().forEach(viewer -> {
                if (viewer instanceof Player) {
                    Player player = (Player) viewer;
                    player.updateInventory();
                }
            });
        }
    }

    /**
     * Should the plugin force viewers to refresh their inventory?
     * If set to false, the inventory will still be refreshed, but viewers will not be forced to refresh their inventory.
     *
     * @param forceRefresh true to force refresh
     */
    public void setForceRefresh(boolean forceRefresh) {
        isForceRefresh = forceRefresh;
    }

    /**
     * Set the border item. Set to null to disable.
     *
     * @param borderItem the border item
     */
    public void setBorderItem(IClickableItem borderItem) {
        this.borderItem = borderItem;
    }

    /**
     * Set the corner item. Set to null to disable.
     *
     * @param cornerItem the corner item
     */
    public void setCornerItem(IClickableItem cornerItem) {
        this.cornerItem = cornerItem;
    }

    /**
     * Set the default item. Set to null to disable.
     *
     * @param defaultItem the default item
     */
    public void setDefaultItem(IClickableItem defaultItem) {
        this.defaultItem = defaultItem;
    }
}
