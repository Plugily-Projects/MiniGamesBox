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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 18.09.2021
 */
public class Message {

  private static final Map<String, Message> messages = new HashMap<>();

  // GLOBAL LANGUAGE PLACEHOLDERS
  // %plugin_name% - Returns plugin name
  // %plugin_name_uppercase% - Returns uppercase plugin name
  // %arena_min_players% - Players needed to start arena
  // %arena_players%% - Current players list inside arena
  // %arena_max_players% - Max players who can play on the arena
  // %arena_name% - Arena name
  // %arena_id% - Arena id
  // %arena_state% - Arena state
  // %arena_state_formatted% - Formatted arena state
  // %arena_state_placeholder% - Placeholder arena state like language.yml defined
  // %plugin_short_command% - Shorten plugin command string
  // %arena_players_size% - Arena players size
  // %arena_time% - Arena specified timer
  // %arena_formatted_time% - Formatted timer
  // %player% - The player in context, mostly executed player
  static {
    // color
    messages.put("COLOR_PLACEHOLDER_VALUE", new Message("Color.Placeholder.Value", "", true));
    messages.put("COLOR_PLACEHOLDER_NUMBER", new Message("Color.Placeholder.Number", "", true));
    messages.put("COLOR_PLACEHOLDER_PLAYER", new Message("Color.Placeholder.Player", "", true));
    messages.put("COLOR_PLACEHOLDER_OTHER", new Message("Color.Placeholder.Other", "", true));
    messages.put("COLOR_CHAT_ISSUE", new Message("Color.Chat.Issue", "", true));
    messages.put("COLOR_CHAT_MESSAGES", new Message("Color.Chat.Messages", "", true));
    messages.put("COLOR_CHAT_SPECIAL_CONTAINS", new Message("Color.Chat.Special-Char.Contains", "", true));
    messages.put("COLOR_CHAT_SPECIAL_BEFORE", new Message("Color.Chat.Special-Char.Before", "", true));
    // commands
    messages.put("COMMANDS_DID_YOU_MEAN", new Message("Commands.Did-You-Mean", "", true));
    messages.put("COMMANDS_COMMAND_EXECUTED", new Message("Commands.Command-Executed", "", true));
    messages.put("COMMANDS_TELEPORTED_TO_LOBBY", new Message("Commands.Teleported-To-Lobby", "", true));
    messages.put("COMMANDS_REMOVED_GAME_INSTANCE", new Message("Commands.Removed-Game-Instance", "", true));
    messages.put("COMMANDS_NO_ARENA_LIKE_THAT", new Message("Commands.No-Arena-Like-That", "", true));
    messages.put("COMMANDS_LOOK_AT_SIGN", new Message("Commands.Look-At-Sign", "", true));
    messages.put("COMMANDS_TYPE_ARENA_NAME", new Message("Commands.Type-Arena-Name", "", true));
    messages.put("COMMANDS_HOLD_ANY_ITEM", new Message("Commands.Hold-Any-Item", "", true));
    messages.put("COMMANDS_NO_FREE_ARENAS", new Message("Commands.No-Free-Arenas", "", true));
    messages.put("COMMANDS_ONLY_BY_PLAYER", new Message("Commands.Only-By-Player", "", true));
    messages.put("COMMANDS_NOT_PLAYING", new Message("Commands.Not-Playing", "", true));
    messages.put("COMMANDS_NO_PERMISSION", new Message("Commands.No-Permission", "", true));
    messages.put("COMMANDS_PLAYER_NOT_FOUND", new Message("Commands.Player-Not-Found", "", true));
    messages.put("COMMANDS_INVALID_LOCATION_TELEPORT", new Message("Commands.Invalid-Location-Teleport", "", true));
    messages.put("COMMANDS_WRONG_USAGE", new Message("Commands.Wrong-Usage", "", true));
    // commands.admin
    messages.put("COMMANDS_ADMIN_ADJUST_STATISTIC", new Message("Commands.Admin.Adjust-Statistic", "", true));
    messages.put("COMMANDS_ADMIN_RELOAD_SUCCESS", new Message("Commands.Admin.Reload-Success", "", true));
    messages.put("COMMANDS_ADMIN_LIST_HEADER", new Message("Commands.Admin.List.Header", "", true));
    messages.put("COMMANDS_ADMIN_LIST_FORMAT", new Message("Commands.Admin.List.Format", "", true));
    messages.put("COMMANDS_ADMIN_LIST_NO_ARENAS", new Message("Commands.Admin.List.No-Arenas", "", true));
    messages.put("COMMANDS_ADMIN_SPYCHAT_TOGGLE", new Message("Commands.Admin.Spychat.Toggled", "", true));
    // commands.main
    messages.put("COMMANDS_MAIN_HEADER", new Message("Commands.Main.Header", "", true));
    messages.put("COMMANDS_MAIN_DESCRIPTION", new Message("Commands.Main.Description", "", true));
    messages.put("COMMANDS_MAIN_ADMIN_BONUS_DESCRIPTION", new Message("Commands.Main.Admin-Bonus-Description", "", true));
    messages.put("COMMANDS_MAIN_FOOTER", new Message("Commands.Main.Footer", "", true));

    // scoreboard (scoreboard.content gets by arena state)
    messages.put("SCOREBOARD_TITLE", new Message("Scoreboard.Title", "", true));
    // bossbar (bossbar.content gets by arena state)
    messages.put("BOSSBAR_TITLE", new Message("Bossbar.", "", true));
    // In-Game messages
    messages.put("IN_GAME_PLUGIN_PREFIX", new Message("In-Game.Plugin-Prefix", "", true));
    messages.put("IN_GAME_GAME_CHAT_FORMAT", new Message("In-Game.Game-Chat-Format", "", true));
    messages.put("IN_GAME_LEVEL_UP", new Message("In-Game.You-Leveled-Up", "", true));
    messages.put("IN_GAME_COMMANDS_BLOCKED", new Message("In-Game.Commands-Blocked", "", true));
    // in-game.join
    messages.put("IN_GAME_JOIN_ALREADY_PLAYING", new Message("In-Game.Join.Already-Playing", "", true));
    messages.put("IN_GAME_JOIN_NO_PERMISSION", new Message("In-Game.Join.No-Permission", "", true));
    messages.put("IN_GAME_JOIN_FULL_GAME", new Message("In-Game.Join.Full-Game", "", true));
    messages.put("IN_GAME_JOIN_NO_SLOTS_FOR_PREMIUM", new Message("In-Game.Join.No-Slots-For-Premium", "", true));
    messages.put("IN_GAME_JOIN_CANCEL_API", new Message("In-Game.Join.Cancelled-Via-API", "", true));
    messages.put("IN_GAME_JOIN_AS_PARTY_MEMBER", new Message("In-Game.Join.As-Party-Member", "", true));
    messages.put("IN_GAME_JOIN_ARENA_NOT_CONFIGURED", new Message("In-Game.Join.Arena-Not-Configured", "", true));
    messages.put("IN_GAME_JOIN_TITLE", new Message("In-Game.Join.Title", "", true));
    // in-game.death
    messages.put("IN_GAME_DEATH_TAG", new Message("In-Game.Death.Tag", "", true));
    messages.put("IN_GAME_DEATH_SCREEN", new Message("In-Game.Death.Screen", "", true));
    // in-game.spectator
    messages.put("IN_GAME_SPECTATOR_YOU_ARE_SPECTATOR", new Message("In-Game.Spectator.You-Are-Spectator", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_NAME", new Message("In-Game.Spectator.Spectator-Menu-Name", "", true));
    messages.put("IN_GAME_SPECTATOR_TARGET_PLAYER_HEALTH", new Message("In-Game.Spectator.Target-Player-Health", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_WARNING", new Message("In-Game.Spectator.Spectator-Warning", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_TELEPORT", new Message("In-Game.Spectator.Teleport", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED", new Message("In-Game.Spectator.Menu.Settings.Status.Enabled", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED", new Message("In-Game.Spectator.Menu.Settings.Status.Disabled", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED", new Message("In-Game.Spectator.Menu.Settings.Changed-Speed", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_AUTO_TELEPORT", new Message("In-Game.Spectator.Menu.Settings.Auto-Teleport", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_TARGET_PLAYER_ACTION_BAR", new Message("In-Game.Spectator.Menu.Settings.Target-Player.Action-Bar", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_NIGHT_VISION", new Message("In-Game.Spectator.Menu.Settings.Night-Vision", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_ACTION_BAR", new Message("In-Game.Spectator.Menu.Settings.First-Person-Mode.Action-Bar", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_TITLE", new Message("In-Game.Spectator.Menu.Settings.First-Person-Mode.Title", "", true));
    messages.put("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_VISIBILITY", new Message("In-Game.Spectator.Menu.Settings.Visibility", "", true));
    // in-game.messages
    messages.put("IN_GAME_MESSAGES_JOIN", new Message("In-Game.Messages.Join", "", true));
    messages.put("IN_GAME_MESSAGES_LEAVE", new Message("In-Game.Messages.Leave", "", true));
    messages.put("IN_GAME_MESSAGES_DEATH", new Message("In-Game.Messages.Death", "", true));
    // in-game.messages.lobby
    messages.put("IN_GAME_MESSAGES_LOBBY_START_IN", new Message("In-Game.Messages.Lobby.Start-In", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_WAITING_FOR_PLAYERS", new Message("In-Game.Messages.Lobby.Waiting-For-Players", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_ENOUGH_PLAYERS", new Message("In-Game.Messages.Lobby.Enough-Players-To-Start", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_REDUCED_TIME", new Message("In-Game.Messages.Lobby.Reduced-Time", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_MAX_PLAYERS", new Message("In-Game.Messages.Lobby.Max-Players", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_GAME_START", new Message("In-Game.Messages.Lobby.Game-Started", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_KICKED_FOR_PREMIUM", new Message("In-Game.Messages.Lobby.Kicked-For-Premium-Slot", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_YOU_WERE_KICKED_FOR_PREMIUM", new Message("In-Game.Messages.Lobby.You-Were-Kicked-For-Premium-Slot", "", true));
    messages.put("IN_GAME_MESSAGES_LOBBY_NOT_ENOUGH_SPACE_FOR_PARTY", new Message("In-Game.Messages.Lobby.Not-Enough-Space-For-Party", "", true));
    // in-game.messages.game-end
    messages.put("IN_GAME_MESSAGES_GAME_END_SUMMARY", new Message("In-Game.Messages.Game-End.Summary", "", true));
    messages.put("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN", new Message("In-Game.Messages.Game-End.Placeholders.Win", "", true));
    messages.put("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE", new Message("In-Game.Messages.Game-End.Placeholders.Lose", "", true));
    messages.put("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLAYERS", new Message("In-Game.Messages.Game-End.Placeholders.Players", "", true));
    // in-game.messages.admin
    messages.put("IN_GAME_MESSAGES_ADMIN_FORCESTART", new Message("In-Game.Messages.Admin.Set-Starting-In-To-0", "", true));
    // Signs messages
    messages.put("SIGNS_TYPE_ARENA_NAME", new Message("Signs.Please-Type-Arena-Name", "", true));
    messages.put("SIGNS_ARENA_NOT_FOUND", new Message("Signs.Arena-Doesnt-Exists", "", true));
    messages.put("SIGNS_CREATED", new Message("Signs.Created", "", true));
    messages.put("SIGNS_REMOVED", new Message("Signs.Removed", "", true));
    messages.put("SIGNS_LINES", new Message("Signs.Lines", "", true));
    // Arena-Selector messages
    messages.put("ARENA_SELECTOR_INVENTORY_TITLE", new Message("Arena-Selector.Inventory-Title", "", true));
    messages.put("ARENA_SELECTOR_ITEM_NAME", new Message("Arena-Selector.Item.Name", "", true));
    messages.put("ARENA_SELECTOR_ITEM_LORE", new Message("Arena-Selector.Item.Lore", "", true));
    // Validator
    messages.put("VALIDATOR_INVALID_ARENA_CONFIGURATION", new Message("Validator.Invalid-Arena-Configuration", "", true));
    messages.put("VALIDATOR_INSTANCE_STARTED", new Message("Validator.Instance-Started", "", true));
    messages.put("VALIDATOR_NO_INSTANCES_CREATED", new Message("Validator.No-Instances-Created", "", true));
    // Placeholders
    messages.put("PLACEHOLDERS_GAME_STATES_WAITING", new Message("Placeholders.Game-States.Waiting", "", true));
    messages.put("PLACEHOLDERS_GAME_STATES_STARTING", new Message("Placeholders.Game-States.Starting", "", true));
    messages.put("PLACEHOLDERS_GAME_STATES_FULL_GAME", new Message("Placeholders.Game-States.Full-Game", "", true));
    messages.put("PLACEHOLDERS_GAME_STATES_IN_GAME", new Message("Placeholders.Game-States.In-Game", "", true));
    messages.put("PLACEHOLDERS_GAME_STATES_ENDING", new Message("Placeholders.Game-States.Ending", "", true));
    messages.put("PLACEHOLDERS_GAME_STATES_RESTARTING", new Message("Placeholders.Game-States.Restarting", "", true));
    // Kit
    messages.put("KIT_NOT_UNLOCKED", new Message("Kit.Not-Unlocked", "", false));
    messages.put("KIT_CHOOSE", new Message("Kit.Choose", "", false));
    messages.put("KIT_COOLDOWN", new Message("Kit.Cooldown", "", false));

    // kit.menu
    messages.put("KIT_KIT_MENU_TITLE", new Message("Kit.Menu.Title", "", false));
    // kit.menu.lore
    messages.put("KIT_KIT_MENU_LORE_UNLOCKED", new Message("Kit.Menu.Lore.Unlocked", "", false));
    messages.put("KIT_KIT_MENU_LORE_LOCKED", new Message("Kit.Menu.Lore.Locked", "", false));
    messages.put("KIT_KIT_MENU_LORE_UNLOCK_LEVEL", new Message("Kit.Menu.Lore.Unlock-At-Level", "", false));
    messages.put("KIT_KIT_MENU_LORE_UNLOCK_STORE", new Message("Kit.Menu.Lore.Unlock-In-Store", "", false));
    // Leaderboard
    // Leaderboard.type.hologram
    messages.put("LEADERBOARD_TYPE_HOLOGRAM_HEADER", new Message("Leaderboard.Type.Hologram.Header", "", false));
    messages.put("LEADERBOARD_TYPE_HOLOGRAM_FORMAT", new Message("Leaderboard.Type.Hologram.Format", "", false));
    messages.put("LEADERBOARD_TYPE_HOLOGRAM_EMPTY_FORMAT", new Message("Leaderboard.Type.Hologram.Empty-Format", "", false));

    // Leaderboard.type.chat
    messages.put("LEADERBOARD_TYPE_CHAT_HEADER", new Message("Leaderboard.Type.Chat.Header", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_HEADER_OTHER", new Message("Leaderboard.Type.Chat.Header-Other", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_FOOTER", new Message("Leaderboard.Type.Chat.Footer", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_FORMAT", new Message("Leaderboard.Type.Chat.Format", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_TOP_TYPE_NAME", new Message("Leaderboard.Type.Chat.Top.Type-Name", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_TOP_HEADER", new Message("Leaderboard.Type.Chat.Top.Header", "", true));
    messages.put("LEADERBOARD_TYPE_CHAT_TOP_FORMAT", new Message("Leaderboard.Type.Chat.Top.Format", "", true));

    // Leaderboard.statistics
    messages.put("LEADERBOARD_STATISTICS_GAMES_PLAYED", new Message("Leaderboard.Statistics.Games-Played", "", false));
    messages.put("LEADERBOARD_STATISTICS_LEVEL", new Message("Leaderboard.Statistics.Level", "", false));
    messages.put("LEADERBOARD_STATISTICS_EXP", new Message("Leaderboard.Statistics.Exp", "", false));
    messages.put("LEADERBOARD_STATISTICS_NEXT_LEVEL_EXP", new Message("Leaderboard.Statistics.Next-Level-Exp", "", true));

    messages.put("LEADERBOARD_INVALID_NAME", new Message("Leaderboard.Invalid-Name", "", true));
    messages.put("LEADERBOARD_UNKNOWN_PLAYER", new Message("Leaderboard.Unknown-Player", "", false));


    // File version
    messages.put("FILE_VERSION", new Message("Do-Not-Edit.File-Version", "", true));
    messages.put("CORE_VERSION", new Message("Do-Not-Edit.Core-Version", "", true));
    //empty one
    // messages.put("", new Message("", "", true));
  }

  private final String path;
  private final String value;
  private final boolean protectedOption;


  public Message(String path, String value, boolean protectedOption) {
    this.path = path;
    this.value = value;
    this.protectedOption = protectedOption;
  }

  public Message(String path, String value) {
    this.path = path;
    this.value = value;
    this.protectedOption = false;
  }

  public String getPath() {
    return path;
  }

  /**
   * @return default value of option if absent in config
   */
  public String getValue() {
    return value;
  }

  /**
   * @return whether option is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedOption;
  }

  public static Map<String, Message> getMessages() {
    return Collections.unmodifiableMap(messages);
  }
}
