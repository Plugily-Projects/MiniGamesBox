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
package plugily.projects.minigamesbox.classic.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.utils.serialization.InventorySerializer;
import plugily.projects.minigamesbox.classic.utils.services.UpdateChecker;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class JoinEvent implements Listener {

  private final PluginMain plugin;

  public JoinEvent(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    IPluginArena arena = plugin.getUserManager().getUsersQuitDuringGame().get(event.getPlayer().getUniqueId());
    if(arena != null) {
      VersionUtils.teleport(event.getPlayer(), arena.getEndLocation());
      plugin.getUserManager().getUsersQuitDuringGame().remove(event.getPlayer().getUniqueId());
    }
    plugin.getUserManager().loadStatistics(plugin.getUserManager().getUser(event.getPlayer()));
    //load player inventory in case of server crash, file is deleted once loaded so if file was already
    //deleted player won't receive his backup, in case of crash he will get it back
    if(plugin.getConfigPreferences().getOption("INVENTORY_MANAGER")) {
      InventorySerializer.loadInventory(plugin, event.getPlayer());
    }
    for(Player player : plugin.getServer().getOnlinePlayers()) {
      if(plugin.getArenaRegistry().getArena(player) == null) {
        continue;
      }
      VersionUtils.hidePlayer(plugin, player, event.getPlayer());
      VersionUtils.hidePlayer(plugin, event.getPlayer(), player);
    }
    plugin.getSpecialItemManager().addSpecialItemsOfStage(event.getPlayer(), SpecialItem.DisplayStage.SERVER_JOIN);
  }

  @EventHandler
  public void onJoinCheckVersion(PlayerJoinEvent event) {
    //we want to be the first :)
    if(!plugin.getConfig().getBoolean("Update-Notifier.Enabled", true) || !event.getPlayer().hasPermission(plugin.getPluginNamePrefixLong() + ".updatenotify")) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> UpdateChecker.get().requestUpdateCheck().whenComplete((result, exception) -> {
      if(!result.requiresUpdate()) {
        return;
      }
      if(result.getNewestVersion().contains("b")) {
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(ChatColor.BOLD + plugin.getPluginMessagePrefix() + "UPDATE NOTIFY");
        event.getPlayer().sendMessage(ChatColor.RED + plugin.getPluginMessagePrefix() + "BETA version of software is ready for update! Proceed with caution.");
        event.getPlayer().sendMessage(ChatColor.YELLOW + plugin.getPluginMessagePrefix() + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
      } else {
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(ChatColor.BOLD + plugin.getPluginMessagePrefix() + "UPDATE NOTIFY");
        event.getPlayer().sendMessage(ChatColor.GREEN + plugin.getPluginMessagePrefix() + "Software is ready for update! Download it to keep with latest changes and fixes.");
        event.getPlayer().sendMessage(ChatColor.YELLOW + plugin.getPluginMessagePrefix() + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
      }
    }), 25);
  }
}
