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

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaManager;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.ArenaUtils;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerSwapHandItemsEvent;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class Events implements Listener {

  private final Main plugin;

  public Events(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSpawn(CreatureSpawnEvent event) {
    if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM
        || (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND)) {
      return;
    }

    for(Arena arena : plugin.getArenaRegistry().getArenas()) {
      Location startLoc = arena.getStartLocation();

      if(startLoc != null && event.getEntity().getWorld().equals(startLoc.getWorld())
          && event.getEntity().getLocation().distance(startLoc) < 150) {
        event.setCancelled(true);
        break;
      }
    }
  }


  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    if(plugin.getArenaRegistry().getArena(event.getPlayer()) != null && (plugin.getUserManager().getUser(event.getPlayer()).isSpectator() || event.getItemDrop().getItemStack().getType() == Material.SADDLE)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onExplosionCancel(EntityExplodeEvent event) {
    for(Arena arena : plugin.getArenaRegistry().getArenas()) {
      Location start = arena.getStartLocation();
      if(start.getWorld().getName().equals(event.getLocation().getWorld().getName())
          && start.distance(event.getLocation()) < 300) {
        event.blockList().clear();
      }
    }
  }


  @EventHandler
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {
    Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || !plugin.getConfig().getBoolean("Block-Commands-In-Game", true)) {
      return;
    }

    String command = event.getMessage().substring(1);
    int index = command.indexOf(' ');

    if(index >= 0)
      command = command.substring(0, index);

    for(String msg : plugin.getConfig().getStringList("Whitelisted-Commands")) {
      if(command.equalsIgnoreCase(msg)) {
        return;
      }
    }
    if(command.equalsIgnoreCase("plugily") || event.getMessage().contains("leave") || event.getMessage().contains("stats") || command.equalsIgnoreCase("plugilyadmin")) {
      return;
    }
    if(event.getPlayer().isOp() || event.getPlayer().hasPermission("plugily.command.override")) {
      return;
    }
    event.setCancelled(true);
    event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ONLY_COMMAND_IN_GAME_IS_LEAVE));
  }

  @EventHandler
  public void onSpecialItem(CBPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }

    ItemStack itemStack = VersionUtils.getItemInHand(event.getPlayer());
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return;
    }

    String key = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack).getPath();
    if(key == null) {
      return;
    }
    if(key.equalsIgnoreCase(plugin.getSpecialItemManager().getSpecialItem("FORCESTART").getPath())) {
      event.setCancelled(true);
      ArenaUtils.arenaForceStart(event.getPlayer());
      return;
    }
    if(key.equals(plugin.getSpecialItemManager().getSpecialItem("LOBBY_LEAVE_ITEM").getPath()) || key.equals(plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_LEAVE_ITEM").getPath())) {
      event.setCancelled(true);
      if(plugin.getConfigPreferences().getOption("BUNGEE")) {
        plugin.getBungeeManager().connectToHub(event.getPlayer());
      } else {
        ArenaManager.leaveAttempt(event.getPlayer(), arena);
      }
    }
  }

  private boolean checkSpecialItem(ItemStack itemStack, Player player) {
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return false;
    }
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return false;
    }
    SpecialItem key = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack);
    return key != SpecialItem.INVALID_ITEM;
  }

  @EventHandler
  public void onItemMove(InventoryClickEvent e) {
    if(!(e.getWhoClicked() instanceof Player)) {
      return;
    }
    Arena arena = plugin.getArenaRegistry().getArena(((Player) e.getWhoClicked()));
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      if(e.getClickedInventory() == e.getWhoClicked().getInventory()) {
        if(e.getView().getType() == InventoryType.CRAFTING || e.getView().getType() == InventoryType.PLAYER) {
          e.setResult(Event.Result.DENY);
        }
      }
    }
  }

  @EventHandler
  public void onSwap(CBPlayerSwapHandItemsEvent event) {
    if(checkSpecialItem(event.getOffHandItem(), event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onDecay(LeavesDecayEvent event) {
    for(Arena arena : plugin.getArenaRegistry().getArenas()) {
      Location startLoc = arena.getStartLocation();

      if(startLoc != null && event.getBlock().getWorld().equals(startLoc.getWorld()) && event.getBlock().getLocation().distance(startLoc) < 150) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Arena arena = plugin.getArenaRegistry().getArena((Player) event.getEntity());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.ENDING) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCraft(CBPlayerInteractEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer()) && event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
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
  public void onArmorStandDestroy(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof LivingEntity)) {
      return;
    }
    final LivingEntity livingEntity = (LivingEntity) e.getEntity();
    if(livingEntity.getType() != EntityType.ARMOR_STAND) {
      return;
    }
    if(e.getDamager() instanceof Player && plugin.getArenaRegistry().isInArena((Player) e.getDamager())) {
      e.setCancelled(true);
    } else if(e.getDamager() instanceof Projectile) {
      Projectile projectile = (Projectile) e.getDamager();
      if(projectile.getShooter() instanceof Player && plugin.getArenaRegistry().isInArena((Player) projectile.getShooter())) {
        e.setCancelled(true);
        return;
      }
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteractWithArmorStand(PlayerArmorStandManipulateEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }


}
