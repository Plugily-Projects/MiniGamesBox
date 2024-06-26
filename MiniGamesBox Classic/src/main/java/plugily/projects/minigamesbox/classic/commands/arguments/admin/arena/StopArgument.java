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

package plugily.projects.minigamesbox.classic.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
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
public class StopArgument {

  public StopArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("stop", registry.getPlugin().getPluginNamePrefixLong() + ".admin.stop", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " stop", "/" + registry.getPlugin().getCommandAdminPrefix() + " stop",
            "&7Stops the arena you're in\n&7&lYou must be in target arena!\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.stop")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(registry.getPlugin().getBukkitHelper().checkIsInGameInstance((Player) sender)) {
          IPluginArena arena = registry.getPlugin().getArenaRegistry().getArena((Player) sender);
          if(arena == null) {
            return;
          }
          if(arena.getArenaState() != IArenaState.ENDING) {
            registry.getPlugin().getArenaManager().stopGame(true, arena);
            new MessageBuilder("COMMANDS_COMMAND_EXECUTED").asKey().send(sender);
          }
        }
      }
    });
  }

}
