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

package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class SetupArgument {

  public SetupArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("setup", registry.getPlugin().getCommandAdminPrefixLong() + ".admin.setup", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " setup &c[create/edit] &c[arena]", "/" + registry.getPlugin().getCommandAdminPrefixLong() + " setup [create/edit] [arena]",
            "&7Used for all setup configuration \n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.setup")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length == 1) {
          new SetupInventory(registry.getPlugin(), player).open();
          return;
        }

        if(args.length == 2) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/" + registry.getPlugin().getCommandAdminPrefix() + " setup &c[create/edit] &c[arena]").send(sender);
          return;
        }
        switch(args[1].toLowerCase()) {
          case "create":
            for(IPluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
              if(arena.getId().equalsIgnoreCase(args[2])) {
                new MessageBuilder(ChatColor.DARK_RED + "Arena with that ID already exists!").prefix().send(player);
                new MessageBuilder(ChatColor.DARK_RED + "Usage: /" + registry.getPlugin().getCommandAdminPrefix() + " setup edit <ID>").prefix().send(player);
                return;
              }
            }
            if(ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[2])) {
              new MessageBuilder(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!").prefix().send(player);
            } else {
              registry.getPlugin().getSetupInventory((Player) sender, args[2]).createInstanceInConfig(args[2], player);
            }
            break;
          case "edit":
            IPluginArena arena = registry.getPlugin().getArenaRegistry().getArena(args[2]);
            if(arena == null) {
              new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
              return;
            }
            registry.getPlugin().getSetupInventory((Player) sender, arena.getId()).open();
            break;
          default:
            new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/" + registry.getPlugin().getCommandAdminPrefix() + " setup &c[create/edit] &c[arena]").send(sender);
        }
      }
    });
  }

}
