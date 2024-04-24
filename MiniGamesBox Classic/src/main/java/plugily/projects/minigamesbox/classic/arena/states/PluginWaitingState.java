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


import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginWaitingState implements ArenaStateHandler {

  private PluginMain plugin;
  private int arenaTimer;
  private IArenaState arenaState;

  @Override
  public void init(PluginMain plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(PluginArena arena) {
    setArenaState(IArenaState.WAITING_FOR_PLAYERS);
    setArenaTimer(-999);
    plugin.getDebugger().performance("ArenaUpdate","START Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.WAITING_FOR_PLAYERS, arenaState, arenaTimer);

    int minPlayers = arena.getMinimumPlayers();

    if(arena.getPlayers().size() < minPlayers) {
      if(arena.getTimer() <= 0) {
        arenaTimer = plugin.getConfig().getInt("Time-Manager.Waiting", 20);
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_WAITING_FOR_PLAYERS").asKey().integer(minPlayers).arena(arena).sendArena();
      }
      plugin.getDebugger().performance("ArenaUpdate","END 1 Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.WAITING_FOR_PLAYERS, arenaState, arenaTimer);

      return;
    }
    new MessageBuilder("IN_GAME_MESSAGES_LOBBY_ENOUGH_PLAYERS").asKey().arena(arena).sendArena();
    arenaState = IArenaState.STARTING;
    arenaTimer = plugin.getConfig().getInt("Time-Manager.Starting", 60);
    for(Player player : arena.getPlayers()) {
      plugin.getSpecialItemManager().removeSpecialItemsOfStage(player, SpecialItem.DisplayStage.WAITING_FOR_PLAYERS);
      plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.ENOUGH_PLAYERS_TO_START);
    }
    plugin.getDebugger().performance("ArenaUpdate","END 2 Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.WAITING_FOR_PLAYERS, arenaState, arenaTimer);

  }

  @Override
  public int getArenaTimer() {
    return arenaTimer;
  }

  @Override
  public IArenaState getArenaStateChange() {
    return arenaState;
  }

  public void setArenaTimer(int arenaTimer) {
    this.arenaTimer = arenaTimer;
  }

  public void setArenaState(IArenaState arenaState) {
    this.arenaState = arenaState;
  }

  public PluginMain getPlugin() {
    return plugin;
  }
}
