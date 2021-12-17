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

package plugily.projects.minigamesbox.classic.handlers.items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerSwapHandItemsEvent;

import static plugily.projects.minigamesbox.classic.handlers.items.SpecialItem.INVALID_ITEM;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 11.12.2021
 */
public class SpecialItemEvent implements Listener {

  private final PluginMain plugin;

  public SpecialItemEvent(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  private boolean checkSpecialItem(ItemStack itemStack, Player player) {
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return false;
    }
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return false;
    }
    SpecialItem key = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack);
    return key != INVALID_ITEM;
  }

  @EventHandler
  public void onSwap(CBPlayerSwapHandItemsEvent event) {
    if(checkSpecialItem(event.getOffHandItem(), event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpecialItem(CBPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }

    ItemStack itemStack = VersionUtils.getItemInHand(player);
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return;
    }

    SpecialItem relatedSpecialItem = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack);
    if(relatedSpecialItem == INVALID_ITEM) {
      return;
    }
    if(relatedSpecialItem.getPermission() != null && !relatedSpecialItem.getPermission().equalsIgnoreCase("")) {
      if(!plugin.getBukkitHelper().hasPermission(player, relatedSpecialItem.getPermission())) {
        return;
      }
    }
    plugin.getRewardsHandler().performReward(player, arena, relatedSpecialItem.getRewards());
    if(plugin.getSpecialItemManager().getSpecialItem("FORCESTART").equals(relatedSpecialItem)) {
      event.setCancelled(true);
      ArenaUtils.arenaForceStart(player, plugin.getConfig().getInt("Time-Manager.Shorten-Waiting-Force", 5));
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("LOBBY_LEAVE_ITEM").equals(relatedSpecialItem) || plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_LEAVE_ITEM").equals(relatedSpecialItem)) {
      event.setCancelled(true);
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        plugin.getBungeeManager().connectToHub(player);
      } else {
        plugin.getArenaManager().leaveAttempt(player, arena);
      }
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("PLAYERS_LIST").equals(relatedSpecialItem)) {
      event.setCancelled(true);
      plugin.getSpectatorItemsManager().openSpectatorMenu(player, arena);
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_SETTINGS").equals(relatedSpecialItem)) {
      event.setCancelled(true);
      plugin.getSpectatorItemsManager().getSpectatorSettingsMenu().getInventory().open(player);
    }
  }

}