package plugily.projects.minigamesbox.api.events;

import org.bukkit.event.Event;
import plugily.projects.minigamesbox.api.arena.IPluginArena;

/**
 * Represents Plugily Projects game related events.
 *
 * @author Lagggpixel
 * @since April 24, 2024
 */
public abstract class PlugilyEvent extends Event {

  protected IPluginArena arena;
  protected String pluginName;

  public PlugilyEvent(IPluginArena eventArena) {
    arena = eventArena;
    if(arena != null) {
      pluginName = eventArena.getPlugin().getName();
    } else {
      pluginName = "null";
    }
  }

  public IPluginArena getArena() {
    return arena;
  }

  public String getPluginName() {
    return pluginName;
  }
}