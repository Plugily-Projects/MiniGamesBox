package plugily.projects.minigamesbox.api.handlers.powerup;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPowerupPickupHandler {
  BasePowerup getPowerup();

  IPluginArena getArena();

  Player getPlayer();
}
