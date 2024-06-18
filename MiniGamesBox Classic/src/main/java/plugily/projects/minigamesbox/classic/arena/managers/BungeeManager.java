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

package plugily.projects.minigamesbox.classic.arena.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class BungeeManager implements Listener {

  private final PluginMain plugin;
  private final FileConfiguration config;
  private final Map<IArenaState, String> motd = new EnumMap<>(IArenaState.class);

  public BungeeManager(PluginMain plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "bungee");

    for(IArenaState arenaState : IArenaState.values()) {
      motd.put(arenaState, plugin.getLanguageManager().getLanguageMessage("Placeholders.Motd." + arenaState.getFormattedName()));
    }

    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void connectToHub(Player player) {
    if(!config.getBoolean("Connect-To-Hub", true)) {
      return;
    }
    String serverName = config.getString("Hub");
    plugin.getDebugger().debug(Level.INFO, "Server name that we try to connect {0} ({1})", serverName, player.getName());
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(serverName);
    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerListPing(ServerListPingEvent event) {
    if(plugin.getArenaRegistry().getArenas().isEmpty() || !config.getBoolean("MOTD.Manager")) {
      return;
    }
    IPluginArena arena = plugin.getArenaRegistry().getArenas().get(plugin.getArenaRegistry().getBungeeArena());
    event.setMaxPlayers(arena.getMaximumPlayers());
    ComplementAccessor.getComplement().setMotd(event, new MessageBuilder(motd.get(arena.getArenaState())).arena(arena).build());
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    ComplementAccessor.getComplement().setJoinMessage(event, "");
    if(!plugin.getArenaRegistry().getArenas().isEmpty()) {
      plugin.getArenaManager().joinAttempt(event.getPlayer(), plugin.getArenaRegistry().getArenas().get(plugin.getArenaRegistry().getBungeeArena()));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    ComplementAccessor.getComplement().setQuitMessage(event, "");
    if(!plugin.getArenaRegistry().getArenas().isEmpty() && plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      plugin.getArenaManager().leaveAttempt(event.getPlayer(), plugin.getArenaRegistry().getArenas().get(plugin.getArenaRegistry().getBungeeArena()));
    }

  }

}
