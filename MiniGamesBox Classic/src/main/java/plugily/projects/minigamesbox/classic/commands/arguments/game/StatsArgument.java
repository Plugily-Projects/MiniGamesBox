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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.user.User;

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
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_PLAYER_NOT_FOUND"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if(player == sender) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_TYPE_CHAT_HEADER"));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_TYPE_CHAT_HEADER_OTHER").replace("%player%", player.getName()));
        }
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_STATISTICS_GAMES_PLAYED") + user.getStat(registry.getPlugin().getStatsStorage().getStatisticType("GAMES_PLAYED")));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_STATISTICS_LEVEL") + user.getStat(registry.getPlugin().getStatsStorage().getStatisticType("LEVEL")));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_STATISTICS_EXP") + user.getStat(registry.getPlugin().getStatsStorage().getStatisticType("XP")));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_STATISTICS_NEXT_LEVEL_EXP")
            + Math.ceil(Math.pow(50.0 * user.getStat(registry.getPlugin().getStatsStorage().getStatisticType("LEVEL")), 1.5)));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("LEADERBOARD_TYPE_CHAT_FOOTER"));
      }
    });
  }

}
