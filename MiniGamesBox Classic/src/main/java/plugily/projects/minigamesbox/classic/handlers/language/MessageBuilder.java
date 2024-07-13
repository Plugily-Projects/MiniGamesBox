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

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.states.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.string.StringFormatUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.02.2022
 */
public class MessageBuilder {

  private String placeholderColorValue = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_PLACEHOLDER_VALUE"));
  private String placeholderColorNumber = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_PLACEHOLDER_NUMBER"));
  private String placeholderColorPlayer = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_PLACEHOLDER_PLAYER"));
  private String placeholderColorOther = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_PLACEHOLDER_OTHER"));
  private String messageColor = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_CHAT_MESSAGES"));
  private String messageIssueColor = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_CHAT_ISSUE"));
  private String messageSpecialCharBefore = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("COLOR_CHAT_SPECIAL_BEFORE"));
  private String message;
  private Player player;
  private String value;
  private int integer;
  private IPluginArena arena;
  private static PluginMain plugin;

  public static void init(PluginMain plugin) {
    MessageBuilder.plugin = plugin;
  }

  public MessageBuilder(String message) {
    if(message == null) {
      message = "";
    }
    this.message = message;
    colorChatIssue();
  }

  public MessageBuilder(String message, boolean autoColor) {
    if(message == null) {
      message = "";
    }
    this.message = message;
    if(!autoColor) {
      this.placeholderColorValue = "";
      this.placeholderColorNumber = "";
      this.placeholderColorPlayer = "";
      this.placeholderColorOther = "";
      this.messageColor = "";
      this.messageIssueColor = "";
      this.messageSpecialCharBefore = "";
    }
    colorChatIssue();
  }

  public MessageBuilder(@NotNull ActionType actionType) {
    this.message = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getMessage("IN_GAME_MESSAGES_" + actionType).getPath());
    colorChatIssue();
  }

  public MessageBuilder(@NotNull Message message) {
    this.message = plugin.getLanguageManager().getLanguageMessage(message.getPath());
    colorChatIssue();
  }

  private void colorChatIssue() {
    if(message.contains("%color_chat_issue%")) {
      messageColor = messageIssueColor;
    }

    message = replace(message, "%color_chat_issue%", () -> messageIssueColor);
  }

  public MessageBuilder asKey() {
    message = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath(message));
    return this;
  }

  public MessageBuilder prefix() {
    message = placeholderColorOther + "%plugin_prefix% " + messageColor + message;
    return this;
  }

  public MessageBuilder prefix(String prefix) {
    message = prefix + messageColor + message;
    return this;
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

  public MessageBuilder arena(IPluginArena arena) {
    this.arena = arena;
    formatArena();
    return this;
  }

  private void formatSpecialChars() {
    for(String specialChar : plugin.getMessageManager().getSpecialChars()) {
      message = replace(message, specialChar, () -> messageSpecialCharBefore + specialChar + messageColor);
    }
  }

  private void colorRawMessage() {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16) && message.indexOf('#') != -1) {
      message = MiscUtils.matchColorRegex(message);
    }

    message = ChatColor.translateAlternateColorCodes('&', message);
  }

  private void formatInteger() {
    message = replace(message, "%number%", () -> placeholderColorNumber + integer + messageColor);
  }

  private void formatValue() {
    message = replace(message, "%value%", () -> placeholderColorValue + value + messageColor);
  }

  private void formatPlayer() {
    message = replace(message, "%player%", () -> placeholderColorPlayer + player.getName() + messageColor);
    message = replace(message, "%player_uuid%", () -> placeholderColorPlayer + player.getUniqueId() + messageColor);
  }

  private void formatPlaceholderAPI() {
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      message = PlaceholderAPI.setPlaceholders(player, message);
    }
  }

  private void formatExternalPlayer() {
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() != Placeholder.PlaceholderType.ARENA) {
        message = replace(message, "%" + placeholder.getId() + "%", () -> placeholderColorOther + placeholder.getValue(player) + messageColor);
      }
    }
  }

  private void formatExternalArena() {
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() != Placeholder.PlaceholderType.GLOBAL) {
        message = replace(message, "%" + placeholder.getId() + "%", () -> placeholderColorOther + placeholder.getValue(arena) + messageColor);
      }
    }
  }

  private void formatExternalPlayerAndArena() {
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredInternalPlaceholders()) {
      if(placeholder.getPlaceholderType() != Placeholder.PlaceholderType.GLOBAL) {
        message = replace(message, "%" + placeholder.getId() + "%", () -> placeholderColorOther + placeholder.getValue(player, arena) + messageColor);
      }
    }
  }

  private void formatPlugin() {
    message = replace(message, "%plugin_prefix%", () -> placeholderColorOther + plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("IN_GAME_PLUGIN_PREFIX")) + messageColor);
    message = replace(message, "%plugin_name%", () -> placeholderColorOther + plugin.getName() + messageColor);
    message = replace(message, "%plugin_name_uppercase%", () -> placeholderColorOther + plugin.getName().toUpperCase() + messageColor);
    message = replace(message, "%plugin_short_command%", () -> placeholderColorOther + plugin.getPluginNamePrefix() + messageColor);
  }

  private void formatArena() {
    message = replace(message, "%arena_min_players%", () -> placeholderColorOther + arena.getMinimumPlayers() + messageColor);
    message = replace(message, "%arena_players%", () -> placeholderColorOther + arena.getPlayers() + messageColor);
    message = replace(message, "%arena_players_size%", () -> placeholderColorOther + arena.getPlayers().size() + messageColor);
    message = replace(message, "%arena_players_left%", () -> placeholderColorOther + arena.getPlayersLeft() + messageColor);
    message = replace(message, "%arena_players_left_size%", () -> placeholderColorOther + arena.getPlayersLeft().size() + messageColor);
    message = replace(message, "%arena_max_players%", () -> placeholderColorOther + arena.getMaximumPlayers() + messageColor);
    message = replace(message, "%arena_name%", () -> placeholderColorOther + arena.getMapName() + messageColor);
    message = replace(message, "%arena_id%", () -> placeholderColorOther + arena.getId() + messageColor);
    message = replace(message, "%arena_state%", () -> placeholderColorOther + arena.getArenaState() + messageColor);
    message = replace(message, "%arena_state_formatted%", () -> placeholderColorOther + arena.getArenaState().getFormattedName() + messageColor);
    message = replace(message, "%arena_state_placeholder%", () -> placeholderColorOther + ArenaState.getPlaceholder(arena.getArenaState()) + messageColor);
    message = replace(message, "%arena_time%", () -> placeholderColorOther + arena.getTimer() + messageColor);
    message = replace(message, "%arena_time_formatted%", () -> placeholderColorOther + StringFormatUtils.formatIntoMMSS(arena.getTimer()) + messageColor);
  }

  private String replace(String text, String search, java.util.function.Supplier<String> replacement) {
    int index = text.indexOf(search);

    if(index == -1) {
      return text;
    }

    StringBuilder builder = new StringBuilder(text);
    int searchLength = search.length();
    int replacementLength = replacement.get().length();

    while((index = builder.replace(index, index + searchLength, replacement.get()).indexOf(search, index += replacementLength)) != -1) {
    }

    return builder.toString();
  }

  public String build() {
    colorChatIssue();
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
    formatSpecialChars();
    message = messageColor + message;
    colorRawMessage();
    return message;
  }

  public String getRaw() {
    return message;
  }

  public void sendPlayer() {
    if(player != null) {
      send(player);
    }
  }

  public void send(CommandSender commandSender) {
    build();
    if(message != null && !message.isEmpty()) {
      commandSender.sendMessage(message);
    }
  }

  public void send(IPluginArena arena) {
    build();
    if(message != null && !message.isEmpty()) {
      for(Player arenaPlayer : arena.getPlayers()) {
        arenaPlayer.sendMessage(message);
      }
    }
  }

  public void sendArena() {
    if(arena != null) {
      send(arena);
    }
  }

  public void broadcast() {
    build();
    if(message != null && arena != null && !message.isEmpty()) {
      for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(message);
      }
    }
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

  public String getMessageColor() {
    return messageColor;
  }

  public String getMessageIssueColor() {
    return messageIssueColor;
  }

  public String getMessageSpecialCharBefore() {
    return messageSpecialCharBefore;
  }

  public String getPlaceholderColorNumber() {
    return placeholderColorNumber;
  }

  public String getPlaceholderColorOther() {
    return placeholderColorOther;
  }

  public String getPlaceholderColorPlayer() {
    return placeholderColorPlayer;
  }

  public String getPlaceholderColorValue() {
    return placeholderColorValue;
  }
}
