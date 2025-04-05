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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.managers.IPluginScoreboardManager;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.*;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginScoreboardManager implements IPluginScoreboardManager {

  private final Map<UUID, FastBoard> boardMap = new HashMap<>();
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

    FastBoard board = new FastBoard(player) {
      @Override
      public boolean hasLinesMaxLength() {
        return isLinesMaxLength(getPlayer());
      }
    };

    board.updateTitle(boardTitle);
    board.updateLines(formatScoreboardLines(getScoreboardLines(player), player));


    boardMap.put(user.getUniqueId(), board);
  }

  @Override
  public void updateScoreboards() {
    boardMap.values().forEach(fastBoard -> fastBoard.updateLines(formatScoreboardLines(getScoreboardLines(fastBoard.getPlayer()), fastBoard.getPlayer())));
  }

  @Override
  public void removeScoreboard(IUser user) {
    Optional.ofNullable(boardMap.remove(user.getUniqueId())).ifPresent(FastBoard::delete);
  }

  @Override
  public void stopAllScoreboards() {
    boardMap.values().forEach(FastBoard::delete);
    boardMap.clear();
  }

  @Override
  public List<String> getScoreboardLines(Player player) {
    return new ArrayList<>(plugin.getLanguageManager().getLanguageList(arena.getArenaState() == IArenaState.FULL_GAME ? "Scoreboard.Content.Starting"
        : "Scoreboard.Content." + arena.getArenaState().getFormattedName()));
  }

  @Override
  public List<String> formatScoreboardLines(List<String> lines, Player player) {
    List<String> formattedLines = new ArrayList<>();
    if(isLinesMaxLength(player)) {
      List<String> linesWithoutSpecialChars = new ArrayList<>();
      for(String line : lines) {
        linesWithoutSpecialChars.add(line.replace("■ ", "").replace("|", ""));
      }
      lines = linesWithoutSpecialChars;
    }
    for(String line : lines) {
      formattedLines.add(new MessageBuilder(line).player(player).arena(arena).build());
    }
    return formattedLines;
  }

  private boolean isLinesMaxLength(Player player) {
    if(Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
      try {
        return Via.getAPI().getPlayerVersion(player) < ProtocolVersion.v1_13.getVersion();
      } catch(Exception ignored) {
        //Not using ViaVersion 4 or unable to get ViaVersion return LegacyBoard!
      }
    }
    return !ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13);
  }

}
