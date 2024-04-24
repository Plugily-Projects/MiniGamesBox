package plugily.projects.minigamesbox.api.arena;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPluginArenaRegistry {
  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return true when player is in arena, false if otherwise
   */
  boolean isInArena(@NotNull Player player);

  /**
   * Returns arena where the player is
   *
   * @param player target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  @Nullable IPluginArena getArena(Player player);

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  @Nullable IPluginArena getArena(String id);

  int getArenaPlayersOnline();

  void registerArena(IPluginArena arena);

  void unregisterArena(IPluginArena arena);

  void registerArenas();

  void registerArena(String key);

  @NotNull List<IPluginArena> getArenas();

  List<World> getArenaIngameWorlds();

  List<World> getArenaWorlds();

  void shuffleBungeeArena();

  int getBungeeArena();
}
