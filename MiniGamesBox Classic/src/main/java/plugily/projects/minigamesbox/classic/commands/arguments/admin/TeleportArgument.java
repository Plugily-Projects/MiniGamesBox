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

package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import io.papermc.lib.PaperLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class TeleportArgument {
  //ToDo world teleports
  public TeleportArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("tp", registry.getPlugin().getPluginNamePrefixLong() + ".admin.teleport", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " tp &6<arena> <location type>", "/" + registry.getPlugin().getCommandAdminPrefix() + " tp <arena> <location>",
            "&7Teleport you to provided arena location\n&7Valid locations:\n&7• LOBBY - lobby location\n&7• START - starting location\n"
                + "&7• END - ending location\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.teleport")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          return;
        }
        if(args.length == 2) {
          new MessageBuilder(ChatColor.RED + "Please type location type: END, START, LOBBY").prefix().send(sender);
          return;
        }
        PluginArena.GameLocation type;
        try {
          type = PluginArena.GameLocation.valueOf(args[2].toUpperCase());
        } catch(IllegalArgumentException e) {
          new MessageBuilder("COMMANDS_INVALID_LOCATION_TELEPORT").asKey().send(sender);
          return;
        }
        for(PluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          if(arena.getId().equalsIgnoreCase(args[1])) {
            teleport((Player) sender, arena, type);
            break;
          }
        }
      }
    });
  }

  private void teleport(Player player, PluginArena arena, PluginArena.GameLocation gameLocation) {
    Location location = arena.getLocation(gameLocation);
    if(location == null) {
      player.sendMessage(ChatColor.RED + gameLocation.toString() + " location isn't set for this arena!");
      return;
    }
    PaperLib.teleportAsync(player, location);
    player.sendMessage(ChatColor.GRAY + "Teleported to " + gameLocation.toString() + " location from arena " + arena.getId());
  }
}
