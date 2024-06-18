/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.classic.arena.states;


import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public interface ArenaStateHandler {

  /**
   * Initiate class with injecting the main plugin class
   *
   * @param plugin class to inject
   */
  void init(PluginMain plugin);

  /**
   * Handle call for the current arena state for arena.
   * Basically when arena state is IN_GAME, the in game
   * state will be handled and called via that method.
   *
   * @param arena arena to call state update for
   */
  void handleCall(PluginArena arena);

  /**
   * Handle arena timer change after state is executed.
   *
   * @return int which will be used for arena.setTimer(timer)
   */
  int getArenaTimer();

  /**
   *
   * @return ArenaState that will be executed as next
   */
  IArenaState getArenaStateChange();

}
