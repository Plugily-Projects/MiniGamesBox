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
package plugily.projects.minigamesbox.classic.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.user.User;

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
    plugin.getSpecialItemManager().removeSpecialItemsOfStage(event.getPlayer(), SpecialItem.DisplayStage.SERVER_JOIN);
    PluginArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena != null) {
      plugin.getArenaManager().leaveAttempt(event.getPlayer(), arena);
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    plugin.getUserManager().saveAllStatistic(user);
    plugin.getUserManager().removeUser(user);

    plugin.getArgumentsRegistry().getSpyChat().disableSpyChat(event.getPlayer());
  }

}
