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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class LeaveArgument {

  public LeaveArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(registry.getPlugin().getConfigPreferences().getOption("BLOCKED_LEAVE_COMMAND")) {
          return;
        }
        Player player = (Player) sender;
        if(!registry.getPlugin().getBukkitHelper().checkIsInGameInstance(player)) {
          return;
        }
        new MessageBuilder("COMMANDS_TELEPORTED_TO_LOBBY").asKey().player(player).sendPlayer();
        IPluginArena arena = registry.getPlugin().getArenaRegistry().getArena(player);

        if(arena == null) {
          return;
        }

        if(registry.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
          registry.getPlugin().getBungeeManager().connectToHub(player);
          registry.getPlugin().getDebugger().debug(Level.INFO, "{0} has left the arena {1}! Teleported to the Hub server.", player.getName(), arena.getId());
        } else {
          registry.getPlugin().getArenaManager().leaveAttempt(player, arena);
          registry.getPlugin().getDebugger().debug(Level.INFO, "{0} has left the arena {1}! Teleported to end location.", player.getName(), arena.getId());
        }
      }
    });
  }
}
