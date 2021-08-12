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

package plugily.projects.minigamesbox.classic.utils.version.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBEntityPickupItemEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBInventoryClickEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEntityEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerPickupArrow;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class LegacyEvents implements Listener {

  public LegacyEvents(JavaPlugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onEntityPickupItem(PlayerPickupItemEvent event) {
    CBEntityPickupItemEvent cbEvent = new CBEntityPickupItemEvent(event.getPlayer(), event.getItem(), event.getRemaining());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerPickupArrow(PlayerPickupItemEvent event) {
    if(!(event.getItem() instanceof Projectile)) {
      return;
    }
    CBPlayerPickupArrow cbEvent = new CBPlayerPickupArrow(event.getPlayer(), event.getItem(), (Projectile) event.getItem(), event.getRemaining(), event.getFlyAtPlayer());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    CBPlayerInteractEvent cbEvent = new CBPlayerInteractEvent(event.getPlayer(), event.getItem(), EquipmentSlot.HAND, event.getAction(), event.getBlockFace(), event.getClickedBlock(), event.getMaterial(),  event.hasItem(), event.hasBlock());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
    CBPlayerInteractEntityEvent cbEvent = new CBPlayerInteractEntityEvent(event.getPlayer(), EquipmentSlot.HAND, event.getRightClicked());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) {
    CBInventoryClickEvent cbEvent = new CBInventoryClickEvent(event.getClick(), event.getCurrentItem(), event.getClickedInventory(), event.getCursor(), event.getHotbarButton(), event.getAction(), event.getRawSlot(), event.getSlot(), event.getSlotType(), event.isLeftClick(), event.isRightClick(), event.isShiftClick(), event.getView());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

}
