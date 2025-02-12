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


package plugily.projects.minigamesbox.classic.handlers.language;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.migrator.MigratorUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't know how it works! You can break migrator modifying these values!
 */

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public enum CoreFileVersion {
    /*ARENA_SELECTOR(0),*/ ARENAS(1), BUNGEE(1), CONFIG(5), KITS(2),
    LANGUAGE(2), /*LEADERBOARDS(0),*/ MYSQL(1), PERMISSIONS(1), POWERUPS(1),
    REWARDS(1), /*SIGNS(0),*/ SPECIAL_ITEMS(1), SPECTATOR(1)/*, STATS(0)*/;

    private final int version;

    CoreFileVersion(int version) {
      this.version = version;
    }

    public int getVersion() {
      return version;
    }
  }

  private final PluginMain plugin;

  public LanguageMigrator(PluginMain plugin) {
    this.plugin = plugin;

    //check for minigamebox file overhaul
    FileConfiguration config = ConfigUtils.getConfig(plugin, "config");
    if(config.isSet("Version")) {
      moveAllPluginFiles("OLD_FILES");
      FileConfiguration arenas = ConfigUtils.getConfig(plugin, "OLD_FILES/arenas");
      ConfigurationSection section = arenas.getConfigurationSection("instances");
      if(section != null) {
        for(String id : section.getKeys(false)) {
          String startLoc = section.getString(id + ".Startlocation", "null");
          String endLoc = section.getString(id + ".Endlocation", "null");
          section.set(id + ".startlocation", startLoc);
          section.set(id + ".endlocation", endLoc);
          section.set(id + ".Startlocation", null);
          section.set(id + ".Endlocation", null);
          if(!section.contains(id + ".spectatorlocation")) {
            section.set(id + ".spectatorlocation", startLoc);
          }
        }
        ConfigUtils.saveConfig(plugin, arenas, "arenas");
      }
      plugin.saveDefaultConfig();
      plugin.setupFiles();
      return;
    }
    updateCoreFiles();
  }

  private void updateCoreFiles() {
    for(CoreFileVersion coreFileVersion : CoreFileVersion.values()) {
      String fileName = coreFileVersion.name().toLowerCase();
      if (fileName.equalsIgnoreCase(CoreFileVersion.KITS.name())) {
        continue;
      }
      int newVersion = coreFileVersion.getVersion();
      File file = new File(plugin.getDataFolder() + "/" + fileName + ".yml");
      FileConfiguration configuration = ConfigUtils.getConfig(plugin, fileName, false);
      if(configuration == null) {
        continue;
      }
      int oldVersion = configuration.getInt("Do-Not-Edit.Core-Version", 0);
      if(oldVersion == newVersion) {
        continue;
      }
      plugin.getDebugger().debug(Level.WARNING, "[System notify] The " + fileName + "  file is outdated! Updating...");
      for(int i = oldVersion; i < newVersion; i++) {
        executeUpdate(file, coreFileVersion, i);
      }

      updateCoreFileVersion(file, configuration, oldVersion, newVersion);
      plugin.getDebugger().debug(Level.WARNING, "[System notify] " + fileName + " updated, no comments were removed :)");
      plugin.getDebugger().debug(Level.WARNING, "[System notify] You're using latest " + fileName + " file now! Nice!");

    }
  }

  private void executeUpdate(File file, CoreFileVersion coreFileVersion, int version) {
    switch(coreFileVersion) {
      case KITS:
        switch(version) {
          case 1:
            renameToFile(file, "old_");
            break;
          case 2:
            renameToFile(file, "deprecated_");
          default:
            break;
        }
      case CONFIG:
        switch(version) {
          case 1:
            MigratorUtils.addNewLines(file, "\r\nChat:\n" +
                "  Separate:\n" +
                "    # Should we enable a separate arena chat for players inside a arena\n" +
                "    # Useful on multi arena servers that don't want the same chat for all players on the server\n" +
                "    Arena: true\n" +
                "    # Should spectators only write with other spectators\n" +
                "    Spectators: true\r\n");
            MigratorUtils.removeLineFromFile(file, "Separate-Arena-Chat: true");
            MigratorUtils.removeLineFromFile(file, "Separate-Arena-Chat: false");
            break;
          case 2:
            MigratorUtils.insertAfterLine(file, "Chat:", "  Format: true");
            MigratorUtils.addNewLines(file, "# Kits configuration\n" +
                "# A server restart is required for changes to apply\n" +
                "Kit:\n" +
                "  # Should we load kits?\n" +
                "  Enabled: false\n" +
                "  # What is the default kit for players?\n" +
                "  # This should be the same name as the file name of the kits file in the kits folder\n" +
                "  Default: \"knight\"\r\n");
            MigratorUtils.removeLineFromFile(file, "# Enable in game (eg. '[KIT][LEVEL] Tigerpanzer_02: hey') special formatting?");
            MigratorUtils.removeLineFromFile(file, "# Formatting is configurable in language.yml");
            MigratorUtils.removeLineFromFile(file, "# You can use PlaceholderAPI placeholders in chat format!");
            MigratorUtils.removeLineFromFile(file, "              Plugin-Chat-Format: true");
            MigratorUtils.removeLineFromFile(file, "              Plugin-Chat-Format: false");
            break;
          case 3:
            MigratorUtils.removeLineFromFile(file, "  Food: false");
            MigratorUtils.removeLineFromFile(file, "  True: false");
            MigratorUtils.insertAfterLine(file, "Damage:", "  Hunger: false");
            break;
          case 4:
            MigratorUtils.insertAfterLine(file, "    Item-Move: true", "    ArmorStand: \n" +
                    "        # Should we block armor stand destroy with double click?\n" +
                    "        Destroy: true\n" +
                    "        # Should we block armor stand interaction?\n" +
                    "        Interact: true\n" +
                    "        # Should these only be blocked while ingame and arena state is in_game? (e.g. Lobby and Ending is blocked)\n" +
                    "        # Setting it to false means on all stages of the game the event will be cancelled. \n" +
                    "        # Setting it to true means only while IN_GAME the event will be cancelled.\n" +
                    "        Check: true\r\n");
            break;
          default:
            break;
        }
        break;
      case LANGUAGE:
        switch(version) {
          case 1:
            MigratorUtils.insertAfterLine(file, "Chat:", "  No-Armor: \"%color_chat_issue%%plugin_prefix% You can't wear armor with your kit!\"");
            break;
          default:
            break;
        }
        break;
      default:
        break;
    }
  }

  private void renameFile(String renamedFilePrefix, String filePath) {
    File file = new File(plugin.getDataFolder() + "/" + filePath + ".yml");
    renameToFile(file, renamedFilePrefix);
  }

  private void renameFile(File oldFile, String renamedFilePrefix) {
    renameToFile(oldFile, renamedFilePrefix);
  }

  private void renameToFile(File oldFile, String renamedFilePrefix) {
    plugin.getMessageUtils().gonnaMigrate();
    if(!oldFile.exists()) {
      plugin.getDebugger().debug(Level.WARNING, "[System notify] &cFile " + oldFile + ".yml does not exits!");
      return;
    }
    try {
      Files.move(Paths.get(oldFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/" + renamedFilePrefix + oldFile.getName()));
      plugin.getDebugger().debug(Level.WARNING, "[System notify] &aRenamed file " + oldFile + "");
    } catch(IOException e) {
      plugin.getDebugger().debug(Level.WARNING, "[System notify] &cCouldn't rename file " + oldFile + ". Problems might occur!");
    }
  }


  public void copyFile(String from, String to) {
    try {
      Files.copy(Paths.get(plugin.getDataFolder() + "/" + from + ".yml"), Paths.get(plugin.getDataFolder() + "/" + to + ".yml"));
    } catch(IOException e) {
      plugin.getDebugger().debug(Level.WARNING, "[System notify] &cCouldn't copy file " + from + ".yml. Problems might occur!");
    }
  }

  private void moveAllPluginFiles(String newSubFolderName) {
    plugin.getDebugger().debug(Level.WARNING, "[System notify] &aMoving all files to the to the subfolder " + newSubFolderName + "...");
    plugin.getDebugger().debug(Level.WARNING, "[System notify] &aDon't worry! Old files will be renamed not overridden!");
    File folder = new File(plugin.getDataFolder() + "");
    try {
      Files.createDirectory(Paths.get(plugin.getDataFolder() + "/" + newSubFolderName));
    } catch(IOException e) {
      plugin.getDebugger().debug(Level.WARNING, "[System notify] &cCouldn't create subfolder " + newSubFolderName + ". Problems might occur!");
    }
    for(File fileEntry : folder.listFiles()) {
      renameFile(fileEntry, newSubFolderName + "/");
    }
  }

  private void updateCoreFileVersion(File file, FileConfiguration fileConfiguration, int oldVersion, int newVersion) {
    int fileVersion = fileConfiguration.getInt("Do-Not-Edit.File-Version", 0);
    updateFileVersion(file, newVersion, oldVersion, fileVersion, fileVersion);
  }

  public void updatePluginFileVersion(File file, FileConfiguration fileConfiguration, int oldVersion, int newVersion) {
    int coreVersion = fileConfiguration.getInt("Do-Not-Edit.Core-Version", 0);
    updateFileVersion(file, coreVersion, coreVersion, newVersion, oldVersion);
  }

  private void updateFileVersion(File file, int coreVersion, int oldCoreVersion, int fileVersion, int oldFileVersion) {
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "# You edited it, huh? Next time hurt yourself!");
    MigratorUtils.removeLineFromFile(file, "Do-Not-Edit:");
    MigratorUtils.removeLineFromFile(file, "  File-Version: " + oldFileVersion + "");
    MigratorUtils.removeLineFromFile(file, "  Core-Version: " + oldCoreVersion + "");
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\r\n" +
        "# Really, don't edit ;p\r\n" +
        "# You edited it, huh? Next time hurt yourself!\r\n" +
        "Do-Not-Edit:\r\n" +
        "  File-Version: " + fileVersion + "\r\n" +
        "  Core-Version: " + coreVersion + "\r\n");
  }

}
