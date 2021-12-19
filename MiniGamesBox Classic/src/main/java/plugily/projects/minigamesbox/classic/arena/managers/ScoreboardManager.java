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

package plugily.projects.minigamesbox.classic.arena.managers;

import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class ScoreboardManager {

  private final Map<UUID, Scoreboard> boardMap = new ConcurrentHashMap<>();
  private final Map<UUID, org.bukkit.scoreboard.Scoreboard> lastBoardMap = new ConcurrentHashMap<>();
  private final org.bukkit.scoreboard.Scoreboard dummyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
  private final PluginMain plugin;
  private final String boardTitle;
  private final PluginArena arena;

  public ScoreboardManager(PluginArena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    this.boardTitle = plugin.getChatManager().colorMessage("SCOREBOARD_TITLE");
  }

  /**
   * Creates arena scoreboard for target user
   *
   * @param user user that represents game player
   * @see User
   */
  public void createScoreboard(User user) {
    Player player = user.getPlayer();
    lastBoardMap.put(player.getUniqueId(), player.getScoreboard());
    player.setScoreboard(dummyBoard);

    Scoreboard scoreboard = ScoreboardLib.createScoreboard(player).setHandler(new ScoreboardHandler() {
      @Override
      public String getTitle(Player player) {
        return boardTitle;
      }

      @Override
      public List<Entry> getEntries(Player player) {
        return formatScoreboard(user);
      }
    });
    scoreboard.activate();
    boardMap.put(player.getUniqueId(), scoreboard);
  }

  /**
   * Removes scoreboard of user
   *
   * @param user user that represents game player
   * @see User
   */
  public void removeScoreboard(User user) {
    Optional.ofNullable(boardMap.remove(user.getUniqueId())).ifPresent(Scoreboard::deactivate);
    Optional.ofNullable(lastBoardMap.remove(user.getUniqueId())).ifPresent(user.getPlayer()::setScoreboard);
  }

  /**
   * Forces all scoreboards to deactivate.
   */
  public void stopAllScoreboards() {
    boardMap.values().forEach(Scoreboard::deactivate);
    boardMap.clear();
  }

  private List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if(arena.getArenaState() == ArenaState.FULL_GAME) {
      lines = plugin.getLanguageManager().getLanguageList("Scoreboard.Content.In-Game");
    } else {
      lines = plugin.getLanguageManager().getLanguageList("Scoreboard.Content." + arena.getArenaState().getFormattedName());
    }
    for(String line : lines) {
      builder.next(formatScoreboardLine(line, user));
    }
    return builder.build();
  }

  private String formatScoreboardLine(String line, User user) {
    String formattedLine = line;
    formattedLine = plugin.getChatManager().formatMessage(arena, formattedLine, user.getPlayer());
    return formattedLine;
  }

}
