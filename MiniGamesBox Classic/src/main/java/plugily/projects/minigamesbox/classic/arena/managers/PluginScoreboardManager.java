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

import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.managers.IPluginScoreboardManager;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

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
public class PluginScoreboardManager implements IPluginScoreboardManager {

  private final Map<UUID, Scoreboard> boardMap = new ConcurrentHashMap<>();
  private final Map<UUID, org.bukkit.scoreboard.Scoreboard> lastBoardMap = new ConcurrentHashMap<>();
  private final org.bukkit.scoreboard.Scoreboard dummyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
  private final IPluginMain plugin;
  private final String boardTitle;
  private final PluginArena arena;

  public PluginScoreboardManager(PluginArena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    this.boardTitle = new MessageBuilder("SCOREBOARD_TITLE").asKey().arena(arena).build();
  }

  @Override
  public void createScoreboard(IUser user) {
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

  @Override
  public void updateScoreboards() {
    boardMap.values().forEach(Scoreboard::update);
  }

  @Override
  public void removeScoreboard(IUser user) {
    Optional.ofNullable(boardMap.remove(user.getUniqueId())).ifPresent(Scoreboard::deactivate);
    Optional.ofNullable(lastBoardMap.remove(user.getUniqueId())).ifPresent(user.getPlayer()::setScoreboard);
  }

  @Override
  public void stopAllScoreboards() {
    boardMap.values().forEach(Scoreboard::deactivate);
    boardMap.clear();
  }

  @Override
  public List<Entry> formatScoreboard(IUser user) {
    EntryBuilder builder = new EntryBuilder();

    for (String line : plugin.getLanguageManager().getLanguageList(arena.getArenaState() == IArenaState.FULL_GAME ? "Scoreboard.Content.Waiting"
        : "Scoreboard.Content." + arena.getArenaState().getFormattedName())) {
      builder.next(new MessageBuilder(line).player(user.getPlayer()).arena(arena).build());
    }

    return builder.build();
  }

}
