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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.states.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.InventorySerializer;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.concurrent.CompletableFuture;

/**
 * @author Tigerpanzer_02
 * <p>Created at 01.11.2021
 */
public class PluginArenaUtils {

  private static PluginMain plugin;

  public PluginArenaUtils() {
  }

  public static void init(PluginMain plugin) {
    PluginArenaUtils.plugin = plugin;
  }

  public static void hidePlayer(Player p, IPluginArena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.hidePlayer(plugin, player, p);
    }
  }

  public static void showPlayer(Player p, IPluginArena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.showPlayer(plugin, player, p);
    }
  }

  public static void hidePlayersOutsideTheGame(Player player, PluginArena arena) {
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      if(arena.getPlayers().contains(players)) {
        continue;
      }
      VersionUtils.hidePlayer(plugin, player, players);
      VersionUtils.hidePlayer(plugin, players, player);
    }
  }
  
  public static CompletableFuture<Void> preparePlayerForGame(
      IPluginArena arena, Player player, Location location, boolean spectator) {
    return VersionUtils.teleport(player, location).thenAccept(bo -> {
      IUser user = plugin.getUserManager().getUser(player);
      if (plugin.getConfigPreferences().getOption("INVENTORY_MANAGER")) {
        InventorySerializer.saveInventoryToFile(plugin, player);
      }
      player.getInventory().clear();
      player
              .getActivePotionEffects()
              .forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
      VersionUtils.setMaxHealth(player, VersionUtils.getMaxHealth(player));
      player.setHealth(VersionUtils.getMaxHealth(player));
      player.setFoodLevel(20);
      player.setGameMode(GameMode.SURVIVAL);
      player
              .getInventory()
              .setArmorContents(
                      new ItemStack[]{
                              new ItemStack(Material.AIR),
                              new ItemStack(Material.AIR),
                              new ItemStack(Material.AIR),
                              new ItemStack(Material.AIR)
                      });
      player.setExp(1);
      player.setLevel(0);
      player.setWalkSpeed(0.2f);
      player.setFlySpeed(0.1f);

      if (spectator) {
        player.setAllowFlight(true);
        player.setFlying(true);
        user.setSpectator(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        plugin
                .getSpecialItemManager()
                .addSpecialItemsOfStage(player, SpecialItem.DisplayStage.SPECTATOR);
      } else {
        player.setAllowFlight(false);
        player.setFlying(false);
        user.setSpectator(false);
      }
      player.updateInventory();
      arena.getBossbarManager().doBarAction(IPluginArena.IBarAction.ADD, player);
      arena.getScoreboardManager().createScoreboard(user);
    });
  }

  public static void resetPlayerAfterGame(IPluginArena arena, Player player) {
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      VersionUtils.showPlayer(plugin, player, players);
      if(!plugin.getArenaRegistry().isInArena(players)) {
        VersionUtils.showPlayer(plugin, players, player);
      }
    }
    IUser user = plugin.getUserManager().getUser(player);
    user.setSpectator(false);
    user.setPermanentSpectator(false);

    player.closeInventory();
    player.setGameMode(GameMode.SURVIVAL);
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    VersionUtils.setMaxHealth(player, 20);
    player.setHealth(VersionUtils.getMaxHealth(player));
    player.setFireTicks(0);
    player.setFoodLevel(20);
    player.setFlySpeed(0.1f);
    player.setWalkSpeed(0.2f);
    player.setExp(0);
    player.setLevel(0);

    VersionUtils.setCollidable(player, true);
    VersionUtils.setGlowing(player, false);

    arena.getScoreboardManager().removeScoreboard(user);
    arena.getBossbarManager().doBarAction(IPluginArena.IBarAction.REMOVE, player);
    arena.teleportToEndLocation(player);
    arena.getPlayers().remove(player);

    plugin.getActionBarManager().clearActionBarsFromPlayer(player);

    if(plugin.getConfigPreferences().getOption("INVENTORY_MANAGER")) {
      InventorySerializer.loadInventory(plugin, player);
    }
  }

  public static void arenaForceStart(Player player, int timer) {
    if(!plugin.getBukkitHelper().hasPermission(player, plugin.getPluginNamePrefixLong() + ".admin.forcestart")) {
      new MessageBuilder("COMMANDS_NO_PERMISSION").asKey().player(player).sendPlayer();
      return;
    }

    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      new MessageBuilder("COMMANDS_NOT_PLAYING").asKey().player(player).sendPlayer();
      return;
    }
    if(!ArenaState.isLobbyStage(arena)) {
      return;
    }

    plugin.getDebugger().debug("Arena {0} got force started by {1} with timer {2}", arena.getId(), player.getName(), timer);
    if(arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS) {
      arena.setArenaState(IArenaState.STARTING, true);
    }
    if(timer <= 0) {
      arena.setForceStart(true);
      new MessageBuilder("IN_GAME_MESSAGES_ADMIN_FORCESTART").asKey().player(player).arena(arena).sendArena();
    } else {
      if(arena.getTimer() <= timer) {
        return;
      }
      arena.setTimer(timer, true);
      new MessageBuilder("IN_GAME_MESSAGES_LOBBY_REDUCED_TIME").asKey().integer(timer).player(player).arena(arena).sendArena();
    }
  }

  public static boolean areInSameArena(Player one, Player two) {
    IPluginArena arena = plugin.getArenaRegistry().getArena(one);

    return arena != null && arena.equals(plugin.getArenaRegistry().getArena(two));
  }

  public static PluginMain getPlugin() {
    return plugin;
  }
}
