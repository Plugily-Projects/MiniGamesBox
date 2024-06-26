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
package plugily.projects.minigamesbox.classic.events.spectator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerPickupArrow;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class SpectatorEvents implements Listener {

  private final PluginMain plugin;

  public SpectatorEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetEvent event) {
    if(event.getTarget() instanceof Player && plugin.getUserManager().getUser((Player) event.getTarget()).isSpectator()) {
      event.setCancelled(true);
      event.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetLivingEntityEvent event) {
    if(event.getTarget() instanceof Player && plugin.getUserManager().getUser((Player) event.getTarget()).isSpectator()) {
      event.setCancelled(true);
      event.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteract(PlugilyPlayerInteractEntityEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onShear(PlayerShearEntityEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onConsume(PlayerItemConsumeEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntity() instanceof Player && plugin.getUserManager().getUser((Player) event.getEntity()).isSpectator()) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if(!plugin.getUserManager().getUser(player).isSpectator()) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    if(player.getLocation().getY() < VersionUtils.getWorldMinHeight(player.getWorld())) {
      VersionUtils.teleport(player, arena.getStartLocation()).thenAccept(bol -> event.setDamage(0));
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamageByBlock(EntityDamageByBlockEvent event) {
    if(event.getEntity() instanceof Player && plugin.getUserManager().getUser((Player) event.getEntity()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    if(event.getDamager() instanceof Player && plugin.getUserManager().getUser((Player) event.getDamager()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPickup(PlugilyEntityPickupItemEvent event) {
    if(event.getEntity() instanceof Player && plugin.getUserManager().getUser((Player) event.getEntity()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onArrowPickup(PlugilyPlayerPickupArrow event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  //this will spawn orb at spec location when it's taken by spectator
  @EventHandler
  public void onPlayerExpChange(PlayerExpChangeEvent e) {
    if(plugin.getUserManager().getUser(e.getPlayer()).isSpectator()) {
      Location loc = e.getPlayer().getLocation();
      e.setAmount(0);
      Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB), 30);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onTarget(EntityTargetEvent e) {
    if(e.getTarget() instanceof Player && plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()
        && (e.getEntity() instanceof ExperienceOrb || e.getEntity() instanceof Creature)) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDropItem(PlayerDropItemEvent event) {
    IPluginArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena != null && (arena.getArenaState() != IArenaState.IN_GAME || plugin.getUserManager().getUser(event.getPlayer()).isSpectator())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInteractEntityInteract(PlugilyPlayerInteractEntityEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onRightClick(PlugilyPlayerInteractEvent event) {
    if(plugin.getArenaRegistry().getArena(event.getPlayer()) != null && plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    org.bukkit.inventory.Inventory clicked = event.getClickedInventory();
    Player who = (Player) event.getWhoClicked();
    if(clicked != null && clicked.getType() == InventoryType.PLAYER && clicked.getType() == InventoryType.CRAFTING && plugin.getArenaRegistry().getArena(who) != null && plugin.getUserManager().getUser(who).isSpectator()) {
      event.setResult(Event.Result.DENY);
      event.setCancelled(true);
    }
  }

}
