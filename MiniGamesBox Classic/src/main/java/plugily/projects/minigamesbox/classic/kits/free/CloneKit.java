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
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.12.2021
 */
public class CloneKit extends FreeKit {

  public CloneKit() {
    setName(new MessageBuilder("KITS_EXAMPLE_NAME").asKey().build());
    setKey("Clone");
    List<String> description = getPlugin().getBukkitHelper().splitString(new MessageBuilder("KITS_EXAMPLE_DESCRIPTION").asKey().build(), 40);
    setDescription(description.toArray(new String[0]));
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    return itemStack;
  }

  @Override
  public Material getMaterial() {
    return XMaterial.WOODEN_SWORD.parseMaterial();
  }
}
