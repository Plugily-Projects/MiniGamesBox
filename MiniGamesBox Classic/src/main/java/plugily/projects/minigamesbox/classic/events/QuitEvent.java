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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class QuitEvent implements Listener {

  private final PluginMain plugin;

  public QuitEvent(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    onQuit(event.getPlayer());
  }

  @EventHandler
  public void onKick(PlayerKickEvent event) {
    onQuit(event.getPlayer());
  }

  private void onQuit(Player player) {
    plugin
        .getSpecialItemManager()
        .removeSpecialItemsOfStage(player, SpecialItem.DisplayStage.SERVER_JOIN);
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if (arena != null) {
      plugin.getArenaManager().leaveAttempt(player, arena);
    }
    IUser user = plugin.getUserManager().getUser(player);
    plugin.getUserManager().saveAllStatistic(user);
    plugin.getUserManager().removeUser(user);

    plugin.getArgumentsRegistry().getSpyChat().disableSpyChat(player);
  }
}
