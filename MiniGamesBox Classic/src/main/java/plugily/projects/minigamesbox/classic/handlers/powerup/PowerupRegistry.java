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

package plugily.projects.minigamesbox.classic.handlers.powerup;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.events.player.PlugilyPlayerPowerupPickupEvent;
import plugily.projects.minigamesbox.api.handlers.powerup.BasePowerup;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.number.NumberUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.10.2021
 */
public class PowerupRegistry {

  private final Random random = new Random();
  private final List<BasePowerup> registeredPowerups = new ArrayList<>();
  private FileConfiguration config;
  private boolean enabled = false;
  private PluginMain plugin;

  public PowerupRegistry(PluginMain plugin) {
    this.plugin = plugin;
    if(!plugin.getConfigPreferences().getOption("POWERUPS")) {
      return;
    }
    config = ConfigUtils.getConfig(plugin, "powerups");
    enabled = true;
    registerPowerups();
    if(registeredPowerups.isEmpty()) {
      plugin.getDebugger().debug(Level.WARNING, "[PowerupRegistry] Disabling power up module, all power ups disabled");
      enabled = false;
    }
  }

  private void registerPowerups() {
    plugin.getDebugger().debug("[PowerupRegistry] Registering power ups");
    long start = System.currentTimeMillis();

    ConfigurationSection section = config.getConfigurationSection("Powerups.Content");

    if(section == null) {
      return;
    }

    for(String key : section.getKeys(false)) {
      if(!section.getBoolean(key + ".active", true)) {
        continue;
      }
      XMaterial mat = XMaterial.matchXMaterial(section.getString(key + ".material", "BEDROCK").toUpperCase()).orElse(XMaterial.BEDROCK);
      String name = new MessageBuilder(section.getString(key + ".name", "Error!")).build();
      String description = new MessageBuilder(section.getString(key + ".description", "Errror!")).build();

      List<String> effects = new ArrayList<>(section.getStringList(key + ".potion-effect"));

      description = new MessageBuilder(description).integer(getLongestEffect(effects)).build();

      BasePowerup.PotionType potionType = BasePowerup.PotionType.PLAYER;
      try {
        potionType = BasePowerup.PotionType.valueOf(section.getString(key + ".potion-type").toUpperCase());
      } catch(Exception ex) {
        plugin.getDebugger().debug(Level.WARNING, "Invalid potion type of powerup " + key + " in powerups.yml! Please use all or player!");
      }

      Set<Reward> rewards = new HashSet<>();
      for(String reward : section.getStringList(key + ".execute")) {
        rewards.add(new Reward(new RewardType(key), reward));
      }

      registerPowerup(new Powerup(key, name, description, mat, effects, potionType, rewards, pickup -> {
        if(pickup.getPowerup().getPotionType() == BasePowerup.PotionType.ALL) {
          for(Player p : pickup.getArena().getPlayers()) {
            VersionUtils.sendTitles(p, pickup.getPowerup().getName(), pickup.getPowerup().getDescription(), 5, 30, 5);
            XPotion.addEffects(p, pickup.getPowerup().getEffects());
          }
        } else {
          VersionUtils.sendTitles(pickup.getPlayer(), pickup.getPowerup().getName(), pickup.getPowerup().getDescription(), 5, 30, 5);
          XPotion.addEffects(pickup.getPlayer(), pickup.getPowerup().getEffects());
        }
        int duration = getLongestEffect(pickup.getPowerup());

        if(duration != 0) {
          Bukkit.getScheduler().runTaskLater(plugin, () -> {
            new MessageBuilder(config.getString("Powerups.Ended.Chat", "")).arena(pickup.getArena()).player(pickup.getPlayer()).value(pickup.getPowerup().getName()).sendArena();
            new TitleBuilder(config.getString("Powerups.Ended.Title", "")).arena(pickup.getArena()).player(pickup.getPlayer()).value(pickup.getPowerup().getName()).sendArena();
          }, duration);
        }

        plugin.getRewardsHandler().performReward(pickup.getPlayer(), pickup.getArena(), pickup.getPowerup().getRewards());
      }));

    }

    plugin.getDebugger().debug("[PowerupRegistry] Registered all powerups took {0}ms", System.currentTimeMillis() - start);
  }

  public int getLongestEffect(BasePowerup powerup) {
    List<String> effects = powerup.getEffects();
    return getLongestDuration(effects);
  }

  public int getLongestEffect(List<String> effects) {
    return getLongestDuration(effects);
  }

  private int getLongestDuration(List<String> effects) {
    int longDuration = 0;

    for(String effect : effects) {
      String[] split = StringUtils.split(StringUtils.deleteWhitespace(effect), ',');
      if(split.length == 0) {
        split = StringUtils.split(effect, ' ');
      }
      if(split.length <= 2) {
        return longDuration;
      }
      int duration = NumberUtils.parseInt(split[1]).get() * 20;
      if(longDuration <= duration) {
        longDuration = duration;
      }
    }
    return longDuration;
  }

  /**
   * @return random powerup from list of registered ones
   */
  public BasePowerup getRandomPowerup() {
    return registeredPowerups.get(registeredPowerups.size() == 1 ? 0 : random.nextInt(registeredPowerups.size()));
  }

  public void spawnPowerup(Location loc, PluginArena arena) {
    if(!enabled || ThreadLocalRandom.current().nextDouble(0.0, 100.0) > config.getDouble("Powerups.Drop.Chance", 1.0)) {
      return;
    }

    final BasePowerup powerup = getRandomPowerup();


    ArmorStandHologram hologram = new ArmorStandHologram(loc.clone())
        .appendItem(powerup.getMaterial().parseItem()).appendLine(powerup.getName());

    hologram.setPickupHandler(player -> {
      if(plugin.getArenaRegistry().getArena(player) != arena) {
        return;
      }

      PlugilyPlayerPowerupPickupEvent event = new PlugilyPlayerPowerupPickupEvent(arena, player, powerup);
      Bukkit.getPluginManager().callEvent(event);
      if(event.isCancelled()) {
        return;
      }
      new MessageBuilder(config.getString("Powerups.Pickup.Chat", "")).arena(arena).player(player).value(powerup.getName()).sendArena();

      powerup.getOnPickup().accept(new PowerupPickupHandler(powerup, arena, player));
      hologram.delete();
    });
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if(!hologram.isDeleted()) {
        hologram.delete();
      }
    }, /* remove after 40 seconds to prevent staying even if arena is finished */ 20 * 40);
  }

  /**
   * Attempts to register a powerup
   *
   * @param powerup powerup to register
   * @throws IllegalArgumentException if power-up with same ID currently exist
   */
  public void registerPowerup(BasePowerup powerup) {
    for(BasePowerup pwup : registeredPowerups) {
      if(pwup.getKey().equals(powerup.getKey())) {
        throw new IllegalArgumentException("Cannot register new power-up with same ID!");
      }
    }
    plugin.getDebugger().debug("[PowerupRegistry] Registered power up {0}", powerup.getName());
    registeredPowerups.add(powerup);
  }

  /**
   * Unregisters target powerup from registry
   *
   * @param powerup powerup to remove
   */
  public void unregisterPowerup(Powerup powerup) {
    registeredPowerups.remove(powerup);
  }

}
