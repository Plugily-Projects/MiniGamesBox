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

package plugily.projects.minigamesbox.classic.commands.completion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class TabCompletion implements TabCompleter {

  private final List<CompletableArgument> registeredCompletions = new ArrayList<>();
  private final PluginArgumentsRegistry registry;

  public TabCompletion(PluginArgumentsRegistry registry) {
    this.registry = registry;
  }

  public void registerCompletion(CompletableArgument completion) {
    registeredCompletions.add(completion);
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
    List<String> commands = new ArrayList<>();

    if(cmd.getName().equalsIgnoreCase(registry.getPlugin().getCommandAdminPrefixLong())) {
      if(args.length == 1) {
        commands.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
            .collect(Collectors.toList()));
      } else if(args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("tp"))) {
        commands.addAll(registry.getPlugin().getArenaRegistry().getArenas().stream().map(IPluginArena::getId).collect(Collectors.toList()));
      }
    }

    if(cmd.getName().equalsIgnoreCase(registry.getPlugin().getPluginNamePrefixLong())) {
      if(args.length == 2 && args[0].equalsIgnoreCase("join")) {
        commands.addAll(registry.getPlugin().getArenaRegistry().getArenas().stream().map(IPluginArena::getId).collect(Collectors.toList()));
      } else if(args.length == 1) {
        commands.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
            .collect(Collectors.toList()));
      }
    }

    if(commands.isEmpty()) {
      for(CompletableArgument completion : registeredCompletions) {
        if(!cmd.getName().equalsIgnoreCase(completion.getMainCommand()) || !completion.getArgument().equalsIgnoreCase(args[0])) {
          continue;
        }
        return completion.getCompletions();
      }
      return null;
    }

    return commands;
  }
}
