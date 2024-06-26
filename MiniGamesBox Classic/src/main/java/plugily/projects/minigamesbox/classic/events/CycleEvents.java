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

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameJoinAttemptEvent;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 06.02.2022
 */
public class CycleEvents implements Listener {

  private final PluginMain plugin;

  public CycleEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    Bukkit.getScheduler().runTaskLater(plugin, this::changeWorldCycles, 20L * 5);
  }

  @EventHandler
  public void onGameJoinEvent(PlugilyGameJoinAttemptEvent event) {
    changeWorldCycles();
  }

  private void changeWorldCycles() {
    for(World world : plugin.getArenaRegistry().getArenaWorlds()) {
      if(plugin.getConfigPreferences().getOption("WEATHER_CYCLE")) {
        world.setStorm(false);
        world.setThundering(false);
        setWeatherGameRule(world);
      }
      if(plugin.getConfigPreferences().getOption("DAYLIGHT_CYCLE")) {
        world.setTime(plugin.getConfig().getInt("Cycle.Daylight.Time", 10000));
        setDayLightGameRule(world);
      }
    }
  }

  private void setWeatherGameRule(World world) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    } else {
      world.setGameRuleValue("doWeatherCycle", "false");
    }
  }

  private void setDayLightGameRule(World world) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    } else {
      world.setGameRuleValue("doDaylightCycle", "false");
    }
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    if(!plugin.getConfigPreferences().getOption("WEATHER_CYCLE")) {
      return;
    }
    if(!event.toWeatherState()) {
      return;
    }
    if(!plugin.getArenaRegistry().getArenaWorlds().contains(event.getWorld())) {
      return;
    }
    setWeatherGameRule(event.getWorld());
    event.setCancelled(true);
  }

  @EventHandler
  public void onThunderChange(ThunderChangeEvent event) {
    if(!plugin.getConfigPreferences().getOption("WEATHER_CYCLE")) {
      return;
    }
    if(!event.toThunderState()) {
      return;
    }
    if(!plugin.getArenaRegistry().getArenaWorlds().contains(event.getWorld())) {
      return;
    }
    setWeatherGameRule(event.getWorld());
    event.setCancelled(true);
  }

}
