package plugily.projects.minigamesbox.api.arena.managers;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IBossbarManager {
  void bossBarUpdate();

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param player player
   */
  void doBarAction(IPluginArena.IBarAction action, Player player);
}
