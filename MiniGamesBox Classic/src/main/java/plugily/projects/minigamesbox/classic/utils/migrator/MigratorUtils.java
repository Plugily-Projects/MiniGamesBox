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

package plugily.projects.minigamesbox.classic.utils.migrator;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 * <p>
 * Manage file strings without YamlConfiguration
 * and without losing comments
 * @deprecated api subject to removal
 */
@Deprecated
public class MigratorUtils {

  /**
   * Remove specified line from file
   *
   * @param file         file to use
   * @param lineToRemove line to remove
   */
  public static void removeLineFromFile(File file, String lineToRemove) {
    try {
      List<String> lines = Files.readAllLines(file.toPath());
      List<String> updatedLines = lines.stream().filter(s -> !s.contains(lineToRemove)).collect(Collectors.toList());
      Files.write(file.toPath(), updatedLines);
    } catch(IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().warning("[MinigamesBox] Something went horribly wrong with migration! Please contact Plugily Projects!");
    }
  }

  /**
   * Insert text after specified string
   *
   * @param file   file to use
   * @param search string to check
   * @param text   text to insert after search string
   */
  public static void insertAfterLine(File file, String search, String text) {
    try {
      int i = 1;
      List<String> lines = Files.readAllLines(file.toPath());
      for(String line : lines) {
        if(line.contains(search)) {
          lines.add(i, text);
          Files.write(file.toPath(), lines);
          break;
        }
        i++;
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds new lines to file
   *
   * @param file     file to use
   * @param newLines new lines to add
   */
  public static void addNewLines(File file, String newLines) {
    try {
      FileWriter fw = new FileWriter(file.getPath(), true);
      fw.write(newLines);
      fw.close();
    } catch(IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().warning("[MinigamesBox] Something went horribly wrong with migration! Please contact Plugily Projects!");
    }
  }

}
