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

package plugily.projects.minigamesbox.classic.utils.dimensional;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.PluginMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cuboid {

  private final int xMin, xMax, yMin, yMax, zMin, zMax;
  private final double xMinCentered, xMaxCentered, yMinCentered, yMaxCentered, zMinCentered, zMaxCentered;
  private final World world;


  public Cuboid(final Location point1, final Location point2) {
    xMin = Math.min(point1.getBlockX(), point2.getBlockX());
    xMax = Math.max(point1.getBlockX(), point2.getBlockX());
    yMin = Math.min(point1.getBlockY(), point2.getBlockY());
    yMax = Math.max(point1.getBlockY(), point2.getBlockY());
    zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
    zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
    world = point1.getWorld();
    xMinCentered = xMin + 0.5;
    xMaxCentered = xMax + 0.5;
    yMinCentered = yMin + 0.5;
    yMaxCentered = yMax + 0.5;
    zMinCentered = zMin + 0.5;
    zMaxCentered = zMax + 0.5;
  }

  public List<Block> blockList() {
    final List<Block> blocks = new ArrayList<>(getTotalBlockSize());
    for(int x = xMin; x <= xMax; ++x) {
      for(int y = yMin; y <= yMax; ++y) {
        for(int z = zMin; z <= zMax; ++z) {
          blocks.add(world.getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public List<Block> blockListWithoutFloor() {
    final List<Block> blocks = new ArrayList<>(getTotalBlockSize() - (getXWidth() * getZWidth()));
    for(int x = xMin; x <= xMax; ++x) {
      for(int y = yMin + 1; y <= yMax; ++y) {
        for(int z = zMin; z <= zMax; ++z) {
          blocks.add(world.getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public List<Block> floorBlockList() {
    final List<Block> blocks = new ArrayList<>(getXWidth() * getZWidth());
    for(int x = xMin; x <= xMax; ++x) {
      for(int z = zMin; z <= zMax; ++z) {
        blocks.add(world.getBlockAt(x, yMin, z));
      }
    }
    return blocks;
  }

  public List<Chunk> chunkList() {
    List<Block> blockList = blockList();
    List<Chunk> chunks = new ArrayList<>(blockList.size());

    for(Block block : blockList) {
      Chunk chunk = block.getChunk();
      if(!chunks.contains(chunk)) {
        chunks.add(chunk);
      }
    }
    return chunks;
  }

  public Location getCenter() {
    return new Location(world, (xMax - xMin) / 2 + xMin, (yMax - yMin) / 2 + yMin, (zMax - zMin) / 2 + zMin);
  }

  public double getDistance() {
    return getMinPoint().distance(getMaxPoint());
  }

  public double getDistanceSquared() {
    return getMinPoint().distanceSquared(getMaxPoint());
  }

  public int getHeight() {
    return yMax - yMin + 1;
  }

  public Location getMinPoint() {
    return new Location(world, xMin, yMin, zMin);
  }

  public Location getMaxPoint() {
    return new Location(world, xMax, yMax, zMax);
  }

  public Location getRandomLocation() {
    PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);
    final int x = plugin.getRandom().nextInt(Math.abs(xMax - xMin) + 1) + xMin;
    final int y = plugin.getRandom().nextInt(Math.abs(yMax - yMin) + 1) + yMin;
    final int z = plugin.getRandom().nextInt(Math.abs(zMax - zMin) + 1) + zMin;
    return new Location(world, x, y, z);
  }

  public int getTotalBlockSize() {
    return getHeight() * getXWidth() * getZWidth();
  }

  public int getXWidth() {
    return xMax - xMin + 1;
  }

  public int getZWidth() {
    return zMax - zMin + 1;
  }

  public boolean isIn(final Location loc) {
    int blockX = loc.getBlockX();
    int blockY = loc.getBlockY();
    int blockZ = loc.getBlockZ();

    return loc.getWorld() == world && blockX >= xMin && blockX <= xMax && blockY >= yMin && blockY <= yMax && blockZ >= zMin && blockZ <= zMax;
  }

  public boolean isIn(final Player player) {
    return isIn(player.getLocation());
  }

  public boolean isInWithMarge(final Location loc, final double marge) {
    return loc.getWorld() == world && loc.getX() >= xMinCentered - marge && loc.getX() <= xMaxCentered + marge && loc.getY() >= yMinCentered - marge && loc
        .getY() <= yMaxCentered + marge && loc.getZ() >= zMinCentered - marge && loc.getZ() <= zMaxCentered + marge;
  }

  public boolean isEmpty() {
    for(Block block : blockList()) {
      if(block.getType() != Material.AIR) {
        return false;
      }
    }
    return true;
  }

  public boolean contains(final Material material) {
    for(Block block : blockList()) {
      if(block.getType() == material) {
        return true;
      }
    }
    return false;
  }

  public void fill(final Material material) {
    for(Block block : blockList()) {
      block.setType(material);
    }
  }

  public void fillWithoutFloor(final Material material) {
    for(Block block : blockListWithoutFloor()) {
      block.setType(material);
    }
  }

  public void fillFloor(final Material material) {
    for(Block block : floorBlockList()) {
      block.setType(material);
    }
  }

  public boolean collidesWith(final Cuboid other) {
    if(xMax < other.xMin || xMin > other.xMax) {
      return false;
    }
    if(yMax < other.yMin || yMin > other.yMax) {
      return false;
    }
    return zMax >= other.zMin && zMin <= other.zMax;
  }

  public static boolean collidesWith(final Cuboid left, final Cuboid right) {
    return left.collidesWith(right);
  }
}
