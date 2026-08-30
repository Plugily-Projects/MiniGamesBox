/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.api.events.game;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.events.PlugilyEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 * Called when arena is stopped (game has ended)
 */
public class PlugilyGameStopEvent extends PlugilyEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final List<Player> players;

  public PlugilyGameStopEvent(IPluginArena arena) {
    super(arena);
    players = Collections.unmodifiableList(new ArrayList<>(arena.getPlayers()));
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  /**
   * Gets a snapshot of the players who were in the arena when the game stopped.
   *
   * @return an immutable list of players
   */
  public List<Player> getPlayers() {
    return players;
  }

}
