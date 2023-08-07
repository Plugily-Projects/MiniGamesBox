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

package plugily.projects.minigamesbox.classic.handlers.setup;

import org.bukkit.entity.HumanEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class SetupInventoryUtils {

  private static Map<HumanEntity, String> setupInventories = new HashMap<>();

  public static void addSetupInventory(HumanEntity humanEntity, String arenaKey) {
    setupInventories.put(humanEntity, arenaKey);
  }

  public static String getArenaKey(HumanEntity humanEntity) {
    return setupInventories.get(humanEntity);
  }

  public static void removeSetupInventory(HumanEntity humanEntity) {
    setupInventories.remove(humanEntity);
  }

  public enum SetupInventoryStage {
    HOME("home"), ARENA_EDITOR("arena_editor"), ARENA_LIST("arena_list");

    private final String tutorialURL;

    SetupInventoryStage(String tutorialURL) {
      //TODO first time arena setup watch tutorial video
      //TODO interactive video tutorial, tutorials for any item, e.g. opens on item middle click
      this.tutorialURL = "https://wiki.plugily.xyz/plugily/tutorial/setup/gui/" + tutorialURL;
    }

    public String getTutorialURL() {
      return tutorialURL;
    }
  }
}
