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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerPickupArrow;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerSwapHandItemsEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class Events implements Listener {

  public Events(JavaPlugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    PlugilyPlayerSwapHandItemsEvent cbEvent = new PlugilyPlayerSwapHandItemsEvent(event.getPlayer(), event.getMainHandItem(), event.getOffHandItem());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    PlugilyEntityPickupItemEvent cbEvent = new PlugilyEntityPickupItemEvent(event.getEntity(), event.getItem(), event.getRemaining());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
    PlugilyPlayerPickupArrow cbEvent;
    try {
      Projectile projectile = (Projectile) event.getClass().getDeclaredMethod("getArrow").invoke(event);
      cbEvent = new PlugilyPlayerPickupArrow(event.getPlayer(), event.getItem(), projectile, event.getRemaining(), VersionUtils.isPaper() && event.getFlyAtPlayer());
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      cbEvent = new PlugilyPlayerPickupArrow(event.getPlayer(), event.getItem(), null, event.getRemaining(), false);
    }
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    PlugilyPlayerInteractEvent cbEvent = new PlugilyPlayerInteractEvent(event.getPlayer(), event.getItem(), event.getHand(), event.getAction(), event.getBlockFace(), event.getClickedBlock(), event.getMaterial(), event.hasItem(), event.hasBlock());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
    PlugilyPlayerInteractEntityEvent cbEvent = new PlugilyPlayerInteractEntityEvent(event.getPlayer(), event.getHand(), event.getRightClicked());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }
}
