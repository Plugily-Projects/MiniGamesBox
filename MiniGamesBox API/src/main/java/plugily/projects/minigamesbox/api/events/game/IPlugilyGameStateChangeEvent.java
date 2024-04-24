package plugily.projects.minigamesbox.api.events.game;

import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.events.IPlugilyEvent;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPlugilyGameStateChangeEvent extends IPlugilyEvent {
  IArenaState getArenaState();
}
