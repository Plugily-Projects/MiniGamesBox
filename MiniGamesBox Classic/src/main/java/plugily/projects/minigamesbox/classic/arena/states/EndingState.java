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

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.ArenaUtils;
import plugily.projects.minigamesbox.classic.user.User;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class EndingState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    arena.getScoreboardManager().stopAllScoreboards();

    int timer = arena.getTimer();

    if(timer <= 0) {
      String teleportedToLobby = plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("COMMANDS_TELEPORTED_TO_LOBBY");

      for(Player player : arena.getPlayers()) {
        ArenaUtils.resetPlayerAfterGame(player);
        arena.getBossbarManager().doBarAction(Arena.BarAction.REMOVE, player);
        arena.teleportToEndLocation(player);

        User user = plugin.getUserManager().getUser(player);

        plugin.getUserManager().addStat(user, plugin.getStatsStorage().getStatisticType("GAMES_PLAYED"));
        user.setSpectator(false);
        arena.getScoreboardManager().removeScoreboard(user);
        plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("END_GAME"));

        if(!teleportedToLobby.isEmpty()) {
          player.sendMessage(teleportedToLobby);
        }
      }

      arena.setArenaState(ArenaState.RESTARTING);
    }
    arena.setTimer(timer - 1);
  }

}
