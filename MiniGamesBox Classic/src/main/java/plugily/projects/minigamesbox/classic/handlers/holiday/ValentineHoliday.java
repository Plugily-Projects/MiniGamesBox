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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.powerup.Powerup;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class ValentineHoliday implements Holiday, Listener {
  private PluginMain plugin;

  @Override
  public String getName() {
    return "Valentine";
  }

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return month == 2 && day >= 10 && day <= 18;
  }

  @Override
  public void enable(PluginMain plugin) {
    this.plugin = plugin;
    Powerup powerup = new Powerup("VALENTINES_HEALING", new MessageBuilder("&c&l<3").build(),
        new MessageBuilder("&d&lHappy Valentine's Day!").build(), XMaterial.POPPY, null, null, null, pickup -> {
      pickup.getPlayer().setHealth(VersionUtils.getMaxHealth(pickup.getPlayer()));
      VersionUtils.sendTitle(pickup.getPlayer(), pickup.getPowerup().getDescription(), 5, 30, 5);
    });
    plugin.getPowerupRegistry().registerPowerup(powerup);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
        VersionUtils.sendParticles("HEART", (Set<Player>) null, en.getLocation(), 1);
      }
    }.runTaskTimer(plugin, 1, 1);
  }
}
