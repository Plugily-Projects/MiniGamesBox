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

package plugily.projects.minigamesbox.classic.kits.free;

import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;

import java.util.Collections;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.12.2021
 */
public class EmptyKit extends FreeKit {
  public EmptyKit(String key, String name) {
    super(key, name, Collections.singletonList("Kit Example"), XMaterial.BEDROCK.parseItem());
    getPlugin().getKitRegistry().registerKit(this);
  }
}
