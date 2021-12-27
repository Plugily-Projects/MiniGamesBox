package plugily.projects.minigamesbox.inventory.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * A clickable item
 *
 * @author HSGamer
 */
public interface ClickableItem {
    /**
     * Get the display item
     *
     * @return the display item
     */
    ItemStack getItem();

    /**
     * Called when the item is clicked
     *
     * @param event the click event
     */
    void onClick(InventoryClickEvent event);

    /**
     * Get the click consumer
     *
     * @return the click consumer
     */
    default Consumer<InventoryClickEvent> getClickConsumer() {
        return this::onClick;
    }
}
