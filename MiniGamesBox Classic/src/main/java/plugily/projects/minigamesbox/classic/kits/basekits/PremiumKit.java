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

package plugily.projects.minigamesbox.classic.kits.basekits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class PremiumKit extends Kit {

  public Material getMaterial() {
    return null;
  }

  @Override
  public ItemStack getItemStack() {
    return new ItemBuilder(getMaterial())
        .name(getName())
        .lore(getDescription())
        .lore(new MessageBuilder("KIT_KIT_MENU_LORE_UNLOCK_STORE").asKey().build())
        .build();
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return player.hasPermission( getPlugin().getPluginNamePrefixLong() + ".kits.locked") || player.hasPermission(getPlugin().getPluginNamePrefixLong() + ".kit." + getKey().toLowerCase());
  }
}
