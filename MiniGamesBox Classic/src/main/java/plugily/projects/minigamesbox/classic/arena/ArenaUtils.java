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

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.serialization.InventorySerializer;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class ArenaUtils {

  private static PluginMain plugin;

  private ArenaUtils() {
  }

  public static void init(PluginMain plugin) {
    ArenaUtils.plugin = plugin;
  }

  public static void hidePlayer(Player p, Arena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.hidePlayer(plugin, player, p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.showPlayer(plugin, player, p);
    }
  }

  public static void resetPlayerAfterGame(Player player) {
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      VersionUtils.showPlayer(plugin, players, player);
      VersionUtils.showPlayer(plugin, player, players);
    }
    VersionUtils.setGlowing(player, false);
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
    if(plugin.getConfigPreferences().getOption("INVENTORY_MANAGER")) {
      InventorySerializer.loadInventory(plugin, player);
    }
  }

  public static void arenaForceStart(Player player, int timer) {
    //Todo
    if(!plugin.getBukkitHelper().hasPermission(player, plugin.getPermissionsManager().getForceStart())) {
      player.sendMessage(plugin.getChatManager().colorMessage("COMMANDS_NO_PERMISSION"));
      return;
    }

    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      player.sendMessage(plugin.getChatManager().colorMessage("COMMANDS_NOT_PLAYING"));
      return;
    }

    if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
      arena.setArenaState(ArenaState.STARTING);
      arena.setForceStart(true);
      arena.setTimer(timer);
      plugin.getChatManager().broadcast(arena, "IN_GAME_MESSAGES_ADMIN_FORCESTART");
    }
  }

}
