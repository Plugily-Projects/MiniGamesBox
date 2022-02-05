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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class SpyChatArgument {

  private final Set<Player> spyChatters = new HashSet<>();

  public SpyChatArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("spychat", registry.getPlugin().getPluginNamePrefixLong() + ".admin.spychat", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " spychat", "/" + registry.getPlugin().getCommandAdminPrefix() + " spychat", "&7Toggles spy chat for all available arenas\n"
            + "&7You will see all messages from these games\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.spychat")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(!disableSpyChat(player)) {
          spyChatters.add(player);
        }
        new MessageBuilder("COMMANDS_ADMIN_SPYCHAT_TOGGLE").asKey().value(String.valueOf(isSpyChatEnabled(player))).prefix().send(sender);
      }
    });
  }

  public boolean isSpyChatEnabled(Player player) {
    return spyChatters.contains(player);
  }

  public boolean disableSpyChat(Player player) {
    return spyChatters.remove(player);
  }
}
