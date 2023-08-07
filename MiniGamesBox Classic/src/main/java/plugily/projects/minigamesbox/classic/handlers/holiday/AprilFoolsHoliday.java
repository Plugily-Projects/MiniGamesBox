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
package plugily.projects.minigamesbox.classic.handlers.holiday;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.powerup.Powerup;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class AprilFoolsHoliday implements Holiday, Listener {
  private PluginMain plugin;

  @Override
  public String getName() {
    return "AprilFools";
  }

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return (month == 3 && day >= 29) || (month == 4 && day <= 3);
  }

  @Override
  public void enable(PluginMain plugin) {
    this.plugin = plugin;
    Powerup powerup = new Powerup("APRIL_FOOL", new MessageBuilder("&a&llololol").build(),
        new MessageBuilder("&a&lApril Fools!").build(), XMaterial.DIRT, null, null, null, pickup -> {
      pickup.getPlayer().damage(0);
      VersionUtils.sendTitle(pickup.getPlayer(), pickup.getPowerup().getDescription(), 5, 30, 5);
    });
    plugin.getPowerupRegistry().registerPowerup(powerup);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void applyDeathEffects(Entity entity) {
    if(!plugin.getRandom().nextBoolean()) {
      return;
    }
    final List<Item> diamonds = new ArrayList<>();
    for(int i = 0; i < plugin.getRandom().nextInt(6); i++) {
      Item item = entity.getWorld().dropItem(entity.getLocation(), XMaterial.DIAMOND.parseItem());
      item.setPickupDelay(1000000);
      item.setVelocity(getRandomVector());
      diamonds.add(item);
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      diamonds.forEach(Item::remove);
      diamonds.clear();
    }, 30);
  }

  @EventHandler
  public void onArrowShoot(EntityShootBowEvent e) {
    if(e.getEntityType() != org.bukkit.entity.EntityType.PLAYER || plugin.getArenaRegistry().getArena((Player) e.getEntity()) == null) {
      return;
    }
    if(plugin.getRandom().nextInt(4) == 0) {
      //chance to make arrow shoot somewhere else
      e.getProjectile().setVelocity(getRandomVector());
    }
  }

  private Vector getRandomVector() {
    Vector direction = new Vector();
    direction.setX(0.0D + Math.random() - Math.random());
    direction.setY(Math.random());
    direction.setZ(0.0D + Math.random() - Math.random());
    return direction;
  }
}
