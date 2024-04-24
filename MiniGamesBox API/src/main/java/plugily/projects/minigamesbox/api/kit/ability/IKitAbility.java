package plugily.projects.minigamesbox.api.kit.ability;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IKitAbility {
  String getName();

  Consumer<InventoryClickEvent> getClickConsumer();
}
