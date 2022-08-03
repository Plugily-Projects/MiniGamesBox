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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameStateChangeEvent;
import plugily.projects.minigamesbox.classic.arena.managers.BossbarManager;
import plugily.projects.minigamesbox.classic.arena.managers.PluginMapRestorerManager;
import plugily.projects.minigamesbox.classic.arena.managers.PluginScoreboardManager;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.states.ArenaStateHandler;
import plugily.projects.minigamesbox.classic.arena.states.PluginEndingState;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.arena.states.PluginRestartingState;
import plugily.projects.minigamesbox.classic.arena.states.PluginStartingState;
import plugily.projects.minigamesbox.classic.arena.states.PluginWaitingState;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArena extends BukkitRunnable {

  private static PluginMain plugin;
  private final String id;

  private final Set<Player> players = new HashSet<>();

  //all arena values that are integers, contains constant and floating values
  private Map<String, ArenaOption> arenaOptions = new HashMap<>();
  //instead of 3 location fields we use map with GameLocation enum
  private final Map<GameLocation, Location> gameLocations = new EnumMap<>(GameLocation.class);
  //all handlers for all game states, we don't include them all in one runnable because it would be too big
  private final Map<ArenaState, ArenaStateHandler> gameStateHandlers = new EnumMap<>(ArenaState.class);

  private PluginScoreboardManager scoreboardManager;
  private BossbarManager bossbarManager;
  private PluginMapRestorerManager mapRestorerManager;

  private ArenaState arenaState = ArenaState.WAITING_FOR_PLAYERS;
  private String mapName = "";
  private boolean forceStart = false;
  private boolean forceArenaState = false;
  private boolean forceArenaTimer = false;
  private boolean ready = true;

  @TestOnly
  protected PluginArena(String id, String mapName) {
    this.id = id;
    this.mapName = mapName;
    gameStateHandlers.put(ArenaState.WAITING_FOR_PLAYERS, new PluginWaitingState());
    gameStateHandlers.put(ArenaState.STARTING, new PluginStartingState());
    gameStateHandlers.put(ArenaState.IN_GAME, new PluginInGameState());
    gameStateHandlers.put(ArenaState.ENDING, new PluginEndingState());
    gameStateHandlers.put(ArenaState.RESTARTING, new PluginRestartingState());
    for(ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    loadArenaOptions();
  }

  public void loadArenaOptions() {
    arenaOptions.clear();
    arenaOptions = new HashMap<>(plugin.getArenaOptionManager().getArenaOptions());
    FileConfiguration arenas = ConfigUtils.getConfig(plugin, "arenas");
    for(Map.Entry<String, ArenaOption> options : plugin.getArenaOptionManager().getArenaOptions().entrySet()) {
      if("null".equals(options.getValue().getPath())) {
        continue;
      }
      setArenaOption(options.getKey(), arenas.getInt("instances." + id + "." + options.getValue().getPath(), options.getValue().getValue()));
    }
  }

  /**
   * Returns whether option value is true or false
   *
   * @param name option to get value from
   * @return true or false based on user configuration
   */
  public Integer getArenaOption(String name) {
    if(!arenaOptions.containsKey(name)) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }
    return arenaOptions.get(name).getValue();
  }

  public void setArenaOption(String name, int value) {
    if(!arenaOptions.containsKey(name)) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }
    arenaOptions.get(name).setValue(value);
  }

  public void changeArenaOptionBy(String name, int value) {
    if(!arenaOptions.containsKey(name)) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }
    arenaOptions.get(name).setValue(arenaOptions.get(name).getValue() + value);
  }


  public PluginArena(String id) {
    this.id = id == null ? "" : id;
    this.mapName = id;
    setDefaultValues();
    scoreboardManager = new PluginScoreboardManager(this);
    mapRestorerManager = new PluginMapRestorerManager(this);
    gameStateHandlers.put(ArenaState.WAITING_FOR_PLAYERS, new PluginWaitingState());
    gameStateHandlers.put(ArenaState.STARTING, new PluginStartingState());
    gameStateHandlers.put(ArenaState.IN_GAME, new PluginInGameState());
    gameStateHandlers.put(ArenaState.ENDING, new PluginEndingState());
    gameStateHandlers.put(ArenaState.RESTARTING, new PluginRestartingState());
    for(ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    bossbarManager = new BossbarManager(this);
  }

  public void removeGameStateHandler(ArenaState arenaState) {
    gameStateHandlers.remove(arenaState);
  }

  public void addGameStateHandler(ArenaState arenaState, ArenaStateHandler arenaStateHandler) {
    gameStateHandlers.put(arenaState, arenaStateHandler);
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
    for(GameLocation location : GameLocation.values()) {
      gameLocations.put(location, Bukkit.getWorlds().get(0).getSpawnLocation());
    }
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }


  @Override
  public void run() {
    //idle task
    if(arenaState == ArenaState.WAITING_FOR_PLAYERS && players.isEmpty()) {
      return;
    }
    plugin.getDebugger().performance("ArenaTask", "[PerformanceMonitor] [{0}] Running game task", id);
    long start = System.currentTimeMillis();
    forceArenaState = false;
    forceArenaTimer = false;
    bossbarManager.bossBarUpdate();
    ArenaStateHandler arenaStateHandler;
    if(arenaState == ArenaState.FULL_GAME) {
      arenaStateHandler = gameStateHandlers.get(ArenaState.STARTING);
    } else {
      arenaStateHandler = gameStateHandlers.get(arenaState);
    }
    arenaStateHandler.handleCall(this);
    plugin.getDebugger().debug("Arena {0} Got from handler {1} and {2}, current {3}", getId(), arenaStateHandler.getArenaTimer(), arenaStateHandler.getArenaStateChange(), arenaState);
    if(!forceArenaTimer && arenaStateHandler.getArenaTimer() != -999) {
      plugin.getDebugger().debug("Arena {0} Changed ArenaTimer to {1} from handler", getId(), arenaStateHandler.getArenaTimer());
      setTimer(arenaStateHandler.getArenaTimer());
    }
    plugin.getDebugger().debug("Arena {0} Force State {1}", getId(), forceArenaState);
    if(!forceArenaState && arenaState != arenaStateHandler.getArenaStateChange()) {
      plugin.getDebugger().debug("Arena {0} Change to {1}", getId(), arenaStateHandler.getArenaStateChange());
      if(!(arenaState == ArenaState.FULL_GAME && arenaStateHandler.getArenaStateChange() == ArenaState.STARTING)) {
        plugin.getDebugger().debug("Arena {0} Changed ArenaState to {1} from handler", getId(), arenaStateHandler.getArenaStateChange());
        setArenaState(arenaStateHandler.getArenaStateChange(), false);
      }
    }
    setTimer(getTimer() - 1);
    plugin.getDebugger().performance("ArenaTask", "[PerformanceMonitor] [{0}] Game task finished took {1}ms", id, System.currentTimeMillis() - start);
  }

  public boolean isForceStart() {
    return forceStart;
  }

  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }

  /**
   * Returns boss bar of the game.
   * Please use doBarAction if possible
   *
   * @return game boss bar manager
   * @see BossbarManager
   */
  public BossbarManager getBossbarManager() {
    return bossbarManager;
  }

  /**
   * Get arena identifier used to get arenas by string.
   *
   * @return arena name
   * @see PluginArenaRegistry#getArena(String)
   */
  public String getId() {
    return id;
  }

  public int getMinimumPlayers() {
    return getArenaOption("MINIMUM_PLAYERS");
  }

  public void setMinimumPlayers(int minimumPlayers) {
    setArenaOption("MINIMUM_PLAYERS", minimumPlayers);
  }

  /**
   * Get arena map name.
   *
   * @return arena map name, <b>it's not arena id</b>
   * @see #getId()
   */
  public String getMapName() {
    return mapName;
  }

  /**
   * Set arena map name.
   *
   * @param mapName new map name, [b]it's not arena id[/b]
   */
  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  /**
   * Get timer of arena.
   *
   * @return timer of lobby time / time to next wave
   */
  public int getTimer() {
    return getArenaOption("TIMER");
  }

  /**
   * Modify game timer.
   *
   * @param timer timer of lobby / time to next wave
   */
  public void setTimer(int timer) {
    plugin.getDebugger().debug("Arena {0} Changed ArenaTimer to {1}", getId(), timer);
    setArenaOption("TIMER", timer);
  }


  /**
   * Modify game timer.
   *
   * @param timer           timer of lobby / time to next wave
   * @param forceArenaTimer should the timer be forced
   */
  public void setTimer(int timer, boolean forceArenaTimer) {
    this.forceArenaTimer = forceArenaTimer;
    plugin.getDebugger().debug("Arena {0} Changed ArenaTimer to {1} {2}", getId(), timer, forceArenaTimer);
    setArenaOption("TIMER", timer);
  }

  public int getMaximumPlayers() {
    return getArenaOption("MAXIMUM_PLAYERS");
  }

  public void setMaximumPlayers(int maximumPlayers) {
    setArenaOption("MAXIMUM_PLAYERS", maximumPlayers);
  }

  public PluginMapRestorerManager getMapRestorerManager() {
    return mapRestorerManager;
  }

  @NotNull
  public ArenaState getArenaState() {
    return arenaState;
  }

  /**
   * Set game state of arena.
   * Calls VillageGameStateChangeEvent
   *
   * @param arenaState      new game state of arena
   * @param forceArenaState should it force the arenaState?
   * @see ArenaState
   * @see PlugilyGameStateChangeEvent
   */
  public void setArenaState(@NotNull ArenaState arenaState, boolean forceArenaState) {
    this.arenaState = arenaState;
    this.forceArenaState = forceArenaState;
    plugin.getDebugger().debug("Arena {0} Changed ArenaState to {1} {2}", getId(), arenaState, forceArenaState);
    Bukkit.getPluginManager().callEvent(new PlugilyGameStateChangeEvent(this, arenaState));
    plugin.getSignManager().updateSigns();
  }

  /**
   * Set game state of arena.
   * Calls VillageGameStateChangeEvent
   *
   * @param arenaState new game state of arena
   * @see ArenaState
   * @see PlugilyGameStateChangeEvent
   */
  public void setArenaState(@NotNull ArenaState arenaState) {
    this.arenaState = arenaState;
    this.forceArenaState = true;
    plugin.getDebugger().debug("Arena {0} Changed ArenaState to {1} {2}", getId(), arenaState, forceArenaState);
    Bukkit.getPluginManager().callEvent(new PlugilyGameStateChangeEvent(this, arenaState));
    plugin.getSignManager().updateSigns();
  }

  @NotNull
  public Set<Player> getPlayers() {
    return players;
  }


  /**
   * Get spectator location of arena.
   *
   * @return end location of arena
   */
  @Nullable
  public Location getSpectatorLocation() {
    return gameLocations.get(GameLocation.SPECTATOR);
  }

  /**
   * Set spectator location of arena.
   *
   * @param spectatorLoc new end location of arena
   */
  public void setSpectatorLocation(Location spectatorLoc) {
    gameLocations.put(GameLocation.SPECTATOR, spectatorLoc);
  }

  public Location getLobbyLocation() {
    return gameLocations.get(GameLocation.LOBBY);
  }

  public void setLobbyLocation(Location loc) {
    gameLocations.put(GameLocation.LOBBY, loc);
  }

  public Location getStartLocation() {
    return gameLocations.get(GameLocation.START);
  }

  public void setStartLocation(Location location) {
    gameLocations.put(GameLocation.START, location);
  }

  public void teleportToEndLocation(Player player) {
    // We should check for #isEnabled to make sure plugin is enabled
    // This happens in some cases
    if(plugin.isEnabled() && plugin.getConfigPreferences().getOption("BUNGEEMODE")
        && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      plugin.getBungeeManager().connectToHub(player);
      plugin.getDebugger().debug("{0} has left the arena {1}! Teleported to the Hub server.", player.getName(), this);
    }
    VersionUtils.teleport(player, getEndLocation());
  }

  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  public Location getLocation(GameLocation gameLocation) {
    return gameLocations.get(gameLocation);
  }

  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
  }

  public void start() {
    plugin.getDebugger().debug("[{0}] Instance started", id);
    runTaskTimer(plugin, 20L, 20L);
    setArenaState(ArenaState.WAITING_FOR_PLAYERS, true);
  }

  public PluginScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  public void setMapRestorerManager(PluginMapRestorerManager mapRestorerManager) {
    this.mapRestorerManager = mapRestorerManager;
  }


  @NotNull
  public List<Player> getPlayersLeft() {
    return plugin.getUserManager().getUsers(this).stream().filter(user -> !user.isSpectator()).map(User::getPlayer).collect(Collectors.toList());
  }

  public PluginMain getPlugin() {
    return plugin;
  }

  public enum BarAction {
    ADD, REMOVE
  }

  public enum GameLocation {
    START, LOBBY, END, SPECTATOR
  }

}