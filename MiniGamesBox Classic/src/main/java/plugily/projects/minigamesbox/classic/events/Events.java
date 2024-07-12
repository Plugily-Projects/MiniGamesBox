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

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerSwapHandItemsEvent;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class Events implements Listener {

  private final PluginMain plugin;

  public Events(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onItemSwap(PlugilyPlayerSwapHandItemsEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpawn(CreatureSpawnEvent event) {
    if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM
        || (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND)) {
      return;
    }

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      Location startLoc = arena.getStartLocation();

      if(startLoc != null && event.getEntity().getWorld().equals(startLoc.getWorld())
          && event.getEntity().getLocation().distance(startLoc) < 150) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler
  public void onExplosionCancel(EntityExplodeEvent event) {
    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      Location start = arena.getStartLocation();
      if(start.getWorld().getName().equals(event.getLocation().getWorld().getName())
          && start.distance(event.getLocation()) < 300) {
        event.blockList().clear();
      }
    }
  }


  @EventHandler
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {
    if(!plugin.getConfigPreferences().getOption("BLOCK_IN_GAME_COMMANDS")) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }

    String command = event.getMessage().substring(1);
    int index = command.indexOf(' ');

    if(index >= 0)
      command = command.substring(0, index);

    for(String msg : plugin.getConfig().getStringList("Commands.Whitelist")) {
      if(command.equalsIgnoreCase(msg)) {
        return;
      }
    }
    if(command.equalsIgnoreCase(plugin.getPluginNamePrefixLong()) || event.getMessage().contains("leave") || event.getMessage().contains("stats") || command.equalsIgnoreCase(plugin.getCommandAdminPrefix()) || command.equalsIgnoreCase(plugin.getCommandAdminPrefixLong())) {
      return;
    }
    if(event.getPlayer().isOp() || event.getPlayer().hasPermission(plugin.getPluginNamePrefixLong() + ".command.override")) {
      return;
    }
    event.setCancelled(true);
    new MessageBuilder("IN_GAME_COMMANDS_BLOCKED").asKey().player(event.getPlayer()).arena(arena).sendPlayer();
  }


  @EventHandler
  public void onItemMove(InventoryClickEvent event) {
    if(!plugin.getConfigPreferences().getOption("BLOCK_IN_GAME_ITEM_MOVE")) {
      return;
    }
    if(!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArena(((Player) event.getWhoClicked()));
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != IArenaState.IN_GAME) {
      if(event.getClickedInventory() == event.getWhoClicked().getInventory()) {
        if(event.getView().getType() == InventoryType.WORKBENCH || event.getView().getType() == InventoryType.ANVIL || event.getView().getType() == InventoryType.ENCHANTING || event.getView().getType() == InventoryType.CRAFTING || event.getView().getType() == InventoryType.PLAYER) {
          event.setResult(Event.Result.DENY);
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerCraft(CraftItemEvent event) {
    if (!plugin.getConfigPreferences().getOption("BLOCK_IN_GAME_ITEM_MOVE")) {
      return;
    }
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArena(((Player) event.getWhoClicked()));
    if (arena == null) {
      return;
    }
    if (arena.getArenaState() != IArenaState.IN_GAME) {
      event.setCancelled(true);
    }
  }


  @EventHandler
  public void onDecay(LeavesDecayEvent event) {
    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      Location startLoc = arena.getStartLocation();

      if(startLoc != null && event.getBlock().getWorld().equals(startLoc.getWorld()) && event.getBlock().getLocation().distance(startLoc) < 150) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler
  public void onCraft(PlugilyPlayerInteractEvent event) {
    IPluginArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || event.getClickedBlock() == null) {
      return;
    }
    if(event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
      event.setCancelled(true);
    }
    if(event.getClickedBlock() == null) {
      return;
    }
    if(event.getClickedBlock().getType() == XMaterial.PAINTING.parseMaterial() || event.getClickedBlock().getType() == XMaterial.FLOWER_POT.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInGameBedEnter(PlayerBedEnterEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
    if(event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting) {
      if(event.getRemover() instanceof Player && plugin.getArenaRegistry().isInArena((Player) event.getRemover())) {
        event.setCancelled(true);
        return;
      }
      if(!(event.getRemover() instanceof Projectile)) {
        return;
      }
      Projectile projectile = (Projectile) event.getRemover();
      if(projectile.getShooter() instanceof Player && plugin.getArenaRegistry().isInArena((Player) projectile.getShooter())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onArmorStandDestroy(EntityDamageByEntityEvent event) {
    if(!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    final LivingEntity livingEntity = (LivingEntity) event.getEntity();
    if(livingEntity.getType() != EntityType.ARMOR_STAND) {
      return;
    }
    if(event.getDamager() instanceof Player && plugin.getArenaRegistry().isInArena((Player) event.getDamager())) {
      event.setCancelled(true);
    } else if(event.getDamager() instanceof Projectile) {
      Projectile projectile = (Projectile) event.getDamager();
      if(projectile.getShooter() instanceof Player && plugin.getArenaRegistry().isInArena((Player) projectile.getShooter())) {
        event.setCancelled(true);
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteractWithArmorStand(PlayerArmorStandManipulateEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }


}
