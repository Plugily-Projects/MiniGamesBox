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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
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
public class ReloadArgument {

  private final Set<CommandSender> confirmations = new HashSet<>();

  public ReloadArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("reload", registry.getPlugin().getPluginNamePrefixLong() + ".admin.reload", CommandArgument.ExecutorType.BOTH,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " reload", "/" + registry.getPlugin().getCommandAdminPrefix() + " reload", "&7Reload all game arenas and configuration files\n&7&lArenas will be stopped!\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20L * 10);
          new MessageBuilder("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!").prefix().send(sender);
          return;
        }
        confirmations.remove(sender);


        for(IPluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          registry.getPlugin().getArenaManager().stopGame(true, arena);
        }

        int time = registry.getPlugin().getConfig().getInt("Time-Manager.Restarting", 5) + 5;

        new MessageBuilder("&cStopping arenas... Waiting for completion of restarting stage! (ETA: " + time + " s)").prefix().send(sender);

        Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> {
          registry.getPlugin().reloadConfig();
          registry.getPlugin().getLanguageManager().reloadLanguage();
          registry.getPlugin().getArenaRegistry().registerArenas();
          new MessageBuilder("COMMANDS_ADMIN_RELOAD_SUCCESS").asKey().send(sender);
        }, time);
      }
    });
  }

}
