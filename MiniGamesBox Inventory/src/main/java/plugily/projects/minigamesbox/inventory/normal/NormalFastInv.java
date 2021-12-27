package plugily.projects.minigamesbox.inventory.normal;

import org.bukkit.event.inventory.InventoryType;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.IClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.Map;

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
    protected Map<Integer, IClickableItem> getClickableItemSlotMap() {
        return itemMap.getItems();
    }

    public void setItem(int slot, IClickableItem item) {
        itemMap.setItem(slot, item);
    }

    public IClickableItem getItem(int slot) {
        return itemMap.getItem(slot);
    }

    public ItemMap getItemMap() {
        return itemMap;
    }
}
