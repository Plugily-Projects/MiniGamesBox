package plugily.projects.minigamesbox.classic.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface HandleItem {
    ItemStack apply(Player player, ItemStack itemStack);
}
