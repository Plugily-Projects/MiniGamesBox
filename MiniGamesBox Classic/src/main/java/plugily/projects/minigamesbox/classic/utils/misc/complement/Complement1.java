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

package plugily.projects.minigamesbox.classic.utils.misc.complement;

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

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class Complement1 implements Complement {

  @Override
  public String getDisplayName(ItemMeta meta) {
    return meta.getDisplayName();
  }

  @Override
  public String getTitle(InventoryView view) {
    return view.getTitle();
  }

  @Override
  public String getLine(SignChangeEvent event, int line) {
    return event.getLine(line);
  }

  @Override
  public void setLine(SignChangeEvent event, int line, String text) {
    event.setLine(line, text);
  }

  @Override
  public String getLine(Sign sign, int line) {
    return sign.getLine(line);
  }

  @Override
  public Inventory createInventory(InventoryHolder owner, int size, String title) {
    return Bukkit.createInventory(owner, size, title);
  }

  @Override
  public void setLore(ItemMeta meta, List<String> lore) {
    meta.setLore(lore);
  }

  @Override
  public void setDisplayName(ItemMeta meta, String name) {
    meta.setDisplayName(name);
  }

  @Override
  public String getDisplayName(Player player) {
    return player.getDisplayName();
  }

  @Override
  public void setLine(Sign sign, int line, String text) {
    sign.setLine(line, text);
  }

  @Override
  public List<String> getLore(ItemMeta meta) {
    return meta.hasLore() ? meta.getLore() : new ArrayList<>();
  }

  @Override
  public void setDeathMessage(PlayerDeathEvent event, String message) {
    event.setDeathMessage(message);
  }

  @Override
  public void setJoinMessage(PlayerJoinEvent event, String message) {
    event.setJoinMessage(message);
  }

  @Override
  public void setQuitMessage(PlayerQuitEvent event, String message) {
    event.setQuitMessage(message);
  }

  @Override
  public void setMotd(ServerListPingEvent event, String motd) {
    event.setMotd(motd);
  }

  @Override
  public void kickPlayer(Player player, String message) {
    player.kickPlayer(message);
  }

  @Override
  public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
    return Bukkit.createInventory(owner, type, title);
  }
}
