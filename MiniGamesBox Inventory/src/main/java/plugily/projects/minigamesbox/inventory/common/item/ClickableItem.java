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
     * Create a dummy clickable item, with no click consumer
     *
     * @param item the item
     * @return the dummy clickable item
     */
    static ClickableItem of(ItemStack item) {
        return new SimpleClickableItem(item, event -> {
        });
    }

    /**
     * Create a clickable item
     *
     * @param item          the item
     * @param clickConsumer the click consumer
     * @return the clickable item
     */
    static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
        return new SimpleClickableItem(item, clickConsumer);
    }

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
