package plugily.projects.minigamesbox.inventory.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface IClickableItem {
    ItemStack getItem();

    void onClick(InventoryClickEvent event);

    default Consumer<InventoryClickEvent> getClickConsumer() {
        return this::onClick;
    }
}
