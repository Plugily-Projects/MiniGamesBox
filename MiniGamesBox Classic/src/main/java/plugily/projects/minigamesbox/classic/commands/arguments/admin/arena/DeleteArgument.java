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
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class DeleteArgument {

  private final Set<CommandSender> confirmations = new HashSet<>();

  public DeleteArgument(PluginArgumentsRegistry registry) {

    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("delete", registry.getPlugin().getPluginNamePrefixLong() + ".admin.delete", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " delete &6<arena>", "/" + registry.getPlugin().getCommandAdminPrefix() + " delete <arena>",
            "&7Deletes specified arena\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.delete")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          return;
        }
        IPluginArena arena = registry.getPlugin().getArenaRegistry().getArena(args[1]);
        if(arena == null) {
          new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
          return;
        }
        if(!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20L * 10);
          new MessageBuilder("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!").prefix().send(sender);
          return;
        }
        confirmations.remove(sender);
        registry.getPlugin().getArenaRegistry().unregisterArena(arena);
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        config.set("instances." + args[1], null);
        ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
        new MessageBuilder("COMMANDS_REMOVED_GAME_INSTANCE").asKey().send(sender);
      }
    });
  }

}
