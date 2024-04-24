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

package plugily.projects.minigamesbox.classic.events.spectator.settings;

import com.cryptomorin.xseries.XMaterial;
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
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.utils.actionbar.ActionBar;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.12.2021
 */
public class SpectatorSettingsMenu implements Listener {

  private final PluginMain plugin;
  private final NormalFastInv inventory;
  private final FileConfiguration config;
  public List<SpectatorSettingsItem> settingsItems = new ArrayList<>();
  public List<Player> firstPersonMode = new ArrayList<>();
  public List<Player> autoTeleport = new ArrayList<>();
  public Map<Player, Player> targetPlayer = new HashMap<>();
  public List<Player> invisibleSpectators = new ArrayList<>();

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
      String name = new MessageBuilder(config.getString(path + ".name", "Error!")).build();
      int slot = config.getInt(path + ".slot", -1);
      List<String> description = config.getStringList(path + ".description").stream()
          .map(itemLore -> itemLore = new MessageBuilder(itemLore).build())
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

  private NormalFastInv setupSpectatorSettings() {
    NormalFastInv gui = new NormalFastInv(plugin.getBukkitHelper().serializeInt(45), new MessageBuilder(config.getString("Settings-Menu.Inventory-name", "Settings Menu")).build());
    for(SpectatorSettingsItem item : settingsItems) {
      gui.setItem(item.getSlot(), item.getItemStack(), event -> {
        Player player = (Player) event.getWhoClicked();
        IPluginArena arena = plugin.getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        if(item.getPermission() != null && !item.getPermission().equalsIgnoreCase("")) {
          if(!plugin.getBukkitHelper().hasPermission(player, item.getPermission())) {
            return;
          }
        }
        plugin.getDebugger().debug("!!! SpectatorSettings " + item.getType());
        switch(item.getType()) {
          case DEFAULT_SPEED:
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED").asKey().arena(arena).integer(0).player(player).sendPlayer();
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.15f);
            break;
          case SPEED1:
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED").asKey().arena(arena).integer(1).player(player).sendPlayer();
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.2f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
            break;
          case SPEED2:
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED").asKey().arena(arena).integer(2).player(player).sendPlayer();
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.25f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
            break;
          case SPEED3:
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED").asKey().arena(arena).integer(3).player(player).sendPlayer();
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.3f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
            break;
          case SPEED4:
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_CHANGED_SPEED").asKey().arena(arena).integer(4).player(player).sendPlayer();
            player.removePotionEffect(PotionEffectType.SPEED);
            player.setFlySpeed(0.35f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, false, false));
            break;
          case AUTO_TELEPORT:
            if(autoTeleport.contains(player)) {
              autoTeleport.remove(player);
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_AUTO_TELEPORT").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED").asKey().build()).player(player).sendPlayer();
            } else {
              autoTeleport.add(player);
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_AUTO_TELEPORT").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED").asKey().build()).player(player).sendPlayer();
            }
            break;
          case NIGHT_VISION:
            if(player.getActivePotionEffects().stream().anyMatch(potionEffect -> potionEffect.getType().equals(PotionEffectType.NIGHT_VISION))) {
              player.removePotionEffect(PotionEffectType.NIGHT_VISION);
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_NIGHT_VISION").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED").asKey().build()).player(player).sendPlayer();
            } else {
              player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_NIGHT_VISION").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED").asKey().build()).player(player).sendPlayer();
            }
            break;
          case FIRST_PERSON_MODE:
            if(!targetPlayer.containsKey(player)) {
              return;
            }
            autoTeleport.remove(player);
            firstPersonMode.add(player);
            VersionUtils.sendTitle(player, new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_TITLE").asKey().player(player).arena(arena).build(), 5, 20, 5);
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
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_VISIBILITY").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_ENABLED").asKey().build()).player(player).sendPlayer();
            } else {
              invisibleSpectators.add(player);
              for(Player players : arena.getPlayers()) {
                if(!plugin.getUserManager().getUser(player).isSpectator()) {
                  continue;
                }
                VersionUtils.hidePlayer(plugin, player, players);
              }
              new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_VISIBILITY").asKey().arena(arena).value(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_STATUS_DISABLED").asKey().build()).player(player).sendPlayer();
            }
            break;
          case NONE:
            break;
        }
        plugin.getRewardsHandler().performReward(player, new HashSet<>(item.getRewards()));
      });
    }
    return gui;
  }

  @EventHandler
  public void onPlayerMovement(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    IUser user = plugin.getUserManager().getUser(player);
    if(user.getArena() != null) {
      firstPersonMode.forEach(spectator -> {
        if(spectator.getSpectatorTarget() instanceof Player) {
          plugin.getActionBarManager().addActionBar(spectator, new ActionBar(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_ACTION_BAR").asKey().arena(user.getArena()).player((Player) spectator.getSpectatorTarget()), ActionBar.ActionBarType.DISPLAY));
        }
      });
    }
    if(!user.isSpectator()) {
      return;
    }
    if(!targetPlayer.containsKey(player)) {
      return;
    }
    Player target = targetPlayer.get(player);
    if(player.getLocation().getWorld() != target.getLocation().getWorld()) {
      //Fix Cannot measure distance between worlds
      return;
    }
    double distance = player.getLocation().distance(target.getLocation());
    plugin.getActionBarManager().addActionBar(player, new ActionBar(new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_TARGET_PLAYER_ACTION_BAR").asKey().arena(user.getArena()).integer((int) distance).player(target), ActionBar.ActionBarType.DISPLAY));
    if(distance <= 15) {
      return;
    }
    if(autoTeleport.contains(player)) {
      VersionUtils.teleport(player, target.getLocation());
    }
  }

  @EventHandler
  public void onPlayerClick(PlugilyPlayerInteractEntityEvent event) {
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
    targetPlayer.remove(player);
    targetPlayer.put(player, target);
    if(!autoTeleport.contains(player)) {
      VersionUtils.sendTitle(player, new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_SETTINGS_FIRST_PERSON_MODE_TITLE").asKey().player(player).build(), 5, 20, 5);
      player.setGameMode(GameMode.SPECTATOR);
      player.setSpectatorTarget(target);
    }
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
    firstPersonMode.remove(player);
    player.setSpectatorTarget(null);
    player.setGameMode(GameMode.SURVIVAL);
  }

  public NormalFastInv getInventory() {
    return inventory;
  }
}
