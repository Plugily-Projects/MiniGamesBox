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

package plugily.projects.minigamesbox.classic.handlers.sign;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.lang.reflect.InvocationTargetException;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */

/**
 * Created for 1.14 compatibility purposes, it will cache block behind sign that will be
 * accessed via reflection on 1.14 which is expensive
 */
public class ArenaSign {

  private final Sign sign;
  private Block behind;
  private final IPluginArena arena;

  public ArenaSign(Sign sign, IPluginArena arena) {
    this.sign = sign;
    this.arena = arena;
    setBehindBlock();
  }

  private void setBehindBlock() {
    behind = null;
    if(MaterialUtils.isWallSign(sign.getBlock().getType())) {
      behind = ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_14_R1) ? getBlockBehind() : getBlockBehindLegacy();
    }
  }

  private Block getBlockBehind() {
    try {
      org.bukkit.block.BlockState state = sign.getBlock().getState();
      Object blockData = state.getClass().getMethod("getBlockData").invoke(state);
      BlockFace face = (BlockFace) blockData.getClass().getMethod("getFacing").invoke(blockData);

      Location loc = sign.getLocation();
      return new Location(loc.getWorld(), loc.getBlockX() - face.getModX(), loc.getBlockY() - face.getModY(),
          loc.getBlockZ() - face.getModZ()).getBlock();
    } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  private Block getBlockBehindLegacy() {
    return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
  }

  public Sign getSign() {
    return sign;
  }

  @Nullable
  public Block getBehind() {
    return behind;
  }

  public IPluginArena getArena() {
    return arena;
  }

}
