package plugily.projects.minigamesbox.inventory.normal;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

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

  /**
   * Sets the item in the specified slot.
   *
   * @param slot the slot to set the item in
   * @param item the item to set
   */
  public void setItem(int slot, ClickableItem item) {
    itemMap.setItem(slot, item);
  }

  @Override
  public void addItem(ItemStack item) {
    addItem(ClickableItem.of(item));
    super.addItem(item, null);
  }

  @Override
  public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
    addItem(ClickableItem.of(item));
    super.addItem(item, handler);
  }

  @Override
  public void setItem(int slot, ItemStack item) {
    setItem(slot, ClickableItem.of(item));
    super.setItem(slot, item);
  }

  @Override
  public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
    setItem(slot, ClickableItem.of(item));
    super.setItem(slot, item, handler);
  }

  /**
   * Sets the item in the next free slot.
   *
   * @param item the item to set
   */
  public void addItem(ClickableItem item) {
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
