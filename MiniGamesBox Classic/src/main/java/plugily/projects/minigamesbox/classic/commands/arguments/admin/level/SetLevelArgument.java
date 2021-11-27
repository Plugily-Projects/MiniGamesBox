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

package plugily.projects.minigamesbox.classic.commands.arguments.admin.level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.user.User;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class SetLevelArgument {

  public SetLevelArgument(ArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("setlevel", registry.getPlugin().getPluginNamePrefixLong() + ".admin.setlevel",
        CommandArgument.ExecutorType.BOTH, new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " setlevel &6<amount> &c[player]", "/" + registry.getPlugin().getCommandAdminPrefix() + " setlevel <amount>",
        "&7Set level to yourself or target player\n&7Can be used from console too\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.setlevel (for yourself)\n"
            + "&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.setlevel.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of levels to set!");
          return;
        }
        Player target;
        if(args.length == 2 && sender instanceof Player) {
          target = (Player) sender;
        } else {
          target = Bukkit.getPlayerExact(args[2]);
        }

        if(target == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("COMMANDS_PLAYER_NOT_FOUND"));
          return;
        }

        java.util.Optional<Integer> opt = NumberUtils.parseInt(args[1]);

        if(opt.isPresent()) {
          User user = registry.getPlugin().getUserManager().getUser(target);
          user.setStat(registry.getPlugin().getStatsStorage().getStatisticType("LEVEL"), opt.get());
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_ADMIN_ADDED_LEVEL"));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_WRONG_USAGE")
              .replace("%correct%", "/" + registry.getPlugin().getCommandAdminPrefix() + " setlevel <amount> [player]"));
        }
      }
    });
  }

}
