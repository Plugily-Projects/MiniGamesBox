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

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameJoinAttemptEvent;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameLeaveAttemptEvent;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameStopEvent;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.ChatManager;
import plugily.projects.minigamesbox.classic.handlers.party.GameParty;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.List;

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
   * @see plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameJoinAttemptEvent
   */
  public void joinAttempt(@NotNull Player player, @NotNull PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Initial join attempt for {1}", arena.getId(), player.getName());
    if(!canJoinArenaAndMessage(player, arena) || !checkFullGamePermission(player, arena)) {
      return;
    }
    plugin.getDebugger().debug("[{0}] Checked join attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();
    if(plugin.getArenaRegistry().isInArena(player)) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_ALREADY_PLAYING"));
      return;
    }

    //check if player is in party and send party members to the game
    GameParty party = plugin.getPartyHandler().getParty(player);

    if(party != null && player.getUniqueId().equals(party.getLeader().getUniqueId())) {
      if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
        for(Player partyPlayer : party.getPlayers()) {
          if(player.getUniqueId().equals(partyPlayer.getUniqueId())) {
            continue;
          }
          PluginArena partyPlayerGame = plugin.getArenaRegistry().getArena(partyPlayer);

          if(partyPlayerGame != null) {
            if(partyPlayerGame.getArenaState() == ArenaState.IN_GAME) {
              continue;
            }
            leaveAttempt(partyPlayer, partyPlayerGame);
          }
          partyPlayer.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_JOIN_AS_PARTY_MEMBER"), partyPlayer));
          joinAttempt(partyPlayer, arena);
        }
      } else {
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_LOBBY_NOT_ENOUGH_SPACE_FOR_PARTY"), player));
        return;
      }
    }

    arena.getPlayers().add(player);
    User user = plugin.getUserManager().getUser(player);
    arena.getScoreboardManager().createScoreboard(user);

    if((arena.getArenaState() == ArenaState.IN_GAME || ((arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || (arena.getArenaState() == ArenaState.FULL_GAME && arena.getTimer() <= 3)) || arena.getArenaState() == ArenaState.ENDING)) {
      PluginArenaUtils.preparePlayerForGame(player, arena.getStartLocation(), true);
      player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_YOU_ARE_SPECTATOR"));
      PluginArenaUtils.hidePlayer(player, arena);
      for(Player spectator : arena.getPlayers()) {
        if(plugin.getUserManager().getUser(spectator).isSpectator()) {
          VersionUtils.hidePlayer(plugin, player, spectator);
        } else {
          VersionUtils.showPlayer(plugin, player, spectator);
        }
      }
      additionalSpectatorSettings(player, arena);
      plugin.getDebugger().debug("[{0}] Final join attempt as spectator for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
      return;
    }
    PluginArenaUtils.preparePlayerForGame(player, arena.getLobbyLocation(), false);

    arena.getBossbarManager().doBarAction(PluginArena.BarAction.ADD, player);

    plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.JOIN);

    user.setKit(plugin.getKitRegistry().getDefaultKit());
    plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.LOBBY);
    if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.WAITING_FOR_PLAYERS);
    } else if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.FULL_GAME) {
      plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.ENOUGH_PLAYERS_TO_START);
    }

    for(Player arenaPlayer : arena.getPlayers()) {
      PluginArenaUtils.showPlayer(arenaPlayer, arena);
    }
    plugin.getSignManager().updateSigns();
    plugin.getDebugger().debug("[{0}] Final join attempt as player for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  public void additionalSpectatorSettings(Player player, PluginArena arena) {

  }

  private boolean checkFullGamePermission(Player player, PluginArena arena) {
    if(arena.getPlayers().size() + 1 <= arena.getMaximumPlayers()) {
      return true;
    }
    if(!player.hasPermission(plugin.getPermissionsManager().getPermissionString("JOIN_FULL_GAME"))) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_FULL_GAME"));
      return false;
    }
    for(Player players : arena.getPlayers()) {
      if(players.hasPermission(plugin.getPermissionsManager().getPermissionString("JOIN_FULL_GAME"))) {
        continue;
      }
      if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.FULL_GAME || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
        leaveAttempt(players, arena);
        players.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_LOBBY_YOU_WERE_KICKED_FOR_PREMIUM"));
        plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_LOBBY_KICKED_FOR_PREMIUM"), players));
      }
      return true;
    }
    player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_NO_SLOTS_FOR_PREMIUM"));
    return false;
  }

  private boolean canJoinArenaAndMessage(Player player, PluginArena arena) {
    if(!arena.isReady()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_ARENA_NOT_CONFIGURED"));
      return false;
    }

    PlugilyGameJoinAttemptEvent event = new PlugilyGameJoinAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(event);
    if(event.isCancelled()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_CANCEL_API"));
      return false;
    }
    if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
      String perm = plugin.getPermissionsManager().getPermissionString("JOIN");
      if(!(player.hasPermission(perm.replace("<arena>", "*")) || player.hasPermission(perm.replace("<arena>", arena.getId())))) {
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_JOIN_NO_PERMISSION")
            .replace("%value%", perm.replace("<arena>", arena.getId())));
        return false;
      }
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
  public void leaveAttempt(@NotNull Player player, @NotNull PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Initial leave attempt of {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new PlugilyGameLeaveAttemptEvent(player, arena));

    User user = plugin.getUserManager().getUser(player);
    arena.getScoreboardManager().removeScoreboard(user);
    arena.getPlayers().remove(player);
    if(!user.isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.LEAVE);
    }
    user.setSpectator(false);
    user.setPermanentSpectator(false);

    arena.getBossbarManager().doBarAction(PluginArena.BarAction.REMOVE, player);
    if(arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != ArenaState.STARTING && arena.getPlayers().isEmpty()) {
      stopGame(true, arena);
    }
    PluginArenaUtils.resetPlayerAfterGame(player);
    arena.teleportToEndLocation(player);
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
  public void stopGame(boolean quickStop, @NotNull PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Game stop event start", arena.getId());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new PlugilyGameStopEvent(arena));
    for(Player player : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(player);
      if(!quickStop) {
        spawnFireworks(arena, player);
      }
      List<String> summaryMessages = plugin.getLanguageManager().getLanguageList("In-Game.Messages.Game-End.Summary");
      for(String msg : summaryMessages) {
        MiscUtils.sendCenteredMessage(player, plugin.getChatManager().formatMessage(arena, msg, user.getPlayer()));
      }
    }
    arena.setTimer(plugin.getConfig().getInt("Time-Manager.Ending", 10), true);
    arena.setArenaState(ArenaState.ENDING, true);
    for(Player players : arena.getPlayers()) {
      plugin.getSpecialItemManager().removeSpecialItemsOfStage(players, SpecialItem.DisplayStage.IN_GAME);
      plugin.getSpecialItemManager().addSpecialItemsOfStage(players, SpecialItem.DisplayStage.ENDING);
    }
    plugin.getDebugger().debug("[{0}] Game stop event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

  private void spawnFireworks(PluginArena arena, Player player) {
    if(!plugin.getConfigPreferences().getOption("FIREWORK")) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      @Override
      public void run() {
        if(i == 4 || arena.getArenaState() == ArenaState.RESTARTING || !arena.getPlayers().contains(player)) {
          cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(player.getLocation());
        i++;
      }
    }.runTaskTimer(plugin, 30, 30);
  }

}
