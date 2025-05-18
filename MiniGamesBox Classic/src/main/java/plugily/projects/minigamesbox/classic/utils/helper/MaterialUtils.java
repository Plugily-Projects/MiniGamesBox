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

package plugily.projects.minigamesbox.classic.utils.helper;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class MaterialUtils {

  private static final Set<Material> WALL_SIGNS = new HashSet<Material>() {
    {
      add(getMaterial("WALL_SIGN"));
      add(getMaterial("ACACIA_WALL_SIGN"));
      add(getMaterial("BIRCH_WALL_SIGN"));
      add(getMaterial("DARK_OAK_WALL_SIGN"));
      add(getMaterial("JUNGLE_WALL_SIGN"));
      add(getMaterial("OAK_WALL_SIGN"));
      add(getMaterial("SPRUCE_WALL_SIGN"));
      add(getMaterial("WARPED_WALL_SIGN"));
      add(getMaterial("CRIMSON_WALL_SIGN"));
    }
  };

  public static final Set<Material> BASIC_SIGNS = new HashSet<Material>() {
    {
      add(getMaterial("SIGN"));
      add(getMaterial("STANDING_SIGN"));
      add(getMaterial("ACACIA_SIGN"));
      add(getMaterial("BIRCH_SIGN"));
      add(getMaterial("DARK_OAK_SIGN"));
      add(getMaterial("JUNGLE_SIGN"));
      add(getMaterial("OAK_SIGN"));
      add(getMaterial("SPRUCE_SIGN"));
      add(getMaterial("WARPED_SIGN"));
      add(getMaterial("CRIMSON_SIGN"));
    }
  };

  public static final Set<Material> ALL_SIGNS = new HashSet<Material>() {
    {
      addAll(WALL_SIGNS);
      addAll(BASIC_SIGNS);
    }
  };

  public static final Set<Material> DOORS = new HashSet<Material>() {
    {
      add(getMaterial("WOODEN_DOOR"));
      add(getMaterial("ACACIA_DOOR"));
      add(getMaterial("BIRCH_DOOR"));
      add(getMaterial("DARK_OAK_DOOR"));
      add(getMaterial("JUNGLE_DOOR"));
      add(getMaterial("SPRUCE_DOOR"));
      add(getMaterial("OAK_DOOR"));
      add(getMaterial("WOOD_DOOR"));
      add(getMaterial("WARPED_DOOR"));
      add(getMaterial("CRIMSON_DOOR"));
    }
  };

  public static boolean isWallSign(Material mat) {
    return WALL_SIGNS.contains(mat);
  }

  public static boolean isDoor(Material mat) {
    return DOORS.contains(mat);
  }

  private static Material getMaterial(String name) {
    return XMaterial.matchXMaterial(name.toUpperCase()).orElse(XMaterial.OAK_DOOR).get();
  }
}
