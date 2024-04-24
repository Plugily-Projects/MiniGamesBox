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

package plugily.projects.minigamesbox.classic.events;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.ArrayList;

/**
 * @author Tigerpanzer_02
 * <p>Created at 09.10.2021
 */
public class ChatEvents implements Listener {

  private final PluginMain plugin;

  public ChatEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChatIngame(AsyncPlayerChatEvent event) {
    IPluginArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(plugin.getConfigPreferences().getOption("SEPARATE_ARENA_CHAT")) {
      if(arena == null) {
        for(IPluginArena loopArena : plugin.getArenaRegistry().getArenas()) {
          for(Player player : loopArena.getPlayers()) {
            if(!plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player)) {
              event.getRecipients().remove(player);
            }
          }
        }
        return;
      }
      event.getRecipients().removeIf(player -> !plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player));
      event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));
      if(plugin.getConfigPreferences().getOption("SEPARATE_ARENA_SPECTATORS")) {
        if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
          event.getRecipients().removeIf(player -> arena.getPlayersLeft().contains(player));
        } else {
          event.getRecipients().removeIf(player -> !arena.getPlayersLeft().contains(player));
        }
      }
    } else if(plugin.getConfigPreferences().getOption("SEPARATE_ARENA_SPECTATORS")) {
      for(IPluginArena loopArena : plugin.getArenaRegistry().getArenas()) {
        if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
          event.getRecipients().removeIf(player -> loopArena.getPlayersLeft().contains(player));
        } else {
          event.getRecipients().removeIf(player -> !loopArena.getPlayersLeft().contains(player));
        }
      }
    }
    if(plugin.getConfigPreferences().getOption("PLUGIN_CHAT_FORMAT")) {
      String format = formatChatPlaceholders(plugin.getUserManager().getUser(event.getPlayer()), arena);
      event.setFormat(format);
      event.setMessage(event.getMessage());
    }
  }

  private String formatChatPlaceholders(IUser user, IPluginArena arena) {
    String formatted = new MessageBuilder("IN_GAME_GAME_CHAT_FORMAT").asKey().getRaw();
    if(user.isSpectator()) {
      if(formatted.contains("%kit%")) {
        formatted =
            StringUtils.replace(
                formatted, "%kit%", new MessageBuilder("IN_GAME_DEATH_TAG").asKey().build());
      } else {
        formatted = new MessageBuilder("IN_GAME_DEATH_TAG").asKey().build() + formatted;
      }
    } else {
      if(!plugin.getConfigPreferences().getOption("KITS")) {
        formatted = StringUtils.replace(formatted, "%kit%", "-");
      } else {
        formatted = StringUtils.replace(formatted, "%kit%", user.getKit().getName());
      }
    }
    formatted = StringUtils.replace(formatted, "%player%", "%1$s");
    formatted = StringUtils.replace(formatted, "%message%", "%2$s");
    formatted = new MessageBuilder(formatted).arena(arena).player(user.getPlayer()).build();
    // notice - unresolved % could throw UnknownFormatException
    return formatted;
  }
}
