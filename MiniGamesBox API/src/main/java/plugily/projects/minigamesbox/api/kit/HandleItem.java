package plugily.projects.minigamesbox.api.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface HandleItem {
    ItemStack apply(Player player, ItemStack itemStack);
}
