package plugily.projects.minigamesbox.inventory.normal;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.function.Consumer;

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
  public ItemMap getItemMap() {
    return itemMap;
  }

  @Override
  public void addItem(ItemStack item) {
    addItemToMap(ClickableItem.of(item));
    super.addItem(item, null);
  }

  public void addItem(ClickableItem clickableItem) {
    addItemToMap(clickableItem);
    super.addItem(clickableItem.getItem(), clickableItem.getClickConsumer());
  }

  @Override
  public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
    addItemToMap(ClickableItem.of(item));
    super.addItem(item, handler);
  }

  @Override
  public void setItem(int slot, ItemStack item) {
    setItemToMap(slot, ClickableItem.of(item));
    super.setItem(slot, item);
  }

  public void setItem(int slot, ClickableItem clickableItem) {
    setItemToMap(slot, clickableItem);
    super.setItem(slot, clickableItem.getItem(), clickableItem.getClickConsumer());
  }

  @Override
  public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
    setItemToMap(slot, ClickableItem.of(item));
    super.setItem(slot, item, handler);
  }

  /**
   * Sets the item in the specified slot.
   *
   * @param slot the slot to set the item in
   * @param item the item to set
   */
  private void setItemToMap(int slot, ClickableItem item) {
    itemMap.setItem(slot, item);
  }

  /**
   * Sets the item in the next free slot.
   *
   * @param item the item to set
   */
  private void addItemToMap(ClickableItem item) {
    itemMap.addItem(item);
  }

  /**
   * Sets the default item.
   *
   * @param item the item to set
   */
  public void setDefaultItem(ClickableItem item) {
    itemMap.setDefaultItem(item);
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
}
