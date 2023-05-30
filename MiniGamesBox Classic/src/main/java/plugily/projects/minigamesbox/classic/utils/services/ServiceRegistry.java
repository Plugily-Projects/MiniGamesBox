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

package plugily.projects.minigamesbox.classic.utils.services;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleService;
import plugily.projects.minigamesbox.classic.utils.services.metrics.MetricsService;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
/**
 * Class for registering new services
 */
public class ServiceRegistry {

  private static PluginMain registeredService;
  private static boolean serviceEnabled;
  private static long serviceCooldown = 0;
  private static LocaleService localeService;

  public static boolean registerService(PluginMain plugin) {
    if(registeredService != null && registeredService.equals(plugin)) {
      return false;
    }
    plugin.getLogger().log(Level.INFO, "Connecting to services, please wait! Server may freeze a bit!");
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.plugily.xyz/ping.php").openConnection();
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(2000);
      connection.setRequestMethod("HEAD");
      connection.setRequestProperty("User-Agent", "PLService/1.0");
      int responseCode = connection.getResponseCode();
      if(responseCode != 200) {
        plugin.getLogger().log(Level.WARNING, "Plugily Projects services aren't online or inaccessible from your location! Response: {0}. Do you think it's site problem? Contact developer! Make sure " +
            "Cloudflare isn't blocked in your area!", responseCode);
        serviceEnabled = false;
        return false;
      }
    } catch(IOException ignored) {
      plugin.getLogger().log(Level.WARNING, "Plugily Projects services aren't online or inaccessible from your location!");
      serviceEnabled = false;
      return false;
    }
    registeredService = plugin;
    serviceEnabled = true;
    plugin.getLogger().log(Level.INFO, "Hooked with ServiceRegistry! Initialized services properly!");
    new MetricsService(plugin);
    localeService = new LocaleService(plugin);
    return true;
  }

  public static JavaPlugin getRegisteredService() {
    return registeredService;
  }

  public static long getServiceCooldown() {
    return serviceCooldown;
  }

  public static void setServiceCooldown(long serviceCooldown) {
    ServiceRegistry.serviceCooldown = serviceCooldown;
  }

  public static LocaleService getLocaleService(JavaPlugin plugin) {
    if(!serviceEnabled || registeredService == null || !registeredService.equals(plugin)) {
      return null;
    }
    return localeService;
  }

  public static boolean isServiceEnabled() {
    return serviceEnabled;
  }
}
