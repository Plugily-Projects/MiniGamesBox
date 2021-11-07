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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaManager;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;

import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class LeaveArgument {

  public LeaveArgument(ArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command")) {
          Player player = (Player) sender;
          if(!registry.getPlugin().getBukkitHelper().checkIsInGameInstance(player)) {
            return;
          }
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TELEPORTED_TO_THE_LOBBY));
          Arena arena = registry.getPlugin().getArenaRegistry().getArena(player);

          if(arena == null) {
            return;
          }

          if(registry.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
            registry.getPlugin().getBungeeManager().connectToHub(player);
            registry.getPlugin().getDebugger().debug(Level.INFO, "{0} has left the arena {1}! Teleported to the Hub server.", player.getName(), arena.getId());
          } else {
            arena.teleportToEndLocation(player);
            ArenaManager.leaveAttempt(player, arena);
            registry.getPlugin().getDebugger().debug(Level.INFO, "{0} has left the arena {1}! Teleported to end location.", player.getName(), arena.getId());
          }
        }
      }
    });
  }
}