package plugily.projects.minigamesbox.api;

import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.api.arena.IPluginArenaRegistry;
import plugily.projects.minigamesbox.api.handlers.language.ILanguageManager;
import plugily.projects.minigamesbox.api.kit.IKitRegistry;
import plugily.projects.minigamesbox.api.preferences.IConfigPreferences;
import plugily.projects.minigamesbox.api.user.IUserManager;
import plugily.projects.minigamesbox.api.utils.misc.IDebugger;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPluginMain {

  FileConfiguration getConfig();

  String getName();

  IDebugger getDebugger();

  IConfigPreferences getConfigPreferences();

  IUserManager getUserManager();

  String getPluginNamePrefix();

  String getPluginNamePrefixLong();

  String getCommandAdminPrefix();

  String getCommandAdminPrefixLong();

  IPluginArenaRegistry getArenaRegistry();

  IKitRegistry getKitRegistry();

  ILanguageManager getLanguageManager();
}
