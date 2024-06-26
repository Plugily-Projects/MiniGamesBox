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

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class LobbyEvents implements Listener {

  private final PluginMain plugin;

  public LobbyEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArena((Player) event.getEntity());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != IArenaState.IN_GAME) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onLobbyDamage(EntityDamageEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null || arena.getArenaState() == IArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
    player.setHealth(VersionUtils.getMaxHealth(player));
  }

  @EventHandler
  public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null || arena.getArenaState() == IArenaState.IN_GAME) {
      return;
    }
    if(event.getRightClicked() instanceof ItemFrame && ((ItemFrame) event.getRightClicked()).getItem().getType() != Material.AIR) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onHangingBreak(HangingBreakByEntityEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null || arena.getArenaState() == IArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
  }

}
