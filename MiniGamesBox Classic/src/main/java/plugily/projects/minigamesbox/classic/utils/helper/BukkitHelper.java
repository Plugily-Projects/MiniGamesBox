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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.actionbar.ActionBar;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tigerpanzer_02
 * <p>Created at 19.09.2021
 */
public class BukkitHelper {

  private final PluginMain plugin;

  public BukkitHelper(PluginMain plugin) {
    this.plugin = plugin;
  }

  @Deprecated
  public void applyActionBarCooldown(Player player, double seconds) {
    plugin.getActionBarManager().addActionBar(player, new ActionBar(new MessageBuilder("IN_GAME_MESSAGES_ARENA_COOLDOWN").asKey(),
        ActionBar.ActionBarType.PROGRESS, seconds));
  }

  public void takeOneItem(Player player, ItemStack stack) {
    if(stack.getAmount() <= 1) {
      VersionUtils.setItemInHand(player, new ItemStack(Material.AIR));
    } else {
      VersionUtils.getItemInHand(player).setAmount(stack.getAmount() - 1);
    }
  }

  /**
   * Serialize int to use it in Inventories size ex. you have 38 kits and it will serialize it to 45
   * (9*5) because it is valid inventory size next ex. you have 55 items and it will serialize it to
   * 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public int serializeInt(int i) {
    return (i % 9) == 0 ? i : (int) ((Math.ceil((double) i / 9) * 9) + 9);
  }

  @SuppressWarnings("deprecation")
  public List<Block> getNearbyBlocks(LivingEntity entity, int distance) {
    List<Block> blocks = new LinkedList<>();
    Iterator<Block> itr = new BlockIterator(entity, distance);
    while(itr.hasNext()) {
      Block block = itr.next();
      if(!block.getType().isTransparent()) {
        blocks.add(block);
      }
    }
    return blocks;
  }

  public List<Block> getNearbyBlocks(Location location, int radius) {
    List<Block> blocks = new ArrayList<>();

    org.bukkit.World world = location.getWorld();
    if(world == null)
      return blocks;

    int blockX = location.getBlockX();
    int blockY = location.getBlockY();
    int blockZ = location.getBlockZ();

    for(int x = blockX - radius; x <= blockX + radius; x++) {
      for(int y = blockY - radius; y <= blockY + radius; y++) {
        for(int z = blockZ - radius; z <= blockZ + radius; z++) {
          blocks.add(world.getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public Entity[] getNearbyEntities(Location loc, int radius) {
    org.bukkit.World world = loc.getWorld();
    Block locBlock = world.getBlockAt(loc);

    int x = (int) loc.getX();
    int y = (int) loc.getY();
    int z = (int) loc.getZ();

    int chunkRadius = radius < 16 ? 1 : radius / 16;
    Set<Entity> radiusEntities = new HashSet<>();

    for(int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
      for(int chunkZ = -chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
        for(Entity e :
            new Location(world, x + (chunkX * 16), y, z + (chunkZ * 16)).getChunk().getEntities()) {
          if(!world.getName().equalsIgnoreCase(e.getWorld().getName())) {
            continue;
          }
          if(e.getLocation().distanceSquared(loc) <= radius * radius
              && e.getLocation().getBlock() != locBlock) {
            radiusEntities.add(e);
          }
        }
      }
    }
    return radiusEntities.toArray(new Entity[0]);
  }

  public List<String> splitString(String string, int max) {
    List<String> matchList = new ArrayList<>();
    Matcher regexMatcher =
        Pattern.compile(".{1," + max + "}(?:\\s|$)", Pattern.DOTALL).matcher(string);

    while(regexMatcher.find()) {
      matchList.add(org.bukkit.ChatColor.GRAY + regexMatcher.group());
    }

    return matchList;
  }

  public byte getDoorByte(BlockFace face) {
    switch(face) {
      case NORTH:
        return 3;
      case SOUTH:
        return 1;
      case WEST:
        return 2;
      case EAST:
      default:
        return 0;
    }
  }

  public BlockFace getFacingByByte(byte bt) {
    switch(bt) {
      case 2:
        return BlockFace.WEST;
      case 3:
        return BlockFace.EAST;
      case 4:
        return BlockFace.NORTH;
      case 1:
      default:
        return BlockFace.SOUTH;
    }
  }

  public boolean checkIsInGameInstance(Player player) {
    if(plugin.getArenaRegistry().getArena(player) == null) {
      new MessageBuilder("COMMANDS_NOT_PLAYING").asKey().player(player).sendPlayer();
      return false;
    }
    return true;
  }

  public boolean hasPermission(CommandSender sender, String perm) {
    if(sender.hasPermission(perm)) {
      return true;
    }
    new MessageBuilder("COMMANDS_NO_PERMISSION").asKey().send(sender);
    return false;
  }

  public Vector rotateAroundAxisX(Vector v, double angle) {
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle),
        sin = Math.sin(angle),
        y = v.getY() * cos - v.getZ() * sin,
        z = v.getY() * sin + v.getZ() * cos;
    return v.setY(y).setZ(z);
  }

  public Vector rotateAroundAxisY(Vector v, double angle) {
    angle = -angle;
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle),
        sin = Math.sin(angle),
        x = v.getX() * cos + v.getZ() * sin,
        z = v.getX() * -sin + v.getZ() * cos;
    return v.setX(x).setZ(z);
  }

  public Location getBlockCenter(Location location) {
    double x = location.getX();
    double z = location.getZ();
    Location center = location.clone();
    if(x % 1 == 0) {
      center.add(0.5, 0, 0);
    }
    if(z % 1 == 0) {
      center.add(0, 0, 0.5);
    }
    return center;
  }

  public String matchColorRegex(String s) {
    String regex = "&?#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})";
    Matcher matcher = Pattern.compile(regex).matcher(s);
    while(matcher.find()) {
      String group = matcher.group(0);
      String group2 = matcher.group(1);
      try {
        s = s.replace(group, net.md_5.bungee.api.ChatColor.of("#" + group2) + "");
      } catch(Exception e) {
        plugin.getDebugger().debug("Bad hex color match: " + group);
      }
    }
    return s;
  }

  /**
   * Checks whether itemstack is named (not null, has meta and display name)
   *
   * @param stack item stack to check
   * @return true if named, false otherwise
   */
  public boolean isNamed(ItemStack stack) {
    return stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
  }
}
