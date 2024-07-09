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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameStartEvent;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.SoundHelper;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginStartingState implements ArenaStateHandler {

  private PluginMain plugin;
  private int arenaTimer;
  private IArenaState IArenaState;

  @Override
  public void init(PluginMain plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(PluginArena arena) {
    setArenaState(IArenaState.STARTING);
    setArenaTimer(-999);
    plugin.getDebugger().performance("ArenaUpdate", "START Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.STARTING, IArenaState, arenaTimer);
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Arena players: {1}", arena.getId());
    for (Player player : arena.getPlayers()) {
      plugin.getDebugger().performance("ArenaUpdate", "Arena {0}: {1}", arena.getId(), player.getName());
    }


    int timer = arena.getTimer();

    double startWaiting = plugin.getConfig().getDouble("Starting-Waiting-Time", 60);
    arena.getBossbarManager().setProgress(timer / startWaiting);

    float exp = (float) (timer / startWaiting);

    for(Player player : arena.getPlayers()) {
      player.setExp(exp);
      player.setLevel(timer);
    }

    int minPlayers = arena.getMinimumPlayers();

    plugin.getDebugger().debug("Arena {0} forcestart {1} and players {2} while min is {3}", arena.getId(), arena.isForceStart(), arena.getPlayers().size(), minPlayers);
    if(arena.getPlayers().size() < minPlayers && arena.isForceStart()) {
      arena.setForceStart(false);
      new MessageBuilder("IN_GAME_MESSAGES_LOBBY_WAITING_FOR_PLAYERS").asKey().integer(minPlayers).arena(arena).sendArena();
    }
    if(!arena.isForceStart() && arena.getPlayers().size() < minPlayers) {
      arena.getBossbarManager().setProgress(1.0);
      new MessageBuilder("IN_GAME_MESSAGES_LOBBY_WAITING_FOR_PLAYERS").asKey().arena(arena).integer(minPlayers).sendArena();
      IArenaState = IArenaState.WAITING_FOR_PLAYERS;
      for(Player player : arena.getPlayers()) {
        plugin.getSpecialItemManager().removeSpecialItemsOfStage(player, SpecialItem.DisplayStage.ENOUGH_PLAYERS_TO_START);
        plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.WAITING_FOR_PLAYERS);
      }
      arenaTimer = plugin.getConfig().getInt("Time-Manager.Waiting", 20);
      for(Player player : arena.getPlayers()) {
        player.setExp(1);
        player.setLevel(0);
      }
      plugin.getDebugger().performance("ArenaUpdate", "END 1 Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.STARTING, IArenaState, arenaTimer);

      return;
    }
    if(timer == 0 || arena.isForceStart()) {
      Bukkit.getPluginManager().callEvent(new PlugilyGameStartEvent(arena));
      arena.getBossbarManager().setProgress(1.0);
      org.bukkit.Location arenaLoc = arena.getStartLocation();
      for(Player player : arena.getPlayers()) {
        VersionUtils.teleport(player, arenaLoc).thenAccept(bol -> {
        PluginArenaUtils.hidePlayersOutsideTheGame(player, arena);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        IUser user = plugin.getUserManager().getUser(player);
        if(plugin.getConfigPreferences().getOption("KITS")) {
          user.getKit().giveKitItems(player);
        }
        player.updateInventory();
        plugin.getUserManager().addExperience(player, 10);
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_GAME_START").asKey().arena(arena).player(player).sendPlayer();
        plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.IN_GAME);
        plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("START_GAME"));
        plugin.getUserManager().addStat(user, plugin.getStatsStorage().getStatisticType("GAMES_PLAYED"));
        });
      }
      arenaTimer = plugin.getConfig().getInt("Time-Manager.In-Game", 270);
      plugin.getDebugger().performance("ArenaUpdate", "Arena {0} current timer set to {1}", arena.getId(), arenaTimer);
      IArenaState = IArenaState.IN_GAME;
    }
    SoundHelper.playArenaCountdown(arena);
    if(arena.isForceStart()) {
      arena.setForceStart(false);
    }

    if(arena.getMaximumPlayers() == arena.getPlayers().size()) {
      int shorter = plugin.getConfig().getInt("Time-Manager.Shorten-Waiting-Full", 15);

      if (arena.getTimer() > shorter) {
        arenaTimer = shorter;
        IArenaState = IArenaState.FULL_GAME;
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_MAX_PLAYERS").asKey().arena(arena).sendArena();
      }
    }
    plugin.getDebugger().performance("ArenaUpdate", "END 2 Arena {0} Running state {1} value for state {2} and time {3}", arena.getId(), IArenaState.STARTING, IArenaState, arenaTimer);

  }

  @Override
  public int getArenaTimer() {
    return arenaTimer;
  }

  @Override
  public IArenaState getArenaStateChange() {
    return IArenaState;
  }

  public void setArenaTimer(int arenaTimer) {
    this.arenaTimer = arenaTimer;
  }

  public void setArenaState(IArenaState IArenaState) {
    this.IArenaState = IArenaState;
  }

  public PluginMain getPlugin() {
    return plugin;
  }
}
