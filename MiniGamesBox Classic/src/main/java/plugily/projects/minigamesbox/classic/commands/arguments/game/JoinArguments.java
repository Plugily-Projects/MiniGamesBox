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
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.states.ArenaState;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class JoinArguments {

  public JoinArguments(PluginArgumentsRegistry registry) {
    //join argument
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          return;
        }
        if(!registry.getPlugin().getArenaRegistry().getArenas().isEmpty() && args[1].equalsIgnoreCase("maxplayers") && registry.getPlugin().getArenaRegistry().getArena("maxplayers") == null) {
          if(registry.getPlugin().getArenaRegistry().getArenaPlayersOnline() == 0) {
            registry.getPlugin().getArenaManager().joinAttempt((Player) sender, registry.getPlugin().getArenaRegistry().getArenas().get(registry.getPlugin().getRandom().nextInt(registry.getPlugin().getArenaRegistry().getArenas().size())));
            return;
          }

          Map<IPluginArena, Integer> arenas = new HashMap<>();
          List<IPluginArena> arenaList = registry.getPlugin().getArenaRegistry().getArenas();
          if(args.length > 2) {
            arenaList = registry.getSpecificFilteredArenas(arenaList, args[2]);
          }
          for(IPluginArena arena : arenaList) {
            if(!(ArenaState.isLobbyStage(arena)) || arena.getPlayers().size() >= arena.getMaximumPlayers())
              continue;
            arenas.put(arena, arena.getPlayers().size());
          }
          if(arenas.isEmpty()) {
            new MessageBuilder("COMMANDS_NO_FREE_ARENAS").asKey().send(sender);
            return;
          }
          arenas.entrySet()
              .stream()
              .max(Map.Entry.comparingByValue())
              .map(Map.Entry::getKey)
              .ifPresent(arena -> registry.getPlugin().getArenaManager().joinAttempt((Player) sender, arena));
          return;
        }
        for(IPluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          if(!ArenaState.isLobbyStage(arena) || arena.getPlayers().size() >= arena.getMaximumPlayers()) {
            continue;
          }
          if(args[1].equalsIgnoreCase(arena.getId())) {
            registry.getPlugin().getArenaManager().joinAttempt((Player) sender, arena);
            return;
          }
        }
        new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
      }
    });

    //random join argument, disable for bungee
    if(!registry.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
      registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {

        @Override
        public void execute(CommandSender sender, String[] args) {
          //check starting arenas -> random
          List<IPluginArena> arenas = registry.getPlugin().getArenaRegistry().getArenas().stream().filter(arena -> arena.getArenaState() == IArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(args.length > 1) {
            arenas = registry.getSpecificFilteredArenas(arenas, args[1]);
          }
          if(!arenas.isEmpty()) {
            registry.getPlugin().getArenaManager().joinAttempt((Player) sender, arenas.get(registry.getPlugin().getRandom().nextInt(arenas.size())));
            return;
          }
          //check waiting arenas -> random
          arenas = registry.getPlugin().getArenaRegistry().getArenas().stream().filter(arena -> (arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == IArenaState.STARTING)
              && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(args.length > 1) {
            arenas = registry.getSpecificFilteredArenas(arenas, args[1]);
          }
          if(!arenas.isEmpty()) {
            registry.getPlugin().getArenaManager().joinAttempt((Player) sender, arenas.get(registry.getPlugin().getRandom().nextInt(arenas.size())));
            return;
          }
          new MessageBuilder("COMMANDS_NO_FREE_ARENAS").asKey().send(sender);
        }
      });
    }
  }
}
