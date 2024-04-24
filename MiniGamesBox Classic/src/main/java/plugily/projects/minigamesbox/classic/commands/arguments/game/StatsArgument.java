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

package plugily.projects.minigamesbox.classic.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.Collection;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class StatsArgument {

  public StatsArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("stats", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = args.length == 2 ? Bukkit.getPlayerExact(args[1]) : (Player) sender;
        if(player == null) {
          new MessageBuilder("COMMANDS_PLAYER_NOT_FOUND").asKey().send(sender);
          return;
        }
        IUser user = registry.getPlugin().getUserManager().getUser(player);
        sendLeaderboardHeader(sender, player);
        sendLeaderboardBody(sender, user);
        sendLeaderboardFooter(sender);
      }

      private void sendLeaderboardFooter(CommandSender sender) {
        new MessageBuilder("LEADERBOARD_TYPE_CHAT_FOOTER").asKey().send(sender);
      }

      private void sendLeaderboardBody(CommandSender sender, IUser user) {
        Collection<IStatisticType> statisticTypes = registry.getPlugin().getStatsStorage().getStatistics().values();
        for(IStatisticType statisticType : statisticTypes) {
          if(!statisticType.isPersistent()) {
            continue;
          }
          String statisticKey = statisticType.getName().toUpperCase();
          new MessageBuilder("LEADERBOARD_STATISTICS_" + statisticKey).asKey().player(user.getPlayer()).integer(user.getStatistic(statisticType)).send(sender);
        }
      }

      private void sendLeaderboardHeader(CommandSender sender, Player player) {
        if(player == sender) {
          new MessageBuilder("LEADERBOARD_TYPE_CHAT_HEADER").asKey().player(player).sendPlayer();
        } else {
          new MessageBuilder("LEADERBOARD_TYPE_CHAT_HEADER").asKey().player(player).send(sender);
        }
      }
    });
  }

}
