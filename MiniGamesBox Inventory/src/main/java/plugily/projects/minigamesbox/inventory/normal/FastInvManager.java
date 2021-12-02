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

package plugily.projects.minigamesbox.inventory.normal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager for FastInv listeners.
 *
 * @author MrMicky
 */
public final class FastInvManager {

  private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

  private FastInvManager() {
    throw new UnsupportedOperationException();
  }

  /**
   * Register listeners for FastInv.
   *
   * @param plugin plugin to register
   * @throws NullPointerException  if plugin is null
   * @throws IllegalStateException if FastInv is already registered
   */
  public static void register(Plugin plugin) {
    Objects.requireNonNull(plugin, "plugin");

    if(REGISTERED.getAndSet(true)) {
      throw new IllegalStateException("FastInv is already registered");
    }

    Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
  }

  /**
   * Close all open FastInv inventories.
   */
  public static void closeAll() {
    Bukkit.getOnlinePlayers().stream()
        .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof FastInv)
        .forEach(Player::closeInventory);
  }

  public static final class InventoryListener implements Listener {

    private final Plugin plugin;

    public InventoryListener(Plugin plugin) {
      this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
      if(e.getInventory().getHolder() instanceof FastInv && e.getClickedInventory() != null) {
        FastInv inv = (FastInv) e.getInventory().getHolder();

        boolean wasCancelled = e.isCancelled();
        e.setCancelled(true);
        inv.handleClick(e);

        // This prevents un-canceling the event if another plugin canceled it before
        if(!wasCancelled && !e.isCancelled()) {
          e.setCancelled(false);
        }
      }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
      if(e.getInventory().getHolder() instanceof FastInv) {
        FastInv inv = (FastInv) e.getInventory().getHolder();

        inv.handleOpen(e);
      }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
      if(e.getInventory().getHolder() instanceof FastInv) {
        FastInv inv = (FastInv) e.getInventory().getHolder();

        if(inv.handleClose(e)) {
          Bukkit.getScheduler().runTask(this.plugin, () -> inv.open((Player) e.getPlayer()));
        }
      }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
      if(e.getPlugin() == this.plugin) {
        closeAll();

        REGISTERED.set(false);
      }
    }
  }
}