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
package plugily.projects.minigamesbox.classic.handlers.holiday;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class NewYearHoliday implements Holiday, Listener {

  private PluginMain plugin;

  @Override
  public String getName() {
    return "NewYear";
  }

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return (month == 12 && day >= 31) || (month == 1 && day <= 1);
  }

  @Override
  public void enable(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void applyCreatureEffects(Creature creature) {
    org.bukkit.inventory.EntityEquipment equipment = creature.getEquipment();

    if(equipment != null && equipment.getHelmet() == null) {
      //randomizing head type
      if(plugin.getRandom().nextBoolean()) {
        equipment.setItemInMainHand(new ItemStack(XMaterial.FIREWORK_ROCKET.parseMaterial(), 1));
      } else {
        equipment.setItemInMainHand(new ItemStack(XMaterial.FIREWORK_STAR.parseMaterial(), 1));
      }
    }
  }

  @EventHandler
  public void onArrowShoot(EntityShootBowEvent e) {
    if(e.getEntityType() != org.bukkit.entity.EntityType.PLAYER || plugin.getArenaRegistry().getArena((Player) e.getEntity()) == null) {
      return;
    }
    Entity en = e.getProjectile();
    new BukkitRunnable() {
      @Override
      public void run() {
        if(en.isOnGround() || en.isDead()) {
          cancel();
          return;
        }
        VersionUtils.sendParticles("FIREWORKS_SPARK", (Set<Player>) null, en.getLocation(), 1);
      }
    }.runTaskTimer(plugin, 1, 1);
  }
}
