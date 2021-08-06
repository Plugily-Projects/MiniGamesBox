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

package plugily.projects.minigamesbox.classic.misc.stuff;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface Complement {

  String getTitle(InventoryView view);

  String getDisplayName(ItemMeta meta);

  String getDisplayName(Player player);

  String getLine(SignChangeEvent event, int line);

  String getLine(Sign sign, int line);

  void setLine(SignChangeEvent event, int line, String text);

  void setLine(Sign sign, int line, String text);

  Inventory createInventory(InventoryHolder owner, int size, String title);

  Inventory createInventory(InventoryHolder owner, InventoryType type, String title);

  void setLore(ItemMeta meta, List<String> lore);

  List<String> getLore(ItemMeta meta);

  void setDisplayName(ItemMeta meta, String name);

  void setDeathMessage(PlayerDeathEvent event, String message);

  void setJoinMessage(PlayerJoinEvent event, String message);

  void setQuitMessage(PlayerQuitEvent event, String message);

  void setMotd(ServerListPingEvent event, String motd);

  void kickPlayer(Player player, String message);

  default void broadcastMessage(String message) {
    for(Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(message);
    }
  }

  default void broadcastMessage(List<String> messages) {
    for(String msg : messages) {
      for(Player player : Bukkit.getOnlinePlayers()) {
        player.sendMessage(msg);
      }
    }
  }
}
