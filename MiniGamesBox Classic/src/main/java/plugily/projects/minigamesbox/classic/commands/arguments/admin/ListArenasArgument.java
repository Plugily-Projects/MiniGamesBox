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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.TextComponentBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class ListArenasArgument {

  public ListArenasArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("list", registry.getPlugin().getPluginNamePrefixLong() + ".admin.list", CommandArgument.ExecutorType.BOTH,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " list", "/" + registry.getPlugin().getCommandAdminPrefix() + " list",
            "&7Shows list with all loaded arenas\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        new MessageBuilder("COMMANDS_ADMIN_LIST_HEADER").asKey().send(sender);

        if(registry.getPlugin().getArenaRegistry().getArenas().isEmpty()) {
          new MessageBuilder("COMMANDS_ADMIN_LIST_NO_ARENAS").asKey().send(sender);
          new MessageBuilder("&e&lTIP: &7You can get free maps with configs at our wiki! Just head to https://wiki.plugily.xyz/" + registry.getPlugin().getPluginNamePrefixLong().toLowerCase() + "/setup/maps").send(sender);
          return;
        }

        for(IPluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          String listMessage = new MessageBuilder("COMMANDS_ADMIN_LIST_FORMAT").asKey().arena(arena).build();
          boolean senderIsPlayer = sender instanceof Player;
          if(!senderIsPlayer) {
            sender.sendMessage(listMessage);
            return;
          }
          new TextComponentBuilder(listMessage).player((Player) sender)
              .setClickEvent(TextComponentBuilder.ClickAction.SUGGEST_COMMAND, "/" + registry.getPlugin().getPluginNamePrefixLong() + " join " + arena.getId())
              .setHoverEvent(TextComponentBuilder.HoverAction.SHOW_TEXT, "/" + registry.getPlugin().getPluginNamePrefixLong() + " join " + arena.getId())
              .sendPlayer();
        }
      }
    });
  }

}
