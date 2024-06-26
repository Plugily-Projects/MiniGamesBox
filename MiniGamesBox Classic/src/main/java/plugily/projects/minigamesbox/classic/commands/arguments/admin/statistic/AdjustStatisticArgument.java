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

package plugily.projects.minigamesbox.classic.commands.arguments.admin.statistic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.number.NumberUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 05.02.2022
 */
public class AdjustStatisticArgument {

  public AdjustStatisticArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("statistic", registry.getPlugin().getPluginNamePrefixLong() + ".admin.statistic",
        CommandArgument.ExecutorType.BOTH, new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " statistic <adjust/set> <statistic> <amount> &c[player]", "/" + registry.getPlugin().getCommandAdminPrefix() + " statistic &6<adjust/set> <statistic> <amount>",
        "&7Add statistics to yourself or target player\n&7Can be used from console too\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.statistic (for yourself)\n"
            + "&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.statistic.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder(ChatColor.RED + "Please type parameter adjust/set!").prefix().send(sender);
          return;
        }
        boolean set = args[1].equalsIgnoreCase("set");
        if(args.length == 2) {
          new MessageBuilder(ChatColor.RED + "Please type statistic to interact with!").prefix().send(sender);
          return;
        }
        IStatisticType statisticType = registry.getPlugin().getStatsStorage().getStatisticType(args[2].toUpperCase());
        if(args.length == 3) {
          new MessageBuilder(ChatColor.RED + "Please type amount of statistic to interact with!").prefix().send(sender);
          return;
        }
        Player target;
        if(args.length == 4 && sender instanceof Player) {
          target = (Player) sender;
        } else {
          target = Bukkit.getPlayerExact(args[4]);
        }

        if(target == null) {
          new MessageBuilder("COMMANDS_PLAYER_NOT_FOUND").asKey().send(sender);
          return;
        }

        java.util.Optional<Integer> opt = NumberUtils.parseInt(args[3]);

        if(opt.isPresent()) {
          IUser user = registry.getPlugin().getUserManager().getUser(target);
          if(set) {
            user.setStatistic(statisticType, opt.get());
          } else {
            user.adjustStatistic(statisticType, opt.get());
          }
          new MessageBuilder("COMMANDS_ADMIN_ADJUST_STATISTIC").asKey().player(target).value(statisticType.getName()).integer(user.getStatistic(statisticType)).send(sender);
        } else {
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/" + registry.getPlugin().getCommandAdminPrefix() + " statistic &6<adjust/set> <statistic> <amount> &c[player]").send(sender);
        }
      }
    });
  }

}
