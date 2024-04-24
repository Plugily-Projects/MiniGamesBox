package plugily.projects.minigamesbox.api.events;

import plugily.projects.minigamesbox.api.arena.IPluginArena;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPlugilyEvent {
  /**
   * Returns event arena
   *
   * @return event arena
   */
  IPluginArena getArena();

  /**
   * Returns the plugin name
   * @return plugin name
   */
  String getPluginName();
}
