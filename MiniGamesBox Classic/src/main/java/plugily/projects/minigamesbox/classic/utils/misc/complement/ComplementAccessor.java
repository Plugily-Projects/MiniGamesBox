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

package plugily.projects.minigamesbox.classic.utils.misc.complement;

import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

public final class ComplementAccessor {

  private static Complement complement;

  static {
    boolean kyoriSupported = false;
    try {
      Class.forName("net.kyori.adventure.text.Component");
      org.bukkit.inventory.InventoryView.class.getDeclaredMethod("title");
      kyoriSupported = true;
    } catch(NoSuchMethodException | ClassNotFoundException e) {
    }

    complement = (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16) && kyoriSupported)
        ? new Complement2()
        : new Complement1();
  }

  public static Complement getComplement() {
    return complement;
  }
}
