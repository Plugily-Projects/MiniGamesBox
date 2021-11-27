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

package plugily.projects.minigamesbox.classic.commands.arguments.game;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.completion.CompletableArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class LeaderboardArgument {

  private final ArgumentsRegistry registry;

  public LeaderboardArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    List<String> stats = new ArrayList<>();
    for(StatisticType val : registry.getPlugin().getStatsStorage().getStatistics().values()) {
      if(!val.isPersistent() || val == registry.getPlugin().getStatsStorage().getStatisticType("XP")) {
        continue;
      }
      stats.add(val.getName().toLowerCase());
    }
    registry.getTabCompletion().registerCompletion(new CompletableArgument(registry.getPlugin().getPluginNamePrefixLong(), "top", stats));
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("top", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_STATS_TOP_TYPE_NAME"));
          return;
        }
        try {
          StatisticType statisticType = registry.getPlugin().getStatsStorage().getStatisticType(args[1].toUpperCase());
          if(statisticType == registry.getPlugin().getStatsStorage().getStatisticType("XP")) {
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_STATS_TOP_INVALID_NAME"));
            return;
          }
          printLeaderboard(sender, statisticType);
        } catch(IllegalArgumentException e) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_STATS_TOP_INVALID_NAME"));
        }
      }
    });
  }

  private void printLeaderboard(CommandSender sender, StatisticType statisticType) {
    java.util.Map<UUID, Integer> stats = registry.getPlugin().getStatsStorage().getStats(statisticType);
    sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_HEADER"));
    String statistic = StringUtils.capitalize(statisticType.toString().toLowerCase().replace('_', ' '));
    UUID[] array = stats.keySet().toArray(new UUID[0]);
    for(int position = 1; position <= 10; position++) {
      if(array.length - position < 0) {
        sender.sendMessage(formatMessage(statistic, "Empty", position, 0));
      } else {
        UUID current = array[array.length - position];
        String name = registry.getPlugin().getUserManager().getDatabase().getPlayerName(current);
        if(name == null) {
          name = "Unknown Player";
        }
        sender.sendMessage(formatMessage(statistic, name, position, stats.get(current)));
      }
    }
  }

  private String formatMessage(String statisticName, String playerName, int position, int value) {
    String message = registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_FORMAT");
    message = StringUtils.replace(message, "%position%", Integer.toString(position));
    message = StringUtils.replace(message, "%name%", playerName);
    message = StringUtils.replace(message, "%value%", Integer.toString(value));
    message = StringUtils.replace(message, "%statistic%", statisticName);
    return message;
  }

}
