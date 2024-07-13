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

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameJoinAttemptEvent;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameLeaveAttemptEvent;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameStopEvent;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.states.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.handlers.party.GameParty;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArenaManager {

  private final PluginMain plugin;

  public PluginArenaManager(PluginMain plugin) {
    this.plugin = plugin;
  }

  /**
   * Attempts player to join arena.
   * Calls PlugilyGameJoinAttemptEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param player player to join
   * @param arena  arena to join
   * @see PlugilyGameJoinAttemptEvent
   */
  public void joinAttempt(@NotNull Player player, @NotNull IPluginArena arena) {
    plugin.getDebugger().debug("[{0}] Initial join attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();
    if(!canJoinArenaAndMessage(player, arena) || !checkFullGamePermission(player, arena)) {
      return;
    }
    plugin.getDebugger().debug("[{0}] Checked join attempt for {1}", arena.getId(), player.getName());

    if(!joinAsParty(player, arena)) {
      return;
    }

    arena.getPlayers().add(player);

    if(arena.getArenaState() == IArenaState.IN_GAME || ArenaState.isStartingStage(arena) && arena.getTimer() <= 3 || arena.getArenaState() == IArenaState.ENDING) {
      if(!plugin.getConfigPreferences().getOption("SPECTATORS")) {
        new MessageBuilder("IN_GAME_SPECTATOR_BLOCKED").asKey().player(player).arena(arena).sendPlayer();
        return;
      }
      PluginArenaUtils.preparePlayerForGame(arena, player, arena.getSpectatorLocation(), true).thenAccept(act -> {
        new MessageBuilder("IN_GAME_SPECTATOR_YOU_ARE_SPECTATOR").asKey().player(player).arena(arena).sendPlayer();
        PluginArenaUtils.hidePlayer(player, arena);
        for (Player spectator : arena.getPlayers()) {
          if (plugin.getUserManager().getUser(spectator).isSpectator()) {
            VersionUtils.hidePlayer(plugin, player, spectator);
          } else {
            VersionUtils.showPlayer(plugin, player, spectator);
          }
        }
        additionalSpectatorSettings(player, arena);
        plugin.getDebugger().debug("[{0}] Final join attempt as spectator for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
      });
      return;
    }

    PluginArenaUtils.preparePlayerForGame(arena, player, arena.getLobbyLocation(), false).thenAccept(act -> {
      new MessageBuilder(MessageBuilder.ActionType.JOIN).arena(arena).player(player).sendArena();
      new TitleBuilder("IN_GAME_JOIN_TITLE").asKey().arena(arena).player(player).sendPlayer();

      if(plugin.getConfigPreferences().getOption("KITS")) {
        plugin.getUserManager().getUser(player).setKit(plugin.getKitRegistry().getDefaultKit());
      }
      plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.LOBBY);
      if(arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS) {
        plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.WAITING_FOR_PLAYERS);
      } else if(ArenaState.isStartingStage(arena)) {
        plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.ENOUGH_PLAYERS_TO_START);
      }

      for(Player arenaPlayer : arena.getPlayers()) {
        PluginArenaUtils.showPlayer(arenaPlayer, arena);
      }

      plugin.getSignManager().updateSigns();
      plugin.getDebugger().debug("[{0}] Final join attempt as player for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
    });
  }

  private boolean joinAsParty(@NotNull Player player, @NotNull IPluginArena arena) {
    //check if player is in party and send party members to the game
    GameParty party = plugin.getPartyHandler().getParty(player);

    if(party != null && player.getUniqueId().equals(party.getLeader().getUniqueId())) {
      if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
        for(Player partyPlayer : party.getPlayers()) {
          if(player.getUniqueId().equals(partyPlayer.getUniqueId())) {
            continue;
          }
          IPluginArena partyPlayerGame = plugin.getArenaRegistry().getArena(partyPlayer);

          if(partyPlayerGame != null) {
            if(partyPlayerGame.getArenaState() == IArenaState.IN_GAME) {
              continue;
            }
            leaveAttempt(partyPlayer, partyPlayerGame);
            plugin.getDebugger().debug("[Party] Removed party member " + partyPlayer.getName() + " from other not ingame arena " + player.getName());
          }
          new MessageBuilder("IN_GAME_JOIN_AS_PARTY_MEMBER").asKey().arena(arena).player(partyPlayer).sendPlayer();
          joinAttempt(partyPlayer, arena);
          additionalPartyJoin(player, arena, party.getLeader());
          plugin.getDebugger().debug("[Party] Added party member " + partyPlayer.getName() + " to arena of " + player.getName());
        }
      } else {
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_NOT_ENOUGH_SPACE_FOR_PARTY").asKey().arena(arena).player(player).sendPlayer();
        plugin.getDebugger().debug("[Party] Not enough space for party of " + player.getName());
        return false;
      }
    }
    plugin.getDebugger().debug("[Party] Party check done for " + player.getName());
    return true;
  }

  public void additionalPartyJoin(Player player, IPluginArena arena, Player partyLeader) {

  }

  public void additionalSpectatorSettings(Player player, IPluginArena arena) {

  }

  private boolean checkFullGamePermission(Player player, IPluginArena arena) {
    if(arena.getPlayers().size() + 1 <= arena.getMaximumPlayers()) {
      return true;
    }
    if(!player.hasPermission(plugin.getPluginNamePrefixLong() +".fullgames")) {
      new MessageBuilder("IN_GAME_JOIN_FULL_GAME").asKey().player(player).arena(arena).sendPlayer();
      return false;
    }
    for(Player arenaPlayer : arena.getPlayers()) {
      if(arenaPlayer.hasPermission(plugin.getPluginNamePrefixLong() +".fullgames")) {
        continue;
      }
      if(ArenaState.isLobbyStage(arena)) {
        leaveAttempt(arenaPlayer, arena);
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_YOU_WERE_KICKED_FOR_PREMIUM").asKey().player(player).arena(arena).sendPlayer();
        new MessageBuilder("IN_GAME_MESSAGES_LOBBY_KICKED_FOR_PREMIUM").asKey().player(arenaPlayer).arena(arena).sendArena();
      }
      return true;
    }
    new MessageBuilder("IN_GAME_JOIN_NO_SLOTS_FOR_PREMIUM").asKey().player(player).arena(arena).sendPlayer();
    return false;
  }

  private boolean canJoinArenaAndMessage(Player player, IPluginArena arena) {
    if(!arena.isReady()) {
      new MessageBuilder("IN_GAME_JOIN_ARENA_NOT_CONFIGURED").asKey().player(player).arena(arena).sendPlayer();
      return false;
    }

    PlugilyGameJoinAttemptEvent event = new PlugilyGameJoinAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(event);
    if(event.isCancelled()) {
      new MessageBuilder("IN_GAME_JOIN_CANCEL_API").asKey().player(player).arena(arena).sendPlayer();
      return false;
    }
    String perm = plugin.getPluginNamePrefixLong() + ".join.<arena>";
    if(!(player.hasPermission(perm.replace("<arena>", "*")) || player.hasPermission(perm.replace("<arena>", arena.getId())))) {
      MessageBuilder denyMessage = new MessageBuilder("IN_GAME_JOIN_NO_PERMISSION").asKey().player(player).value(perm.replace("<arena>", arena.getId()));
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        ComplementAccessor.getComplement().kickPlayer(player, denyMessage.build());
      } else {
        denyMessage.sendPlayer();
      }
      return false;
    }

    if(plugin.getArenaRegistry().isInArena(player)) {
      new MessageBuilder("IN_GAME_JOIN_ALREADY_PLAYING").asKey().arena(arena).player(player).sendPlayer();
      return false;
    }
    if(arena.getArenaState() == IArenaState.RESTARTING) {
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        ComplementAccessor.getComplement().kickPlayer(player, new MessageBuilder(arena.getArenaState().getFormattedName() + "...").prefix().build());
        return false;
      }
      new MessageBuilder(arena.getArenaState().getFormattedName() + "...").prefix().player(player).sendPlayer();
      return false;
    }
    return true;
  }

  /**
   * Attempts player to leave arena.
   * Calls PlugilyGameLeaveAttemptEvent event.
   *
   * @param player player to leave
   * @param arena  arena to leave
   * @see PlugilyGameLeaveAttemptEvent
   */
  public void leaveAttempt(@NotNull Player player, @NotNull IPluginArena arena) {
    plugin.getDebugger().debug("[{0}] Initial leave attempt of {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new PlugilyGameLeaveAttemptEvent(player, arena));

    IUser user = plugin.getUserManager().getUser(player);

    if(!user.isSpectator()) {
      if(arena.getArenaState() != IArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != IArenaState.STARTING && arena.getPlayers().isEmpty()) {
        stopGame(true, arena);
        plugin.getDebugger().debug(Level.INFO, "[{0}] Game stopped due to lack of players", arena.getId());
      }
      new MessageBuilder(MessageBuilder.ActionType.LEAVE).arena(arena).player(player).sendArena();
    }
    plugin.getUserManager().saveAllStatistic(user);
    PluginArenaUtils.resetPlayerAfterGame(arena, player);
    plugin.getSignManager().updateSigns();
    plugin.getDebugger().debug("[{0}] Final leave attempt for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  /**
   * Stops current arena. Calls PlugilyGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @param arena     which arena should stop
   * @see PlugilyGameStopEvent
   */
  public void stopGame(boolean quickStop, @NotNull IPluginArena arena) {
    plugin.getDebugger().debug("[{0}] Game stop event start", arena.getId());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new PlugilyGameStopEvent(arena));
    for(Player player : arena.getPlayers()) {
      if (quickStop) {
        new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLAYERS").asKey().arena(arena).sendArena();
      } else {
        spawnFireworks(arena, player);
        for(String msg : plugin.getLanguageManager().getLanguageList("In-Game.Messages.Game-End.Summary")) {
          MiscUtils.sendCenteredMessage(player, new MessageBuilder(msg).player(player).arena(arena).build());
        }
      }
    }
    if(quickStop) {
      arena.setTimer(0, true);
      arena.setArenaState(IArenaState.RESTARTING, true);
    } else {
      arena.setTimer(plugin.getConfig().getInt("Time-Manager.Ending", 10), true);
      arena.setArenaState(IArenaState.ENDING, true);
    }

    for(Player players : arena.getPlayers()) {
      plugin.getSpecialItemManager().removeSpecialItemsOfStage(players, SpecialItem.DisplayStage.IN_GAME);
      plugin.getSpecialItemManager().addSpecialItemsOfStage(players, SpecialItem.DisplayStage.ENDING);
    }
    plugin.getDebugger().debug("[{0}] Game stop event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

  private void spawnFireworks(IPluginArena arena, Player player) {
    if(!plugin.getConfigPreferences().getOption("FIREWORK")) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      @Override
      public void run() {
        if(i == 4 || arena.getArenaState() == IArenaState.RESTARTING || !arena.getPlayers().contains(player)) {
          cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(player.getLocation());
        i++;
      }
    }.runTaskTimer(plugin, 30, 30);
  }

}
