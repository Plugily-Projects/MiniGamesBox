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

package plugily.projects.minigamesbox.classic.handlers.powerup;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerPowerupPickupEvent;
import plugily.projects.minigamesbox.classic.handlers.language.ChatManager;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
  private Main plugin;

  public PowerupRegistry(Main plugin) {
    if(!plugin.getConfig().getBoolean("Powerups.Enabled", true)) {
      return;
    }
    config = ConfigUtils.getConfig(plugin, "powerups");
    enabled = true;
    this.plugin = plugin;
    registerPowerups();
    if(registeredPowerups.isEmpty()) {
      plugin.getDebugger().debug(Level.WARNING, "[PowerupRegistry] Disabling power up module, all power ups disabled");
      enabled = false;
    }
  }

  private void registerPowerups() {
    plugin.getDebugger().debug("[PowerupRegistry] Registering power ups");
    long start = System.currentTimeMillis();

    ChatManager chatManager = plugin.getChatManager();


    for(String key : config.getKeys(false)) {
      if("Version".equals(key)) {
        continue;
      }

      Material mat = XMaterial.matchXMaterial(config.getString(key + ".material", "BEDROCK").toUpperCase()).orElse(XMaterial.BEDROCK).parseMaterial();
      String name = chatManager.colorRawMessage(config.getString(key + ".name"));
      String description = chatManager.colorRawMessage(config.getString(key + ".description"));

      List<String> effects = new ArrayList<>(config.getStringList(key + ".potion-effect"));

      BasePowerup.PotionType potionType = BasePowerup.PotionType.PLAYER;
      try {
        potionType = BasePowerup.PotionType.valueOf(config.getString(key + ".potion-type").toUpperCase());
      } catch(Exception ex) {
        plugin.getDebugger().debug(Level.WARNING, "Invalid potion type of powerup " + key + " in powerups.yml! Please use all or player!");
      }

      Set<Reward> rewards = new HashSet<>();
      for(String reward : config.getStringList(key + ".execute")) {
        rewards.add(new Reward(new RewardType(key), reward));
      }

      registerPowerup(new Powerup(key, name, description, mat, effects, potionType, rewards, pickup -> {
        if(pickup.getPowerup().getPotionType() == BasePowerup.PotionType.ALL) {
          for(Player p : pickup.getArena().getPlayers()) {
            VersionUtils.sendTitles(p, pickup.getPowerup().getName(), pickup.getPowerup().getDescription(), 5, 30, 5);
            XPotion.addPotionEffectsFromString(p, pickup.getPowerup().getEffects());
          }
        } else {
          VersionUtils.sendTitles(pickup.getPlayer(), pickup.getPowerup().getName(), pickup.getPowerup().getDescription(), 5, 30, 5);
          XPotion.addPotionEffectsFromString(pickup.getPlayer(), pickup.getPowerup().getEffects());
        }
        plugin.getRewardsHandler().performReward(pickup.getPlayer(), pickup.getArena(), pickup.getPowerup().getRewards());
      }));

    }

    plugin.getDebugger().debug("[PowerupRegistry] Registered all powerups took {0}ms", System.currentTimeMillis() - start);
  }

  /**
   * @return random powerup from list of registered ones
   */
  public BasePowerup getRandomPowerup() {
    return registeredPowerups.get(registeredPowerups.size() == 1 ? 0 : random.nextInt(registeredPowerups.size()));
  }

  public void spawnPowerup(Location loc, Arena arena) {
    if(!enabled || ThreadLocalRandom.current().nextDouble(0.0, 100.0) > plugin.getConfig().getDouble("Powerups.Drop-Chance", 1.0)) {
      return;
    }

    final BasePowerup powerup = getRandomPowerup();


    ArmorStandHologram hologram = new ArmorStandHologram(loc.clone().add(0.0, 1.2, 0.0))
        .appendItem(powerup.getMaterial().parseItem()).appendLine(powerup.getName());

    hologram.setPickupHandler(player -> {
      if(ArenaRegistry.getArena(player) != arena) {
        return;
      }

      PlugilyPlayerPowerupPickupEvent event = new PlugilyPlayerPowerupPickupEvent(arena, player, powerup);
      Bukkit.getPluginManager().callEvent(event);
      if(event.isCancelled()) {
        return;
      }

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
