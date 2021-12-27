package plugily.projects.minigamesbox.inventory.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class SimpleClickableItem implements IClickableItem {
    private final ItemStack item;
    private final Consumer<InventoryClickEvent> clickConsumer;

    public SimpleClickableItem(ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
        this.item = item;
        this.clickConsumer = clickConsumer;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        clickConsumer.accept(event);
    }
}
