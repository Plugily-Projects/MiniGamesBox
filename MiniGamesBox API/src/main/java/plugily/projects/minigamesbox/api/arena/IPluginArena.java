package plugily.projects.minigamesbox.api.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.arena.managers.IBossbarManager;
import plugily.projects.minigamesbox.api.arena.managers.IPluginMapRestorerManager;
import plugily.projects.minigamesbox.api.arena.managers.IPluginScoreboardManager;
import plugily.projects.minigamesbox.api.events.game.PlugilyGameStateChangeEvent;

import java.util.List;
import java.util.Set;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPluginArena {

  /**
   * Returns whether option value is true or false
   *
   * @param name option to get value from
   * @return true or false based on user configuration
   */
  Integer getArenaOption(String name);

  boolean isReady();

  void setReady(boolean ready);

  void setForceStart(boolean forceStart);

  /**
   * Returns boss bar of the game.
   * Please use doBarAction if possible
   *
   * @return game boss bar manager
   * @see IBossbarManager
   */
  IBossbarManager getBossbarManager();

  /**
   * Get arena identifier used to get arenas by string.
   *
   * @return arena name
   * @see IPluginArenaRegistry#getArena(String)
   */
  String getId();

  int getMinimumPlayers();

  /**
   * Get arena map name.
   *
   * @return arena map name, <b>it's not arena id</b>
   * @see #getId()
   */
  String getMapName();

  /**
   * Set arena map name.
   *
   * @param mapName new map name, [b]it's not arena id[/b]
   */
  void setMapName(String mapName);

  /**
   * Get timer of arena.
   *
   * @return timer of lobby time / time to next wave
   */
  int getTimer();

  /**
   * Modify game timer.
   *
   * @param timer timer of lobby / time to next wave
   */
  void setTimer(int timer);

  /**
   * Modify game timer.
   *
   * @param timer           timer of lobby / time to next wave
   * @param forceArenaTimer should the timer be forced
   */
  void setTimer(int timer, boolean forceArenaTimer);

  int getMaximumPlayers();

  IPluginMapRestorerManager getMapRestorerManager();

  /**
   * Gets the current arena state
   * @return The current arena state
   */
  @NotNull IArenaState getArenaState();

  /**
   * Set game state of arena.
   * Calls VillageGameStateChangeEvent
   *
   * @param ArenaState      new game state of arena
   * @param forceArenaState should it force the arenaState?
   * @see IArenaState
   * @see PlugilyGameStateChangeEvent
   */
  void setArenaState(@NotNull IArenaState ArenaState, boolean forceArenaState);

  /**
   * Set game state of arena.
   * Calls VillageGameStateChangeEvent
   *
   * @param ArenaState new game state of arena
   * @see IArenaState
   * @see PlugilyGameStateChangeEvent
   */
  void setArenaState(@NotNull IArenaState ArenaState);


  /**
   * Gets all the players in the arena
   * @return a set containing all the players
   */
  @NotNull Set<Player> getPlayers();

  /**
   * Get spectator location of arena.
   *
   * @return end location of arena
   */
  @Nullable Location getSpectatorLocation();

  /**
   * Set spectator location of arena.
   *
   * @param spectatorLoc new end location of arena
   */
  void setSpectatorLocation(Location spectatorLoc);

  Location getLobbyLocation();

  void setLobbyLocation(Location loc);

  Location getStartLocation();

  void setStartLocation(Location location);

  void teleportToEndLocation(Player player);

  Location getEndLocation();

  Location getLocation(GameLocation gameLocation);

  IPluginScoreboardManager getScoreboardManager();

  @NotNull List<Player> getPlayersLeft();

  /**
   * Returns the plugin main class
   * @return plugin main
   */
  IPluginMain getPlugin();

  enum IBarAction {
    ADD, REMOVE;
  }

  enum GameLocation {
    START, LOBBY, END, SPECTATOR
  }
}
