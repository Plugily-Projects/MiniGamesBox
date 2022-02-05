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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import static org.apache.commons.lang.StringUtils.replace;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.02.2022
 */
public class MessageBuilder {

  private String message;
  private String prefix = "";
  private Player player;
  private String value;
  private int integer;
  private PluginArena arena;
  private static PluginMain plugin;

  public static void init(PluginMain plugin) {
    MessageBuilder.plugin = plugin;
  }

  public MessageBuilder(String message) {
    this.message = message;
  }

  public MessageBuilder(@NotNull ActionType actionType) {
    Message message = plugin.getMessageManager().getMessage("IN_GAME_MESSAGES_" + actionType);
    this.message = plugin.getLanguageManager().getLanguageMessage(message.getPath());
  }

  public MessageBuilder(@NotNull Message message) {
    this.message = plugin.getLanguageManager().getLanguageMessage(message.getPath());
  }

  public MessageBuilder asKey() {
    message = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath(message));
    return this;
  }

  public MessageBuilder prefix() {
    prefix = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("IN_GAME_PLUGIN_PREFIX"));
    colorPrefix();
    return this;
  }

  public MessageBuilder prefix(String prefix) {
    this.prefix = prefix;
    colorPrefix();
    return this;
  }

  private void colorPrefix() {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && prefix.indexOf('#') >= 0) {
      prefix = MiscUtils.matchColorRegex(prefix);
    }

    prefix = ChatColor.translateAlternateColorCodes('&', prefix);
  }

  public MessageBuilder player(Player player) {
    this.player = player;
    formatPlayer();
    return this;
  }

  public MessageBuilder value(String value) {
    this.value = value;
    formatValue();
    return this;
  }

  public MessageBuilder integer(int integer) {
    this.integer = integer;
    formatInteger();
    return this;
  }

  public MessageBuilder arena(PluginArena arena) {
    this.arena = arena;
    formatArena();
    return this;
  }

  public MessageBuilder plugin(PluginMain plugin) {
    MessageBuilder.plugin = plugin;
    return this;
  }

  private void colorRawMessage() {
    if(message == null || message.isEmpty()) {
      message = "";
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && message.indexOf('#') >= 0) {
      message = MiscUtils.matchColorRegex(message);
    }

    message = ChatColor.translateAlternateColorCodes('&', message);
  }

  private void formatInteger() {
    message = replace(message, "%number%", Integer.toString(integer));
  }

  private void formatValue() {
    message = replace(message, "%value%", value);
  }

  private void formatPlayer() {
    if(hasNoPlaceholders()) return;
    message = replace(message, "%player%", player.getName());
    message = replace(message, "%player_uuid%", String.valueOf(player.getUniqueId()));
  }

  private void formatPlaceholderAPI() {
    if(hasNoPlaceholders()) return;
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      message = PlaceholderAPI.setPlaceholders(player, message);
    }
  }

  private void formatExternalPlayer() {
    if(hasNoPlaceholders()) return;
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.ARENA) {
        continue;
      }
      message = replace(message, "%" + placeholder.getId() + "%", placeholder.getValue(player));
    }
  }

  private void formatExternalArena() {
    if(hasNoPlaceholders()) return;
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }
      message = replace(message, "%" + placeholder.getId() + "%", placeholder.getValue(arena));
    }
  }

  private void formatExternalPlayerAndArena() {
    if(hasNoPlaceholders()) return;
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }
      message = replace(message, "%" + placeholder.getId() + "%", placeholder.getValue(player, arena));
    }
  }

  private void formatPlugin() {
    if(hasNoPlaceholders()) return;
    message = replace(message, "%plugin_name%", plugin.getName());
    message = replace(message, "%plugin_name_uppercase%", plugin.getName().toUpperCase());
    message = replace(message, "%plugin_short_command%", plugin.getPluginNamePrefix());
  }

  private void formatArena() {
    if(hasNoPlaceholders()) return;
    int timer = arena.getTimer();
    message = replace(message, "%arena_min_players%", Integer.toString(arena.getMinimumPlayers()));
    message = replace(message, "%arena_players%", String.valueOf(arena.getPlayers()));
    message = replace(message, "%arena_players_size%", Integer.toString(arena.getPlayers().size()));
    message = replace(message, "%arena_players_left%", String.valueOf(arena.getPlayersLeft()));
    message = replace(message, "%arena_players_left_size%", Integer.toString(arena.getPlayersLeft().size()));
    message = replace(message, "%arena_max_players%", Integer.toString(arena.getMaximumPlayers()));
    message = replace(message, "%arena_name%", arena.getMapName());
    message = replace(message, "%arena_id%", arena.getId());
    message = replace(message, "%arena_state%", String.valueOf(arena.getArenaState()));
    message = replace(message, "%arena_state_formatted%", arena.getArenaState().getFormattedName());
    message = replace(message, "%arena_state_placeholder%", arena.getArenaState().getPlaceholder());
    message = replace(message, "%arena_time%", Integer.toString(timer));
    message = replace(message, "%arena_formatted_time%", StringFormatUtils.formatIntoMMSS(timer));
  }

  private boolean hasNoPlaceholders() {
    return !message.contains("%");
  }

  public String build() {
    colorRawMessage();
    formatPlugin();
    formatPlaceholderAPI();
    if(player != null && arena != null) {
      formatExternalPlayerAndArena();
    }
    if(player != null) {
      formatExternalPlayer();
    }
    if(arena != null) {
      formatExternalArena();
    }
    return prefix + message;
  }

  public void sendPlayer() {
    build();
    if((message != null) && !message.isEmpty() && player != null) {
      player.sendMessage(prefix + message);
    }
  }

  public void send(CommandSender commandSender) {
    build();
    if((message != null) && !message.isEmpty()) {
      commandSender.sendMessage(prefix + message);
    }
  }

  public void send(Player player) {
    build();
    if((message != null) && !message.isEmpty()) {
      player.sendMessage(prefix + message);
    }
  }

  public void send(PluginArena arena) {
    build();
    if((message != null) && !message.isEmpty()) {
      for(Player arenaPlayer : arena.getPlayers()) {
        arenaPlayer.sendMessage(prefix + message);
      }
    }
  }

  public void sendArena() {
    build();
    if((message != null) && !message.isEmpty() && (arena != null)) {
      for(Player arenaPlayer : arena.getPlayers()) {
        arenaPlayer.sendMessage(prefix + message);
      }
    }
  }

  public void broadcast() {
    build();
    if((message != null) && !message.isEmpty() && (arena != null)) {
      for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(prefix + message);
      }
    }
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }
}
