/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.utils.version.bossbar;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class LegacyBossBar extends BukkitRunnable {

  private String title;
  private HashMap<Player, EntityWither> withers = new HashMap<>();

  public LegacyBossBar(Plugin main, String title) {
    this.title = title;
    runTaskTimer(main, 0, 10);
  }

  public void addPlayer(Player p) {
    EntityWither wither = new EntityWither(((CraftWorld) p.getWorld()).getHandle());
    Location l = getWitherLocation(p.getLocation());
    wither.setCustomName(title);
    wither.setInvisible(true);
    wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
    PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    withers.put(p, wither);
  }

  public void removePlayer(Player p) {
    EntityWither wither = withers.remove(p);
    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
  }

  public void setTitle(String title) {
    this.title = title;
    for(Map.Entry<Player, EntityWither> entry : withers.entrySet()) {
      EntityWither wither = entry.getValue();
      wither.setCustomName(title);
      PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
      ((CraftPlayer) entry.getKey()).getHandle().playerConnection.sendPacket(packet);
    }
  }

  public void setProgress(double progress) {
    for(Map.Entry<Player, EntityWither> entry : withers.entrySet()) {
      EntityWither wither = entry.getValue();
      wither.setHealth((float) (progress * wither.getMaxHealth()));
      PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
      ((CraftPlayer) entry.getKey()).getHandle().playerConnection.sendPacket(packet);
    }
  }

  public Location getWitherLocation(Location l) {
    return l.add(l.getDirection().multiply(60));
  }

  @Override
  public void run() {
    for(Map.Entry<Player, EntityWither> en : withers.entrySet()) {
      EntityWither wither = en.getValue();
      Location l = getWitherLocation(en.getKey().getLocation());
      wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
      PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wither);
      ((CraftPlayer) en.getKey()).getHandle().playerConnection.sendPacket(packet);
    }
  }
}
