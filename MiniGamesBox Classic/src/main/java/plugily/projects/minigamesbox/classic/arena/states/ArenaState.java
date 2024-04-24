package plugily.projects.minigamesbox.classic.arena.states;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public class ArenaState {

  private static final Map<String, String> cache = new HashMap<>();

  public static String getFormattedName(IArenaState arenaState) {
    return arenaState.getFormattedName();
  }

  public static String getPlaceholder(IArenaState arenaState) {
    if (!cache.containsKey(arenaState.getFormattedName())) {
      PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);
      cache.put(arenaState.getFormattedName(), new MessageBuilder(plugin.getLanguageManager().getLanguageMessage("Placeholders.Game-States." + getFormattedName(arenaState))).build());
    }

    return cache.get(arenaState.getFormattedName());
  }

  public static boolean isLobbyStage(IPluginArena arena) {
    return arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == IArenaState.STARTING || arena.getArenaState() == IArenaState.FULL_GAME;
  }

  public static boolean isStartingStage(IPluginArena arena) {
    return arena.getArenaState() == IArenaState.STARTING || arena.getArenaState() == IArenaState.FULL_GAME;
  }
}
