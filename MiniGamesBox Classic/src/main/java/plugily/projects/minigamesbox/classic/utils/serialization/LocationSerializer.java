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

package plugily.projects.minigamesbox.classic.utils.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class LocationSerializer {

  private LocationSerializer() {
  }

  /**
   * Saves location in the file
   *
   * @param plugin   plugin to get data folder from
   * @param file     file object where to save
   * @param fileName file name
   * @param path     path where location will be saved
   * @param loc      location to save
   */
  public static void saveLoc(JavaPlugin plugin, FileConfiguration file, String fileName, String path, Location loc) {
    final DecimalFormat roundFormat = new DecimalFormat("0.00");
    String location = loc.getWorld().getName() + "," + roundFormat.format(loc.getX()) + "," + roundFormat.format(loc.getY()) + "," + roundFormat.format(loc.getZ()) + "," + roundFormat.format(loc.getYaw()) + "," + roundFormat.format(loc.getPitch());
    file.set(path, location);
    try {
      file.save(new File(plugin.getDataFolder(), fileName + ".yml"));
    } catch(IOException e) {
      e.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Cannot save file " + fileName + ".yml!");
      Bukkit.getConsoleSender().sendMessage("Create blank file " + fileName + ".yml or restart the server!");
    }
  }

  /**
   * Retrieves location from given string
   *
   * @param path location as a string, you can get it by #saveLoc method
   * @return full location (world, x, y, z, yaw, pitch) also without yaw and pitch if not provided in path param
   * @see #saveLoc(JavaPlugin, FileConfiguration, String, String, Location)
   */
  public static Location getLocation(String path) {
    String[] loc;
    if(path == null || (loc = path.split(",")).length == 0) {
      return null;
    }

    World world = Bukkit.getServer().getWorld(loc[0]);
    if(world == null && !Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
      world = Bukkit.createWorld(new WorldCreator(loc[0]));
    }

    if(loc.length == 1) {
      return null;
    }

    double x = Double.parseDouble(loc[1]);
    double y = Double.parseDouble(loc[2]);
    double z = Double.parseDouble(loc[3]);

    float yaw = loc.length > 4 ? Float.parseFloat(loc[4]) : 0f;
    float pitch = loc.length > 5 ? Float.parseFloat(loc[5]) : 0f;

    return new Location(world, x, y, z, yaw, pitch);
  }

  /**
   * Save location to string ex world,10,20,30
   *
   * @param location location to string
   * @return location saved to string
   */
  public static String locationToString(Location location) {
    if(location == null) {
      return "null";
    }

    World world = location.getWorld();
    return (world == null ? "null" : world.getName()) + "," + location.getX() + "," + location.getY() + "," + location.getZ();
  }

}
