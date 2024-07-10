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
      WALL_SIGNS.add(getMat("WALL_SIGN"));
      WALL_SIGNS.add(getMat("ACACIA_WALL_SIGN"));
      WALL_SIGNS.add(getMat("BIRCH_WALL_SIGN"));
      WALL_SIGNS.add(getMat("DARK_OAK_WALL_SIGN"));
      WALL_SIGNS.add(getMat("JUNGLE_WALL_SIGN"));
      WALL_SIGNS.add(getMat("OAK_WALL_SIGN"));
      WALL_SIGNS.add(getMat("SPRUCE_WALL_SIGN"));
      WALL_SIGNS.add(getMat("WARPED_WALL_SIGN"));
      WALL_SIGNS.add(getMat("CRIMSON_WALL_SIGN"));
    }
  };

  public static final Set<Material> BASIC_SIGNS = new HashSet<Material>() {
    {
      BASIC_SIGNS.add(getMat("SIGN"));
      BASIC_SIGNS.add(getMat("STANDING_SIGN"));
      BASIC_SIGNS.add(getMat("ACACIA_SIGN"));
      BASIC_SIGNS.add(getMat("BIRCH_SIGN"));
      BASIC_SIGNS.add(getMat("DARK_OAK_SIGN"));
      BASIC_SIGNS.add(getMat("JUNGLE_SIGN"));
      BASIC_SIGNS.add(getMat("OAK_SIGN"));
      BASIC_SIGNS.add(getMat("SPRUCE_SIGN"));
      BASIC_SIGNS.add(getMat("WARPED_SIGN"));
      BASIC_SIGNS.add(getMat("CRIMSON_SIGN"));
    }
  };

  public static final Set<Material> ALL_SIGNS = new HashSet<Material>() {
    {
      ALL_SIGNS.addAll(WALL_SIGNS);
      ALL_SIGNS.addAll(BASIC_SIGNS);
    }
  };

  public static final Set<Material> DOORS = new HashSet<Material>() {
    {
      DOORS.add(getMat("WOODEN_DOOR"));
      DOORS.add(getMat("ACACIA_DOOR"));
      DOORS.add(getMat("BIRCH_DOOR"));
      DOORS.add(getMat("DARK_OAK_DOOR"));
      DOORS.add(getMat("JUNGLE_DOOR"));
      DOORS.add(getMat("SPRUCE_DOOR"));
      DOORS.add(getMat("OAK_DOOR"));
      DOORS.add(getMat("WOOD_DOOR"));
      DOORS.add(getMat("WARPED_DOOR"));
      DOORS.add(getMat("CRIMSON_DOOR"));
    }
  };

  public static boolean isWallSign(Material mat) {
    return WALL_SIGNS.contains(mat);
  }

  public static boolean isDoor(Material mat) {
    return DOORS.contains(mat);
  }

  private static Material getMat(String name) {
    Material material = Material.getMaterial(name.toUpperCase());
    if(material == null) {
      material = XMaterial.OAK_SIGN.parseMaterial();
    }
    return material;
  }
}
