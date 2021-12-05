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

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class HalloweenHoliday implements Holiday, Listener {

  private Random random;
  private PluginMain plugin;

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return (month == 10 && day >= 27) || (month == 11 && day <= 4);
  }

  @Override
  public void enable(PluginMain plugin) {
    random = new Random();
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void applyCreatureEffects(Creature creature) {
    org.bukkit.inventory.EntityEquipment equipment = creature.getEquipment();

    if (equipment != null && equipment.getHelmet() == null) {
      //randomizing head type
      if (random.nextBoolean()) {
        equipment.setHelmet(new ItemStack(Material.JACK_O_LANTERN, 1));
      } else {
        equipment.setHelmet(new ItemStack(Material.PUMPKIN, 1));
      }
    }
  }

  @Override
  public void applyDeathEffects(Entity entity) {
    org.bukkit.Location entityLoc = entity.getLocation();

    entity.getWorld().strikeLightningEffect(entityLoc);

    //randomizing sound
    if (random.nextBoolean()) {
      VersionUtils.playSound(entityLoc, "ENTITY_WOLF_HOWL");
    } else {
      VersionUtils.playSound(entityLoc, "ENTITY_WITHER_DEATH");
    }

    //randomizing bats spawn chance
    if (random.nextBoolean()) {
      final List<Entity> bats = new ArrayList<>();

      for (int i = 0; i < random.nextInt(6); i++) {
        final Entity bat = entityLoc.getWorld().spawnEntity(entityLoc, EntityType.BAT);

        bat.setCustomName(plugin.getChatManager().colorRawMessage("&6Halloween!"));
        bats.add(bat);
      }

      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        for (Entity bat : bats) {
          bat.getWorld().playEffect(bat.getLocation(), Effect.SMOKE, 3);
          bat.remove();
        }

        bats.clear();
      }, 30);
    }
  }

  @EventHandler
  public void onBatDamage(EntityDamageEvent e) {
    if (e.getEntityType() != EntityType.BAT) {
      return;
    }

    String customName = e.getEntity().getCustomName();

    if (customName != null && customName.equals(plugin.getChatManager().colorRawMessage("&6Halloween!"))) {
      e.setCancelled(true);
    }
  }
}
