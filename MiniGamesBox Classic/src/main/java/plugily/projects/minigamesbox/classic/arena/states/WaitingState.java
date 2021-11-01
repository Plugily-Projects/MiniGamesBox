/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
 *
 */

package plugily.projects.minigamesbox.classic.arena.states;


import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class WaitingState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    int minPlayers = arena.getMinimumPlayers();

    if(arena.getPlayers().size() < minPlayers) {
      if(arena.getTimer() <= 0) {
        arena.setTimer(45);
        plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_WAITING_FOR_PLAYERS), minPlayers));
        return;
      }
    } else {
      if(arena.getGameBar() != null) {
        arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_WAITING_FOR_PLAYERS));
      }
      plugin.getChatManager().broadcast(arena, Messages.LOBBY_MESSAGES_ENOUGH_PLAYERS_TO_START);
      arena.setArenaState(ArenaState.STARTING);
      arena.setTimer(plugin.getConfig().getInt("Starting-Waiting-Time", 60));
    }
    arena.setTimer(arena.getTimer() - 1);
  }

}
