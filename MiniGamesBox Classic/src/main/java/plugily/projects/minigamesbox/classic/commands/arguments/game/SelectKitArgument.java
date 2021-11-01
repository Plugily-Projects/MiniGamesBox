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
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class SelectKitArgument {

  public SelectKitArgument(ArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("selectkit", registry.getPlugin().getPluginNamePrefixLong() + ".command.selectkit", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(registry.getPlugin().getBukkitHelper().checkIsInGameInstance(player)) {
          registry.getPlugin().getKitMenuHandler().createMenu(player);
        }
      }
    });
  }

}
