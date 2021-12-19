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

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArenaRegistry {

  private final List<PluginArena> arenas = new ArrayList<>();
  private final PluginMain plugin;
  private final List<World> arenaIngameWorlds = new ArrayList<>();

  private int bungeeArena = -999;

  public PluginArenaRegistry(PluginMain plugin) {
    this.plugin = plugin;
  }

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return true when player is in arena, false if otherwise
   */
  public boolean isInArena(@NotNull Player player) {
    return getArena(player) != null;
  }

  /**
   * Returns arena where the player is
   *
   * @param player target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  @Nullable
  public PluginArena getArena(Player player) {
    if(player == null) {
      return null;
    }

    java.util.UUID playerId = player.getUniqueId();

    for(PluginArena loopArena : arenas) {
      for(Player arenaPlayer : loopArena.getPlayers()) {
        if(arenaPlayer.getUniqueId().equals(playerId)) {
          return loopArena;
        }
      }
    }

    return null;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  @Nullable
  public PluginArena getArena(String id) {
    for(PluginArena loopArena : arenas) {
      if(loopArena.getId().equalsIgnoreCase(id)) {
        return loopArena;
      }
    }
    return null;
  }

  public int getArenaPlayersOnline() {
    int players = 0;
    for(PluginArena arena : arenas) {
      players += arena.getPlayers().size();
    }
    return players;
  }

  public void registerArena(PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Instance registered", arena.getId());
    arenas.add(arena);

    World startLocWorld = arena.getStartLocation().getWorld();
    if(startLocWorld != null)
      arenaIngameWorlds.add(startLocWorld);
  }

  public void unregisterArena(PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Instance unregistered", arena.getId());
    arenas.remove(arena);

    World startLocWorld = arena.getStartLocation().getWorld();
    if(startLocWorld != null)
      arenaIngameWorlds.remove(startLocWorld);
  }

  public PluginArena getNewArena(String id) {
    return new PluginArena(id);
  }

  public void registerArenas() {
    plugin.getDebugger().debug("[ArenaRegistry] Initial arenas registration");
    long start = System.currentTimeMillis();

    if(!arenas.isEmpty()) {
      for(PluginArena arena : new ArrayList<>(arenas)) {
        unregisterArena(arena);
      }
    }

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if(section == null) {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_NO_INSTANCES_CREATED"));
      return;
    }

    for(String id : section.getKeys(false)) {
      if(id.equalsIgnoreCase("default")) {
        continue;
      }

      PluginArena arena = getNewArena(id);

      if(!additionalValidatorChecks(section, arena, id)) {
        arena.setReady(false);
        registerArena(arena);
        continue;
      }

      arena.setMapName(section.getString(id + ".mapname", "none"));
      arena.setMinimumPlayers(section.getInt(id + ".minimumplayers", 1));
      arena.setMaximumPlayers(section.getInt(id + ".maximumplayers", 2));


      registerArena(arena);
      arena.start();
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INSTANCE_STARTED").replace("%arena%", id));
    }
    ConfigUtils.saveConfig(plugin, config, "arenas");

    plugin.getDebugger().debug("[ArenaRegistry] Arenas registration completed took {0}ms", System.currentTimeMillis() - start);
  }

  public boolean additionalValidatorChecks(ConfigurationSection section, PluginArena arena, String id) {
    Location startLoc = LocationSerializer.getLocation(section.getString(id + ".Startlocation", "world,364.0,63.0,-72.0,0.0,0.0"));
    Location lobbyLoc = LocationSerializer.getLocation(section.getString(id + ".lobbylocation", "world,364.0,63.0,-72.0,0.0,0.0"));
    Location endLoc = LocationSerializer.getLocation(section.getString(id + ".Endlocation", "world,364.0,63.0,-72.0,0.0,0.0"));

    if(lobbyLoc == null || lobbyLoc.getWorld() == null || startLoc == null || startLoc.getWorld() == null
        || endLoc == null || endLoc.getWorld() == null) {
      section.set(id + ".isdone", false);
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION").replace("%arena%", id).replace("%error%", "Location world is invalid"));
      return false;
    }

    if(!section.getBoolean(id + ".isdone")) {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION").replace("%arena%", id).replace("%error%", "NOT VALIDATED"));
      return false;
    }
    World startLocWorld = arena.getStartLocation().getWorld();
    if(startLocWorld == null) {
      plugin.getDebugger().sendConsoleMsg("Arena world of " + id + " does not exist or not loaded.");
      return false;
    }
    if(startLocWorld.getDifficulty() == Difficulty.PEACEFUL) {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION").replace("%arena%", id).replace("%error%", "THERE IS A WRONG " +
          "DIFFICULTY -> SET IT TO ANOTHER ONE THAN PEACEFUL"));
      return false;
    }
    arena.setLobbyLocation(lobbyLoc);
    arena.setStartLocation(startLoc);
    arena.setEndLocation(endLoc);
    return true;
  }

  @NotNull
  public List<PluginArena> getArenas() {
    return arenas;
  }

  public List<World> getArenaIngameWorlds() {
    return arenaIngameWorlds;
  }

  public void shuffleBungeeArena() {
    if(!arenas.isEmpty()) {
      bungeeArena = ThreadLocalRandom.current().nextInt(arenas.size());
    }
  }

  public int getBungeeArena() {
    if(bungeeArena == -999 && !arenas.isEmpty()) {
      bungeeArena = ThreadLocalRandom.current().nextInt(arenas.size());
    }
    return bungeeArena;
  }
}