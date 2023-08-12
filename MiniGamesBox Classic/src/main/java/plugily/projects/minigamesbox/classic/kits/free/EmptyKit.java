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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.12.2021
 */
public class EmptyKit extends FreeKit {

  public EmptyKit() {
    setName("");
    setKey("");
    List<String> description = getPlugin().getBukkitHelper().splitString("", 40);
    setDescription(description.toArray(new String[0]));
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public void setupKitItems() {

  }

  @Override
  public void giveKitItems(Player player) {
  }

  @Override
  public Material getMaterial() {
    return XMaterial.BEDROCK.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }
}
