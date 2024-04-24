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

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.preferences.ICommandShorter;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Tigerpanzer_02
 * <p>Created at 01.11.2021
 */
public class PluginArenaEvents implements Listener {

  private final PluginMain plugin;

  public PluginArenaEvents(PluginMain plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void playerCommandExecution(PlayerCommandPreprocessEvent event) {
    for(ICommandShorter commandShorter : plugin.getConfigPreferences().getCommandShorts()) {
      if(event.getMessage().equalsIgnoreCase(commandShorter.getShortCommand())) {
        event.getPlayer().performCommand(commandShorter.getExecuteCommand());
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onEntityDamageEvent(EntityDamageEvent event) {
    if(event.getEntityType() != EntityType.PLAYER) {
      return;
    }
    Player victim = (Player) event.getEntity();
    IPluginArena arena = plugin.getArenaRegistry().getArena(victim);
    if(arena == null) {
      return;
    }
    switch(event.getCause()) {
      case DROWNING:
        if(!plugin.getConfigPreferences().getOption("DROWNING_DAMAGE")) {
          event.setCancelled(true);
        }
        break;
      case FIRE:
      case FIRE_TICK:
        if(!plugin.getConfigPreferences().getOption("FIRE_DAMAGE")) {
          event.setCancelled(true);
        }
        break;
      case FALL:
        if(additionalFallDamageRules(victim, arena, event)) {
          break;
        }
        if(!plugin.getConfigPreferences().getOption("FALL_DAMAGE")) {
          event.setCancelled(true);
        } else if(event.getDamage() >= 20.0) {
          // kill the player for suicidal death, else do not
          victim.damage(1000.0);
        }
        break;
      case VOID:
        if(arena.getArenaState() != IArenaState.IN_GAME) {
          victim.damage(0);
          VersionUtils.teleport(victim, arena.getLobbyLocation());
        } else {
          handleIngameVoidDeath(victim, arena);
        }
        break;
      default:
        break;
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    IPluginArena arena = plugin.getArenaRegistry().getArena(player);
    if (arena == null) {
      return;
    }
    if (!plugin.getConfigPreferences().getOption("HUNGER_LOSE")) {
      event.setCancelled(true);
      event.setFoodLevel(20);
    }
  }

  public boolean additionalFallDamageRules(Player victim, IPluginArena arena, EntityDamageEvent event) {
    return false;
  }

  public void handleIngameVoidDeath(Player victim, IPluginArena arena) {
    victim.damage(1000.0);
    VersionUtils.teleport(victim, arena.getStartLocation());
  }
}
