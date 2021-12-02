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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameStateChangeEvent;
import plugily.projects.minigamesbox.classic.arena.managers.BossbarManager;
import plugily.projects.minigamesbox.classic.arena.managers.MapRestorerManager;
import plugily.projects.minigamesbox.classic.arena.managers.ScoreboardManager;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.states.ArenaStateHandler;
import plugily.projects.minigamesbox.classic.arena.states.EndingState;
import plugily.projects.minigamesbox.classic.arena.states.InGameState;
import plugily.projects.minigamesbox.classic.arena.states.RestartingState;
import plugily.projects.minigamesbox.classic.arena.states.StartingState;
import plugily.projects.minigamesbox.classic.arena.states.WaitingState;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class Arena extends BukkitRunnable {

  private static Main plugin;
  private final String id;

  private final Set<Player> players = new HashSet<>();

  //all arena values that are integers, contains constant and floating values
  private final Map<String, ArenaOption> arenaOptions = new HashMap<>();
  //instead of 3 location fields we use map with GameLocation enum
  private final Map<GameLocation, Location> gameLocations = new EnumMap<>(GameLocation.class);
  //all handlers for all game states, we don't include them all in one runnable because it would be too big
  private final Map<ArenaState, ArenaStateHandler> gameStateHandlers = new EnumMap<>(ArenaState.class);

  private ScoreboardManager scoreboardManager;
  private BossbarManager bossbarManager;
  private MapRestorerManager mapRestorerManager;

  private ArenaState arenaState = ArenaState.WAITING_FOR_PLAYERS;
  private String mapName = "";
  private boolean forceStart = false;
  private boolean ready = true;

  @TestOnly
  protected Arena(String id, String mapName) {
    this.id = id;
    this.mapName = mapName;
    gameStateHandlers.put(ArenaState.WAITING_FOR_PLAYERS, new WaitingState());
    gameStateHandlers.put(ArenaState.STARTING, new StartingState());
    gameStateHandlers.put(ArenaState.IN_GAME, new InGameState());
    gameStateHandlers.put(ArenaState.ENDING, new EndingState());
    gameStateHandlers.put(ArenaState.RESTARTING, new RestartingState());
    for(ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    loadArenaOptions();
  }

  public void loadArenaOptions() {
    ArenaOption.getOptions().forEach((s, option) -> arenaOptions.put(s, new ArenaOption(option.getPath(), plugin.getConfig().getInt(option.getPath(), option.getValue()), option.isProtected())));
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


  /**
   * Register a new config option
   *
   * @param name   The name of the Option
   * @param option Contains the path and the default value
   */
  public void registerArenaOption(String name, ArenaOption option) {
    if(arenaOptions.containsKey(name)) {
      throw new IllegalStateException("Option with path " + name + " was already registered");
    }
    arenaOptions.put(name, option);
  }

  /**
   * Remove config options that are not protected
   *
   * @param name The name of the Option
   */
  public void unregisterArenaOption(String name) {
    ArenaOption option = arenaOptions.get(name);
    if(option == null) {
      return;
    }
    if(option.isProtected()) {
      throw new IllegalStateException("Protected options cannot be removed!");
    }
    arenaOptions.remove(name);
  }

  public Map<String, ArenaOption> getArenaOptions() {
    return Collections.unmodifiableMap(arenaOptions);
  }


  public Arena(String id) {
    this.id = id == null ? "" : id;
    bossbarManager = new BossbarManager(this);
    scoreboardManager = new ScoreboardManager(this);
    mapRestorerManager = new MapRestorerManager(this);
    setDefaultValues();
    gameStateHandlers.put(ArenaState.WAITING_FOR_PLAYERS, new WaitingState());
    gameStateHandlers.put(ArenaState.STARTING, new StartingState());
    gameStateHandlers.put(ArenaState.IN_GAME, new InGameState());
    gameStateHandlers.put(ArenaState.ENDING, new EndingState());
    gameStateHandlers.put(ArenaState.RESTARTING, new RestartingState());
    for(ArenaStateHandler handler : gameStateHandlers.values()) {
      handler.init(plugin);
    }
    loadArenaOptions();
  }

  public static void init(Main plugin) {
    Arena.plugin = plugin;
  }

  private void setDefaultValues() {
    //see loadArenaOptions / may switch locations..
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
    bossbarManager.bossBarUpdate();
    gameStateHandlers.get(arenaState).handleCall(this);
    plugin.getDebugger().performance("ArenaTask", "[PerformanceMonitor] [{0}] Game task finished took {1}ms", id, System.currentTimeMillis() - start);
  }

  public boolean isForceStart() {
    return forceStart;
  }

  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }


  public BossbarManager getBossbarManager() {
    return bossbarManager;
  }

  /**
   * Get arena identifier used to get arenas by string.
   *
   * @return arena name
   * @see ArenaRegistry#getArena(String)
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
    setArenaOption("TIMER", timer);
  }

  public int getMaximumPlayers() {
    return getArenaOption("MAXIMUM_PLAYERS");
  }

  public void setMaximumPlayers(int maximumPlayers) {
    setArenaOption("MAXIMUM_PLAYERS", maximumPlayers);
  }

  public MapRestorerManager getMapRestorerManager() {
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
   * @param arenaState new game state of arena
   * @see ArenaState
   * @see PlugilyGameStateChangeEvent
   */
  public void setArenaState(@NotNull ArenaState arenaState) {
    this.arenaState = arenaState;
    Bukkit.getPluginManager().callEvent(new PlugilyGameStateChangeEvent(this, arenaState));
    plugin.getSignManager().updateSigns();
  }

  @NotNull
  public Set<Player> getPlayers() {
    return players;
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

    player.teleport(getEndLocation());
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
    setArenaState(ArenaState.WAITING_FOR_PLAYERS);
  }

  public ScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  @NotNull
  public List<Player> getPlayersLeft() {
    List<Player> list = new ArrayList<>();

    for(Player player : players) {
      User user = plugin.getUserManager().getUser(player);
      if(!user.isSpectator()) {
        list.add(user.getPlayer());
      }
    }

    return list;
  }

  public Main getPlugin() {
    return plugin;
  }

  public enum BarAction {
    ADD, REMOVE
  }

  public enum GameLocation {
    START, LOBBY, END
  }

}