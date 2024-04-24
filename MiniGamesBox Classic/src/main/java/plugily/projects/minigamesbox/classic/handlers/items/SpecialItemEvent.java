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

package plugily.projects.minigamesbox.classic.handlers.items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerSwapHandItemsEvent;

import java.util.HashSet;

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

    if(!plugin.getArenaRegistry().isInArena(player)) {
      return false;
    }

    return plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack) != plugin.getSpecialItemManager().getInvalidItem();
  }

  @EventHandler
  public void onSwap(PlugilyPlayerSwapHandItemsEvent event) {
    if(checkSpecialItem(event.getOffHandItem(), event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpecialItem(PlugilyPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }
    Player player = event.getPlayer();

    ItemStack itemStack = VersionUtils.getItemInHand(player);
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return;
    }

    SpecialItem relatedSpecialItem = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack);
    if(relatedSpecialItem == plugin.getSpecialItemManager().getInvalidItem()) {
      return;
    }
    plugin.getDebugger().debug("Found the item " + relatedSpecialItem.getPath());
    event.setCancelled(true);
    if(relatedSpecialItem.getPermission() != null && !relatedSpecialItem.getPermission().isEmpty()) {
      if(!plugin.getBukkitHelper().hasPermission(player, relatedSpecialItem.getPermission())) {
        return;
      }
    }
    plugin.getDebugger().debug("SpecialItem {0} - Permission check for {1} true", relatedSpecialItem.getPath(), player.getName());

    IPluginArena arena = plugin.getArenaRegistry().getArena(player);

    if(arena == null) {
      plugin.getRewardsHandler().performReward(player, new HashSet<>(relatedSpecialItem.getRewards()));
      return;
    }
    plugin.getRewardsHandler().performReward(player, arena, new HashSet<>(relatedSpecialItem.getRewards()));
    if(plugin.getSpecialItemManager().getSpecialItem("FORCESTART").getPath().equals(relatedSpecialItem.getPath())) {
      PluginArenaUtils.arenaForceStart(player, plugin.getConfig().getInt("Time-Manager.Shorten-Waiting-Force", 5));
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("LOBBY_LEAVE").getPath().equals(relatedSpecialItem.getPath()) || plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_LEAVE").getPath().equals(relatedSpecialItem.getPath())) {
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        plugin.getBungeeManager().connectToHub(player);
      } else {
        plugin.getArenaManager().leaveAttempt(player, arena);
      }
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("BACK_TO_LOBBY").getPath().equals(relatedSpecialItem.getPath())) {
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        plugin.getBungeeManager().connectToHub(player);
      }
    }
    if(plugin.getSpecialItemManager().getSpecialItem("PLAYERS_LIST").getPath().equals(relatedSpecialItem.getPath())) {
      plugin.getSpectatorItemsManager().openSpectatorMenu(player, arena);
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_SETTINGS").getPath().equals(relatedSpecialItem.getPath())) {
      plugin.getSpectatorItemsManager().getSpectatorSettingsMenu().getInventory().open(player);
      return;
    }
    if(plugin.getSpecialItemManager().getSpecialItem("KIT_SELECTOR_MENU").getPath().equals(relatedSpecialItem.getPath())) {
      plugin.getKitMenuHandler().createMenu(player);
    }
  }

}
