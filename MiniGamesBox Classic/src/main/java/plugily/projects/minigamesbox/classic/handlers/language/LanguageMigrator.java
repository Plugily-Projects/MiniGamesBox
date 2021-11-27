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


package plugily.projects.minigamesbox.classic.handlers.language;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.migrator.MigratorUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't now how it works! You can break migrator modyfing these values!
 */

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public static final int LANGUAGE_FILE_VERSION = 19;
  public static final int CONFIG_FILE_VERSION = 19;
  private final Main plugin;
  private final List<String> migratable = Arrays.asList("config", "kits",
      "language", "special_items", "bungee", "mysql");

  public LanguageMigrator(Main plugin) {
    this.plugin = plugin;

    //checks if file architecture don't need to be updated to 3.x format
    //check if using releases before 2.1.0 or 2.1.0+
    FileConfiguration lang = ConfigUtils.getConfig(plugin, "language");
    if((lang.isSet("STATS-AboveLine") && lang.isSet("SCOREBOARD-Zombies"))
        || (lang.isSet("File-Version") && plugin.getConfig().isSet("Config-Version"))) {
      migrateToNewFormat();
    }

    //initializes migrator to update files with latest values
    configUpdate();
    languageFileUpdate();
  }

  private void configUpdate() {
    if(plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) {
      return;
    }

    plugin.getDebugger().sendConsoleMsg("&eSystem notify >> Your config file is outdated! Updating...");
    File file = new File(plugin.getDataFolder() + "/config.yml");
    File bungeefile = new File(plugin.getDataFolder() + "/bungee.yml");
    File kitsfile = new File(plugin.getDataFolder() + "/kits.yml");

    int version = plugin.getConfig().getInt("Version", CONFIG_FILE_VERSION - 1);

    for(int i = version; i < CONFIG_FILE_VERSION; i++) {
      switch(i) {
        case 1:
          break;
        default:
          break;
      }
    }
    updateConfigVersionControl(version);
    plugin.reloadConfig();
    plugin.getDebugger().sendConsoleMsg("&a[System notify] Config updated, no comments were removed :)");
    plugin.getDebugger().sendConsoleMsg("&a[System notify] You're using latest config file version! Nice!");
  }

  private void languageFileUpdate() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    if(config.getString("File-Version-Do-Not-Edit", "").equals(Integer.toString(LANGUAGE_FILE_VERSION))) {
      return;
    }
    plugin.getDebugger().sendConsoleMsg("&e[System notify] Your language file is outdated! Updating...");

    int version = LANGUAGE_FILE_VERSION - 1;
    if(NumberUtils.isNumber(config.getString("File-Version-Do-Not-Edit"))) {
      version = Integer.parseInt(config.getString("File-Version-Do-Not-Edit", ""));
    } else {
      plugin.getDebugger().sendConsoleMsg("&c[System notify] Failed to parse language file version!");
    }
    updateLanguageVersionControl(version);

    File file = new File(plugin.getDataFolder() + "/language.yml");

    for(int i = version; i < LANGUAGE_FILE_VERSION; i++) {
      switch(version) {
        case 1:
          break;
        default:
          break;
      }
      version++;
    }
    plugin.getDebugger().sendConsoleMsg("&a[System notify] Language file updated! Nice!");
    plugin.getDebugger().sendConsoleMsg("&a[System notify] You're using latest language file version! Nice!");
  }

  private void migrateToNewFormat() {
    plugin.getMessageUtils().gonnaMigrate();
    plugin.getDebugger().sendConsoleMsg("&aMigrating all files to the new file format...");
    plugin.getDebugger().sendConsoleMsg("&aDon't worry! Old files will be renamed not overridden!");
    for(String fileName : migratable) {
      File file = new File(plugin.getDataFolder() + "/" + fileName + ".yml");
      if(!file.exists()) {
        continue;
      }
      if(file.renameTo(new File(plugin.getDataFolder(), plugin.getDataFolder() + plugin.getPluginNamePrefix() + "2_" + file + ".yml"))) {
        plugin.getDebugger().sendConsoleMsg("&aRenamed file " + file + ".yml");
        continue;
      }
      plugin.getDebugger().sendConsoleMsg("&cCouldn't rename file " + file + ".yml. Problems might occur!");
    }
    plugin.getDebugger().sendConsoleMsg("&aDone! Enabling Plugin...");
  }

  private void updateLanguageVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/language.yml");
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "File-Version-Do-Not-Edit: " + oldVersion);
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\r\n# Really, don't edit ;p\r\nFile-Version-Do-Not-Edit: " + LANGUAGE_FILE_VERSION + "\r\n");
  }

  private void updateConfigVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/config.yml");
    MigratorUtils.removeLineFromFile(file, "# Don't modify.");
    MigratorUtils.removeLineFromFile(file, "Version: " + oldVersion);
    MigratorUtils.removeLineFromFile(file, "# No way! You've reached the end! But... where's the dragon!?");
    MigratorUtils.addNewLines(file, "# Don't modify\r\nVersion: " + CONFIG_FILE_VERSION + "\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
  }

}
