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
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
//todo custom holidays made with holidays.yml e.g. custom timeframe, custom effects
public class HolidayManager {

  private final List<Holiday> registeredHolidays = new ArrayList<>();
  private final List<Holiday> enabledHolidays = new ArrayList<>();
  private boolean enabled = true;

  public HolidayManager(Main plugin) {
    if(!plugin.getConfig().getBoolean("Holidays-Enabled", true)) {
      enabled = false;
      return;
    }

    registeredHolidays.add(new AprilFoolsHoliday());
    registeredHolidays.add(new HalloweenHoliday());
    registeredHolidays.add(new ValentineHoliday());

    // Enable holidays after other plugins are enabled (after other addons register their holidays)
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
      LocalDateTime time = LocalDateTime.now();
      registeredHolidays.stream()
          .filter(holiday -> holiday.isHoliday(time))
          .forEach(holiday -> {
            holiday.enable(plugin);
            enabledHolidays.add(holiday);
          });
    });
  }

  /**
   * Applies holiday effects for creatures based on current holiday
   * eg. pumpkin heads on halloween
   *
   * @param creature entity to apply effects for
   */
  public void applyHolidayCreatureEffects(Creature creature) {
    if(enabled) {
      enabledHolidays.forEach(holiday -> holiday.applyCreatureEffects(creature));
    }
  }

  /**
   * Applies holiday death effects for entities based on current holiday
   * eg. scary sound and optional bats when entity dies on halloween
   *
   * @param en entity to apply effects for
   */
  public void applyHolidayDeathEffects(Entity en) {
    if(enabled) {
      enabledHolidays.forEach(holiday -> holiday.applyDeathEffects(en));
    }
  }

  /**
   * Applies holiday kill effects for entities based on current holiday
   * eg. scary sound and optional bats when entity dies on halloween
   *
   * @param alive alive player
   * @param dead  dead player
   */
  public void applyHolidayKillEffects(Entity dead, Entity alive) {
    if(enabled) {
      enabledHolidays.forEach(holiday -> holiday.applyKillEffects(dead, alive));
    }
  }

  /**
   * Applies holiday player effects for entities based on current holiday
   * eg. pumpkin head
   *
   * @param player player to apply effects for
   */
  public void applyPlayerEffects(Player player) {
    if(enabled) {
      enabledHolidays.forEach(holiday -> holiday.applyPlayerEffects(player));
    }
  }

  /**
   * Register a holiday from another plugin
   *
   * @param holiday to register
   */
  public void registerHoliday(Holiday holiday) {
    registeredHolidays.add(holiday);
  }

  /**
   * Get the registered holidays
   *
   * @return the registered holidays
   */
  public List<Holiday> getRegisteredHolidays() {
    return registeredHolidays;
  }

  /**
   * Get the enabled holidays
   *
   * @return the enabled holidays
   */
  public List<Holiday> getEnabledHolidays() {
    return enabledHolidays;
  }
}
