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

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class ChatManager {

  private final String pluginPrefix;
  private final PluginMain plugin;

  public ChatManager(PluginMain plugin) {
    this.plugin = plugin;
    this.pluginPrefix = colorMessage("IN_GAME_PLUGIN_PREFIX");
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return pluginPrefix;
  }

  public String colorMessage(String key) {
    return colorRawMessage(plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath(key)));
  }

  public String colorMessage(Message message) {
    return colorRawMessage(plugin.getLanguageManager().getLanguageMessage(message.getPath()));
  }

  public void sendMessage(Message message, Player player, String prefix) {
    String colorMessage = colorMessage(message);
    if(colorMessage != null && !colorMessage.isEmpty()) {
      player.sendMessage(prefix + colorMessage);
    }
  }

  public void sendMessage(Message message, Player player) {
    String colorMessage = colorMessage(message);
    if(colorMessage != null && !colorMessage.isEmpty()) {
      player.sendMessage(pluginPrefix + colorMessage);
    }
  }

  public String colorRawMessage(String message) {
    if(message == null || message.isEmpty()) {
      return "";
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && message.indexOf('#') >= 0) {
      message = MiscUtils.matchColorRegex(message);
    }

    message = formatMessage(message);

    return ChatColor.translateAlternateColorCodes('&', message);
  }

  /**
   * Broadcasts constant message to all players in arena
   * Includes game prefix!
   *
   * @param arena arena to get players from
   * @param key   message to broadcast
   */
  public void broadcast(PluginArena arena, String key) {
    String colorMessage = colorMessage(key);
    if(colorMessage != null && !colorMessage.isEmpty()) {
      for(Player p : arena.getPlayers()) {
        p.sendMessage(pluginPrefix + colorMessage);
      }
    }
  }

  /**
   * Broadcasts message to all players in arena
   * Includes game prefix!
   *
   * @param arena   arena to get players from
   * @param message message to broadcast
   */
  public void broadcastMessage(PluginArena arena, String message) {
    if(message != null && !message.isEmpty()) {
      for(Player p : arena.getPlayers()) {
        p.sendMessage(pluginPrefix + message);
      }
    }
  }

  public String formatMessage(String message) {
    String returnString = message;
    returnString = colorRawMessage(formatPlaceholders(returnString));
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(null, returnString);
    }
    return returnString;
  }

  public String formatMessage(String message, PluginArena arena) {
    String returnString = message;
    returnString = formatExternalPlaceholders(returnString, arena);
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%number%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString));
    return returnString;
  }

  public String formatMessage(PluginArena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%number%", Integer.toString(integer));
    returnString = formatExternalPlaceholders(returnString, arena);
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(PluginArena arena, String message, int integer, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%player%", player.getName());
    returnString = formatExternalPlaceholders(returnString, player, arena);
    returnString = StringUtils.replace(returnString, "%number%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(PluginArena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%player%", player.getName());
    returnString = formatExternalPlaceholders(returnString, player, arena);
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return returnString;
  }

  private String formatExternalPlaceholders(String returnString, Player player) {
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.ARENA) {
        continue;
      }
      returnString = StringUtils.replace(returnString, "%" + placeholder.getId() + "%", placeholder.getValue(player));
    }
    return returnString;
  }

  private String formatExternalPlaceholders(String returnString, PluginArena arena) {
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }
      returnString = StringUtils.replace(returnString, "%" + placeholder.getId() + "%", placeholder.getValue(arena));
    }
    return returnString;
  }

  private String formatExternalPlaceholders(String returnString, Player player, PluginArena arena) {
    returnString = formatExternalPlaceholders(returnString, player);
    returnString = formatExternalPlaceholders(returnString, arena);
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }
      returnString = StringUtils.replace(returnString, "%" + placeholder.getId() + "%", placeholder.getValue(player, arena));
    }
    return returnString;
  }

  private String formatPlaceholders(String message) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%plugin_name%", plugin.getName());
    returnString = StringUtils.replace(returnString, "%plugin_name_uppercase%", plugin.getName().toUpperCase());
    returnString = StringUtils.replace(returnString, "%plugin_short_command%", plugin.getPluginNamePrefix());
    return returnString;
  }

  private String formatPlaceholders(String message, PluginArena arena) {
    int timer = arena.getTimer();
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%plugin_name%", plugin.getName());
    returnString = StringUtils.replace(returnString, "%plugin_name_uppercase%", plugin.getName().toUpperCase());
    returnString = StringUtils.replace(returnString, "%plugin_short_command%", plugin.getPluginNamePrefix());
    returnString = StringUtils.replace(returnString, "%arena_min_players%", Integer.toString(arena.getMinimumPlayers()));
    returnString = StringUtils.replace(returnString, "%arena_players%", String.valueOf(arena.getPlayers()));
    returnString = StringUtils.replace(returnString, "%arena_max_players%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%arena_players_size%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%arena_name%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%arena_id%", arena.getId());
    returnString = StringUtils.replace(returnString, "%arena_state%", String.valueOf(arena.getArenaState()));
    returnString = StringUtils.replace(returnString, "%arena_state_formatted%", arena.getArenaState().getFormattedName());
    returnString = StringUtils.replace(returnString, "%arena_state_placeholder%", arena.getArenaState().getPlaceholder());
    returnString = StringUtils.replace(returnString, "%arena_time%", Integer.toString(timer));
    returnString = StringUtils.replace(returnString, "%arena_formatted_time%", StringFormatUtils.formatIntoMMSS(timer));
    return returnString;
  }

  public void broadcastAction(PluginArena arena, Player player, ActionType action) {
    Message message = plugin.getMessageManager().getMessage("IN_GAME_MESSAGES_" + action.toString());
    broadcastMessage(arena, formatMessage(arena, colorMessage(message), player));
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}

