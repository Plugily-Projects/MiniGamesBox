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
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class SetupArgument {

  private final PluginArgumentsRegistry registry;

  public SetupArgument(PluginArgumentsRegistry registry) {
    this.registry = registry;
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("setup", registry.getPlugin().getCommandAdminPrefixLong() + ".admin.setup", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " setup &c[create/edit] &c[arena]", registry.getPlugin().getCommandAdminPrefixLong() + " setup [create/edit] [arena]",
            "&7Used for all setup configuration \n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.setup")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length == 1) {
          registry.getPlugin().openSetupInventory(null, player);
          return;
        }

        if(args.length == 2) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("COMMANDS_TYPE_ARENA_NAME"));
          return;
        }
        switch(args[1].toLowerCase()) {
          case "create":
            for(PluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
              if(arena.getId().equalsIgnoreCase(args[2])) {
                player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
                player.sendMessage(ChatColor.DARK_RED + "Usage: /" + registry.getPlugin().getCommandAdminPrefix() + " setup edit <ID>");
                return;
              }
            }
            if(ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[2])) {
              player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
            } else {
              registry.getPlugin().getSetupUtilities().createInstanceInConfig(args[2], player.getWorld().getName(), player);
            }
            break;
          case "edit":
            PluginArena arena = registry.getPlugin().getArenaRegistry().getArena(args[2]);
            if(arena == null) {
              sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_NO_ARENA_LIKE_THAT"));
              return;
            }
            registry.getPlugin().openSetupInventory(arena, (Player) sender, SetupUtilities.InventoryStage.PAGED_GUI);
            break;
          default:
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_WRONG_USAGE", "/" + registry.getPlugin().getCommandAdminPrefix() + " setup &c[create/edit] &c[arena]"));
        }
      }
    });
  }

}
