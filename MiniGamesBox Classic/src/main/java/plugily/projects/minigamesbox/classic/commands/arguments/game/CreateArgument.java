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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class CreateArgument {

  public CreateArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new LabeledCommandArgument("create", registry.getPlugin().getPluginNamePrefixLong() + ".admin.setup", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getPluginNamePrefix() + " create &6<arena>", "/" + registry.getPlugin().getPluginNamePrefix() + " create <arena>",
            "&7Create new arena\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.setup")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          return;
        }
        Player player = (Player) sender;
        for(PluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          if(arena.getId().equalsIgnoreCase(args[1])) {
            new MessageBuilder(ChatColor.DARK_RED + "Arena with that ID already exists!").prefix().send(player);
            new MessageBuilder(ChatColor.DARK_RED + "Usage: /vd create <ID>").prefix().send(player);
            return;
          }
        }
        if(ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[1])) {
          new MessageBuilder(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!").prefix().send(player);
        } else {
          registry.getPlugin().getSetupInventory((Player) sender, args[1]).createInstanceInConfig(args[1], player);
        }
      }
    });
  }

}
