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
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class JoinArguments {

  private final Random random = new Random();

  public JoinArguments(ArgumentsRegistry registry) {
    //join argument
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TYPE_ARENA_NAME));
          return;
        }
        if(!registry.getPlugin().getArenaRegistry().getArenas().isEmpty() && args[1].equalsIgnoreCase("maxplayers") && registry.getPlugin().getArenaRegistry().getArena("maxplayers") == null) {
          if(registry.getPlugin().getArenaRegistry().getArenaPlayersOnline() == 0) {
            ArenaManager.joinAttempt((Player) sender, registry.getPlugin().getArenaRegistry().getArenas().get(random.nextInt(registry.getPlugin().getArenaRegistry().getArenas().size())));
            return;
          }

          Map<Arena, Integer> arenas = new HashMap<>();
          for(Arena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
            arenas.put(arena, arena.getPlayers().size());
          }
          arenas.entrySet()
              .stream()
              .max(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .map(Map.Entry::getKey)
              .ifPresent(arena -> ArenaManager.joinAttempt((Player) sender, arena));
          return;
        }
        for(Arena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          if(args[1].equalsIgnoreCase(arena.getId())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_ARENA_LIKE_THAT));
      }
    });

    //random join argument, disable for bungee
    if(!registry.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
      registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {

        @Override
        public void execute(CommandSender sender, String[] args) {
          //check starting arenas -> random
          List<Arena> arenas = registry.getPlugin().getArenaRegistry().getArenas().stream().filter(arena -> arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(!arenas.isEmpty()) {
            ArenaManager.joinAttempt((Player) sender, arenas.get(random.nextInt(arenas.size())));
            return;
          }
          //check waiting arenas -> random
          arenas = registry.getPlugin().getArenaRegistry().getArenas().stream().filter(arena -> (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
              && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(!arenas.isEmpty()) {
            ArenaManager.joinAttempt((Player) sender, arenas.get(random.nextInt(arenas.size())));
            return;
          }
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_FREE_ARENAS));
        }
      });
    }
  }
}
