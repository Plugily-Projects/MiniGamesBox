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

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import plugily.projects.minigamesbox.classic.PluginMain;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArenaEvents implements Listener {

  private final PluginMain plugin;

  public PluginArenaEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }


  @EventHandler
  public void playerCommandExecution(PlayerCommandPreprocessEvent e) {
    if(plugin.getConfigPreferences().getOption("SHORT_COMMANDS")) {
      if(e.getMessage().equalsIgnoreCase("/start")) {
        e.getPlayer().performCommand(plugin.getCommandAdminPrefix() + " forcestart");
        e.setCancelled(true);
        return;
      }
      if(e.getMessage().equalsIgnoreCase("/leave")) {
        e.getPlayer().performCommand(plugin.getPluginNamePrefix() + " leave");
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onEntityDamageEvent(EntityDamageEvent e) {
    if(e.getEntityType() != EntityType.PLAYER) {
      return;
    }
    Player victim = (Player) e.getEntity();
    PluginArena arena = plugin.getArenaRegistry().getArena(victim);
    if(arena == null) {
      return;
    }
    if(e.getCause() == EntityDamageEvent.DamageCause.DROWNING && plugin.getConfigPreferences().getOption("DROWNING_DAMAGE")) {
      e.setCancelled(true);
    }
    if((e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) && plugin.getConfigPreferences().getOption("FIRE_DAMAGE")) {
      e.setCancelled(true);
    }
    if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
      if(!plugin.getConfigPreferences().getOption("FALL_DAMAGE")) {
        if(e.getDamage() >= 20.0) {
          //kill the player for suicidal death, else do not
          victim.damage(1000.0);
        }
      }
      e.setCancelled(true);
    }
    //kill the player on void
    if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
      if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
        victim.damage(0);
        victim.teleport(arena.getLobbyLocation());
      } else {
        victim.damage(1000.0);
        victim.teleport(arena.getStartLocation());
      }
    }
  }
}
