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

package plugily.projects.minigamesbox.classic.events.spectator.settings;

import com.cryptomorin.xseries.XMaterial;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.12.2021
 */
public class SpectatorSettingsMenu implements Listener {

  private final PluginMain plugin;
  private final FastInv inventory;
  private final FileConfiguration config;
  public List<SpectatorSettingsItem> settingsItems = new ArrayList<>();
  public List<Player> autoTeleport;
  public Map<Player, Player> targetPlayer = new HashMap<>();
  public List<Player> invisibleSpectators;

  public SpectatorSettingsMenu(PluginMain plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "spectator");
    loadSpectatorSettingsItems();
    inventory = setupSpectatorSettings();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  private void loadSpectatorSettingsItems() {
    ConfigurationSection section = config.getConfigurationSection("Settings-Menu.Content");
    if(section == null) {
      return;
    }
    for(String type : section.getKeys(false)) {
      String path = "Settings-Menu.Content." + type;
      Material mat = XMaterial.matchXMaterial(config.getString(path + ".material", "BEDROCK").toUpperCase()).orElse(XMaterial.BEDROCK).parseMaterial();
      String name = plugin.getChatManager().colorRawMessage(config.getString(path + ".name"));
      int slot = config.getInt(path + ".slot", -1);
      List<String> description = config.getStringList(path + ".description").stream()
          .map(itemLore -> itemLore = plugin.getChatManager().colorRawMessage(itemLore))
          .collect(Collectors.toList());
      SpectatorSettingsItem.Type function = SpectatorSettingsItem.Type.NONE;
      try {
        function = SpectatorSettingsItem.Type.valueOf(type.toUpperCase());
      } catch(Exception ignored) {
      }
      Set<Reward> rewards = new HashSet<>();
      for(String reward : config.getStringList(path + ".execute")) {
        rewards.add(new Reward(new RewardType(path), reward));
      }
      settingsItems.add(new SpectatorSettingsItem(new ItemBuilder(mat).name(name).lore(description).build(), slot, config.getString(path + ".permission", null), rewards, function));
    }
  }

  private FastInv setupSpectatorSettings() {
    FastInv gui = new FastInv(plugin.getBukkitHelper().serializeInt(45), plugin.getChatManager().colorRawMessage(config.getString("Settings-Menu.Inventory-name", "Settings Menu")));
    for(SpectatorSettingsItem item : settingsItems) {
      gui.setItem(item.getSlot(), item.getItemStack(), event -> {
        Player player = (Player) event.getWhoClicked();
        PluginArena arena = plugin.getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        if(item.getPermission() != null && !item.getPermission().equalsIgnoreCase("")) {
          if(!plugin.getBukkitHelper().hasPermission(player, item.getPermission())) {
            return;
          }
        }
        switch(item.getType()) {
          case DEFAULT_SPEED:
            player.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED"), 0));
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.15f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
            break;
          case SPEED1:
            player.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED"), 1));
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.2f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
            break;
          case SPEED2:
            player.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED"), 2));
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.25f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
            break;
          case SPEED3:
            player.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED"), 3));
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.3f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, false, false));
            break;
          case SPEED4:
            player.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED"), 4));
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.35f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4, false, false));
            break;
          case AUTO_TELEPORT:
            if(autoTeleport.contains(player)) {
              autoTeleport.remove(player);
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_AUTO_TELEPORT").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED")));
            } else {
              autoTeleport.add(player);
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_AUTO_TELEPORT").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED")));
            }
            break;
          case NIGHT_VISION:
            if(player.getActivePotionEffects().stream().anyMatch(potionEffect -> potionEffect.getType() == PotionEffectType.NIGHT_VISION)) {
              player.removePotionEffect(PotionEffectType.NIGHT_VISION);
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_NIGHT_VISION").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED")));
            } else {
              player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_NIGHT_VISION").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED")));
            }
            break;
          case FIRST_PERSON_MODE:
            if(!targetPlayer.containsKey(player)) {
              return;
            }
            autoTeleport.remove(player);
            VersionUtils.sendTitle(player, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_TITLE"), 5, 20, 5);
            Player target = targetPlayer.get(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(target);
            break;
          case SPECTATORS_VISIBILITY:
            if(invisibleSpectators.contains(player)) {
              invisibleSpectators.remove(player);
              for(Player players : arena.getPlayers()) {
                VersionUtils.showPlayer(plugin, player, players);
              }
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_VISIBILITY").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED")));
            } else {
              invisibleSpectators.add(player);
              for(Player players : arena.getPlayers()) {
                VersionUtils.hidePlayer(plugin, player, players);
              }
              player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_VISIBILITY").replace("%status%", plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED")));
            }
            break;
          case NONE:
            break;
        }
        plugin.getRewardsHandler().performReward(player, item.getRewards());
      });
    }
    return gui;
  }

  @EventHandler
  public void onPlayerMovement(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    User user = plugin.getUserManager().getUser(player);
    if(!user.isSpectator()) {
      return;
    }
    if(!targetPlayer.containsKey(player)) {
      return;
    }
    Player target = targetPlayer.get(player);
    if(!autoTeleport.contains(player)) {
      if(player.getSpectatorTarget() instanceof Player) {
        VersionUtils.sendActionBar(player, plugin.getChatManager().formatMessage(user.getArena(), plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_ACTION_BAR"), target));
      }
      return;
    }
    double distance = player.getLocation().distance(target.getLocation());
    VersionUtils.sendActionBar(player, plugin.getChatManager().formatMessage(user.getArena(), plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_TARGET_PLAYER_ACTION_BAR"), (int) distance, target));
    if(distance <= 15) {
      return;
    }
    player.teleport(target);
  }

  @EventHandler
  public void onPlayerClick(CBPlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    if(!plugin.getUserManager().getUser(player).isSpectator()) {
      return;
    }
    if(!(event.getRightClicked() instanceof Player)) {
      return;
    }
    Player target = (Player) event.getRightClicked();
    if(!plugin.getArenaRegistry().isInArena(target)) {
      return;
    }
    VersionUtils.sendTitle(player, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_TITLE"), 5, 20, 5);
    targetPlayer.remove(player);
    targetPlayer.put(player, target);
    player.setGameMode(GameMode.SPECTATOR);
    player.setSpectatorTarget(target);
  }

  @EventHandler
  public void onPlayerSneak(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    if(!plugin.getUserManager().getUser(player).isSpectator()) {
      return;
    }
    if(!(player.getSpectatorTarget() instanceof Player)) {
      return;
    }
    player.setSpectatorTarget(null);
    player.setGameMode(GameMode.SURVIVAL);
  }

  public FastInv getInventory() {
    return inventory;
  }
}
