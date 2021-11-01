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

package plugily.projects.minigamesbox.classic.events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.user.User;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class ChatEvents implements Listener {

  private final Main plugin;
  private final String[] regexChars = {"$", "\\"};

  public ChatEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChatIngame(AsyncPlayerChatEvent event) {
    Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      if(!plugin.getConfigPreferences().getOption("SEPARATE_ARENA_CHAT")) {
        for(Arena loopArena : plugin.getArenaRegistry().getArenas()) {
          for(Player player : loopArena.getPlayers()) {
            if(!plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player)) {
              event.getRecipients().remove(player);
            }
          }
        }
      }
      return;
    }
    if(plugin.getConfigPreferences().getOption("PLUGIN_CHAT_FORMAT")) {
      String eventMessage = event.getMessage();
      for(String regexChar : regexChars) {
        if(eventMessage.contains(regexChar)) {
          eventMessage = eventMessage.replaceAll(Pattern.quote(regexChar), "");
        }
      }
      event.setMessage(ChatColor.stripColor(eventMessage));

      String format = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format"), plugin.getUserManager().getUser(event.getPlayer()));
      event.setFormat(format);
    }
    if(!plugin.getConfigPreferences().getOption("SEPARATE_ARENA_CHAT")) {
      event.getRecipients().removeIf(player -> !plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player));
      event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));
    }
  }

  private String formatChatPlaceholders(String message, User user) {
    String formatted = message;
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    formatted = StringUtils.replace(formatted, "%level%", Integer.toString(user.getStat(plugin.getStatsStorage().getStatisticType("LEVEL"))));
    if(user.isSpectator()) {
      formatted = StringUtils.replace(formatted, "%kit%", plugin.getChatManager().colorMessage(Messages.DEAD_TAG_ON_DEATH));
    } else {
      formatted = StringUtils.replace(formatted, "%kit%", user.getKit().getName());
    }

    Player player = user.getPlayer();

    formatted = StringUtils.replace(formatted, "%player%", player.getName());
    formatted = StringUtils.replace(formatted, "%message%", "%2$s");
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formatted = PlaceholderAPI.setPlaceholders(player, formatted);
    }
    return formatted;
  }

}
