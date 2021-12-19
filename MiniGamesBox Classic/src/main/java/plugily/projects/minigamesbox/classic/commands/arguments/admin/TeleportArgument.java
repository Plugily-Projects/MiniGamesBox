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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class TeleportArgument {

  public TeleportArgument(ArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("tp", registry.getPlugin().getPluginNamePrefixLong() + ".admin.teleport", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " tp &6<arena> <location type>", "/" + registry.getPlugin().getCommandAdminPrefix() + " tp <arena> <location>",
            "&7Teleport you to provided arena location\n&7Valid locations:\n&7• LOBBY - lobby location\n&7• START - starting location\n"
                + "&7• END - ending location\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.teleport")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_TYPE_ARENA_NAME"));
          return;
        }
        if(args.length == 2) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type location type: END, START, LOBBY");
          return;
        }
        PluginArena.GameLocation type;
        try {
          type = PluginArena.GameLocation.valueOf(args[2].toUpperCase());
        } catch(IllegalArgumentException e) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_INVALID_LOCATION_TELEPORT"));
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
    player.teleport(location);
    player.sendMessage(ChatColor.GRAY + "Teleported to " + gameLocation.toString() + " location from arena " + arena.getId());
  }
}
