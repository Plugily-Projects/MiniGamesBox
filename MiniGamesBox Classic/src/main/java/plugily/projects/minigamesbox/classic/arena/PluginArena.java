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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.arena.managers.IPluginMapRestorerManager;
import plugily.projects.minigamesbox.api.arena.managers.IPluginScoreboardManager;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameStateChangeEvent;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.managers.BossbarManager;
import plugily.projects.minigamesbox.classic.arena.managers.PluginMapRestorerManager;
import plugily.projects.minigamesbox.classic.arena.managers.PluginScoreboardManager;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.states.*;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.*;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArena extends BukkitRunnable implements IPluginArena {

  private static PluginMain plugin;
  private final String id;

  private final Set<Player> players = new HashSet<>();

  //all arena values that are integers, contains constant and floating values
  private Map<String, ArenaOption> arenaOptions = new HashMap<>();
  //instead of 3 location fields we use map with GameLocation enum
  private final Map<GameLocation, Location> gameLocations = new EnumMap<>(GameLocation.class);
  //all handlers for all game states, we don't include them all in one runnable because it would be too big
  private final Map<IArenaState, ArenaStateHandler> gameStateHandlers = new EnumMap<>(IArenaState.class);

  private PluginScoreboardManager scoreboardManager;
  private BossbarManager bossbarManager;
  private PluginMapRestorerManager mapRestorerManager;

  private IArenaState iArenaState = IArenaState.WAITING_FOR_PLAYERS;
  private String mapName = "";
  private boolean forceStart = false;
  private boolean forceArenaState = false;
  private boolean forceArenaTimer = false;
  private boolean ready = false;

  @TestOnly
  protected PluginArena(String id, String mapName) {
    this.id = id;
    this.mapName = mapName;
    gameStateHandlers.put(IArenaState.WAITING_FOR_PLAYERS, new PluginWaitingState());
    gameStateHandlers.put(IArenaState.STARTING, new PluginStartingState());
    gameStateHandlers.put(IArenaState.IN_GAME, new PluginInGameState());
    gameStateHandlers.put(IArenaState.ENDING, new PluginEndingState());
    gameStateHandlers.put(IArenaState.RESTARTING, new PluginRestartingState());
    for (ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    loadArenaOptions();
  }

  public void loadArenaOptions() {
    arenaOptions.clear();
    arenaOptions = plugin.getArenaOptionManager().getDefaultArenaOptions();
    FileConfiguration arenas = ConfigUtils.getConfig(plugin, "arenas");
    for (Map.Entry<String, ArenaOption> options : arenaOptions.entrySet()) {
      if ("null".equals(options.getValue().getPath())) {
        continue;
      }
      setArenaOption(options.getKey(), arenas.getInt("instances." + id + "." + options.getValue().getPath(), options.getValue().getValue()));
    }
  }


  @Override
  public Integer getArenaOption(String name) {
    ArenaOption arenaOption = arenaOptions.get(name);

    if (arenaOption == null) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }

    return arenaOption.getValue();
  }

  public void setArenaOption(String name, int value) {
    ArenaOption arenaOption = arenaOptions.get(name);

    if (arenaOption == null) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }
    arenaOption.setValue(value);
  }

  public void changeArenaOptionBy(String name, int value) {
    ArenaOption arenaOption = arenaOptions.get(name);

    if (arenaOption == null) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }

    arenaOption.setValue(arenaOption.getValue() + value);
  }


  public PluginArena(String id) {
    this.id = id == null ? "" : id;
    this.mapName = id;
    setDefaultValues();
    scoreboardManager = new PluginScoreboardManager(this);
    mapRestorerManager = new PluginMapRestorerManager(this);
    gameStateHandlers.put(IArenaState.WAITING_FOR_PLAYERS, new PluginWaitingState());
    gameStateHandlers.put(IArenaState.STARTING, new PluginStartingState());
    gameStateHandlers.put(IArenaState.IN_GAME, new PluginInGameState());
    gameStateHandlers.put(IArenaState.ENDING, new PluginEndingState());
    gameStateHandlers.put(IArenaState.RESTARTING, new PluginRestartingState());
    for (ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    bossbarManager = new BossbarManager(this);
  }

  public void removeGameStateHandler(ArenaState ArenaState) {
    gameStateHandlers.remove(ArenaState);
  }

  public void addGameStateHandler(IArenaState iArenaState, ArenaStateHandler arenaStateHandler) {
    gameStateHandlers.put(iArenaState, arenaStateHandler);
    arenaStateHandler.init(plugin);
  }

  public void setScoreboardManager(PluginScoreboardManager scoreboardManager) {
    this.scoreboardManager = scoreboardManager;
  }

  public static void init(PluginMain plugin) {
    PluginArena.plugin = plugin;
  }

  private void setDefaultValues() {
    loadArenaOptions();

    Location firstWorldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();

    for (GameLocation location : GameLocation.values()) {
      gameLocations.put(location, firstWorldSpawn);
    }
  }

  @Override
  public boolean isReady() {
    return ready;
  }

  @Override
  public void setReady(boolean ready) {
    this.ready = ready;
  }


  @Override
  public void run() {
    //idle task
    if (iArenaState == iArenaState.WAITING_FOR_PLAYERS && players.isEmpty()) {
      return;
    }
    plugin.getDebugger().performance("ArenaTask", "[PerformanceMonitor] [{0}] Running game task", id);
    long start = System.currentTimeMillis();
    forceArenaState = false;
    forceArenaTimer = false;
    bossbarManager.bossBarUpdate();
    scoreboardManager.updateScoreboards();
    ArenaStateHandler arenaStateHandler;
    if (iArenaState == IArenaState.FULL_GAME) {
      arenaStateHandler = gameStateHandlers.get(IArenaState.STARTING);
    } else {
      arenaStateHandler = gameStateHandlers.get(iArenaState);
    }
    arenaStateHandler.handleCall(this);
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Got from handler {1} and {2}, current {3}", getId(), arenaStateHandler.getArenaTimer(), arenaStateHandler.getArenaStateChange(), iArenaState);
    if (!forceArenaTimer && arenaStateHandler.getArenaTimer() != -999) {
      plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaTimer to {1} from handler", getId(), arenaStateHandler.getArenaTimer());
      setTimer(arenaStateHandler.getArenaTimer());
    }
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Force State {1}", getId(), forceArenaState);
    if (!forceArenaState && iArenaState != arenaStateHandler.getArenaStateChange()) {
      plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Change to {1}", getId(), arenaStateHandler.getArenaStateChange());
      if (!(iArenaState == iArenaState.FULL_GAME && arenaStateHandler.getArenaStateChange() == iArenaState.STARTING)) {
        plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaState to {1} from handler", getId(), arenaStateHandler.getArenaStateChange());
        setArenaState(arenaStateHandler.getArenaStateChange(), false);
      }
    }
    setTimer(getTimer() - 1);
    plugin.getDebugger().performance("ArenaTask", "[PerformanceMonitor] [{0}] Game task finished took {1}ms", id, System.currentTimeMillis() - start);
  }

  public boolean isForceStart() {
    return forceStart;
  }

  @Override
  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }

  @Override
  public BossbarManager getBossbarManager() {
    return bossbarManager;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int getMinimumPlayers() {
    return getArenaOption("MINIMUM_PLAYERS");
  }

  public void setMinimumPlayers(int minimumPlayers) {
    setArenaOption("MINIMUM_PLAYERS", minimumPlayers);
  }

  @Override
  public String getMapName() {
    return mapName;
  }


  @Override
  public void setMapName(String mapName) {
    this.mapName = mapName;
  }


  @Override
  public int getTimer() {
    return getArenaOption("TIMER");
  }

  @Override
  public void setTimer(int timer) {
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaTimer to {1}", getId(), timer);
    setArenaOption("TIMER", timer);
  }

  @Override
  public void setTimer(int timer, boolean forceArenaTimer) {
    this.forceArenaTimer = forceArenaTimer;
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaTimer to {1} {2}", getId(), timer, forceArenaTimer);
    setArenaOption("TIMER", timer);
  }

  @Override
  public int getMaximumPlayers() {
    return getArenaOption("MAXIMUM_PLAYERS");
  }

  public void setMaximumPlayers(int maximumPlayers) {
    setArenaOption("MAXIMUM_PLAYERS", maximumPlayers);
  }

  @Override
  public IPluginMapRestorerManager getMapRestorerManager() {
    return mapRestorerManager;
  }


  @Override
  @NotNull
  public IArenaState getArenaState() {
    return iArenaState;
  }

  @Override
  public void setArenaState(@NotNull IArenaState ArenaState, boolean forceArenaState) {
    this.iArenaState = ArenaState;
    this.forceArenaState = forceArenaState;
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaState to {1} {2}", getId(), ArenaState, forceArenaState);
    Bukkit.getPluginManager().callEvent(new PlugilyGameStateChangeEvent(this, ArenaState));
    plugin.getSignManager().updateSigns();
  }

  @Override
  public void setArenaState(@NotNull IArenaState ArenaState) {
    this.iArenaState = ArenaState;
    this.forceArenaState = true;
    plugin.getDebugger().performance("ArenaUpdate", "Arena {0} Changed ArenaState to {1} {2}", getId(), ArenaState, forceArenaState);
    Bukkit.getPluginManager().callEvent(new PlugilyGameStateChangeEvent(this, ArenaState));
    plugin.getSignManager().updateSigns();
  }

  @Override
  @NotNull
  public Set<Player> getPlayers() {
    return players;
  }


  @Override
  @Nullable
  public Location getSpectatorLocation() {
    return gameLocations.get(GameLocation.SPECTATOR);
  }

  @Override
  public void setSpectatorLocation(Location spectatorLoc) {
    gameLocations.put(GameLocation.SPECTATOR, spectatorLoc);
  }

  @Override
  public Location getLobbyLocation() {
    return gameLocations.get(GameLocation.LOBBY);
  }

  @Override
  public void setLobbyLocation(Location loc) {
    gameLocations.put(GameLocation.LOBBY, loc);
  }

  @Override
  public Location getStartLocation() {
    return gameLocations.get(GameLocation.START);
  }

  @Override
  public void setStartLocation(Location location) {
    gameLocations.put(GameLocation.START, location);
  }

  @Override
  public void teleportToEndLocation(Player player) {
    // We should check for #isEnabled to make sure plugin is enabled
    // This happens in some cases
    if (plugin.isEnabled() && plugin.getConfigPreferences().getOption("BUNGEEMODE")
        && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      plugin.getBungeeManager().connectToHub(player);
      plugin.getDebugger().debug("{0} has left the arena {1}! Teleported to the Hub server.", player.getName(), this);
    }
    VersionUtils.teleport(player, getEndLocation());
  }

  @Override
  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  @Override
  public Location getLocation(GameLocation gameLocation) {
    return gameLocations.get(gameLocation);
  }

  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
  }

  public void start() {
    plugin.getDebugger().debug("[{0}] Instance started", id);
    runTaskTimer(plugin, 20L, 20L);
    setArenaState(IArenaState.WAITING_FOR_PLAYERS, true);
  }

  @Override
  public IPluginScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  public void setMapRestorerManager(PluginMapRestorerManager mapRestorerManager) {
    this.mapRestorerManager = mapRestorerManager;
  }


  @NotNull
  @Override
  public List<Player> getPlayersLeft() {
    List<Player> playersLeft = new ArrayList<>();

    for (IUser user : plugin.getUserManager().getUsers(this)) {
      if (!user.isSpectator()) {
        playersLeft.add(user.getPlayer());
      }
    }

    return playersLeft;
  }

  @Override
  public IPluginMain getPlugin() {
    return plugin;
  }
}