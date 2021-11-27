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

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Contains all GameStates.
 */
public enum ArenaState {
  WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), FULL_GAME("Full-Game"), IN_GAME("In-Game"), ENDING("Ending"), RESTARTING("Restarting");

  private final String formattedName;
  private final String placeholder;

  ArenaState(String formattedName) {
    this.formattedName = formattedName;
    Main plugin = JavaPlugin.getPlugin(Main.class);
    placeholder = plugin.getChatManager()
        .colorRawMessage(plugin.getLanguageManager().getLanguageMessage("Placeholders.Game-States." + formattedName));
  }

  public String getFormattedName() {
    return formattedName;
  }

  public String getPlaceholder() {
    return placeholder;
  }
}