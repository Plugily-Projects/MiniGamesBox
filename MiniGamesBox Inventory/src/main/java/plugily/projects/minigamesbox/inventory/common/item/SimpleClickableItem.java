package plugily.projects.minigamesbox.inventory.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * A simple clickable item contains a {@link ItemStack} and a {@link Consumer}
 */
public class SimpleClickableItem implements ClickableItem {
  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickConsumer;

  /**
   * Constructor
   *
   * @param item          the display item
   * @param clickConsumer the consumer to be called when the item is clicked
   */
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
