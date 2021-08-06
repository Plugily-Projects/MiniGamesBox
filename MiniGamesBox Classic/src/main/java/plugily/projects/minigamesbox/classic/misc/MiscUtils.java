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

package plugily.projects.minigamesbox.classic.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import plugily.projects.minigamesbox.classic.compat.ServerVersion;
import plugily.projects.minigamesbox.classic.compat.ServerVersion.Version;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class MiscUtils {

  private static final Random RANDOM = new Random();
  private static final Pattern PATTERN = Pattern.compile("&?#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

  private MiscUtils() {
  }

  public static String matchColorRegex(String s) {
    if(Version.isCurrentLower(Version.v1_16_R1)) {
      return s;
    }

    Matcher matcher = PATTERN.matcher(s);
    while(matcher.find()) {
      try {
        s = s.replace(matcher.group(0), net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)) + "");
      } catch(Exception e) {
        System.err.println("Invalid hex color: " + e.getLocalizedMessage());
      }
    }

    return s;
  }

  // https://www.spigotmc.org/threads/comprehensive-particle-spawning-guide-1-13.343001/
  @Deprecated
  public static void spawnParticle(org.bukkit.Particle particle, Location loc, int count, double offsetX, double offsetY, double offsetZ, double extra) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R2) && particle == org.bukkit.Particle.REDSTONE) {
      org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(Color.RED, 2);
      loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, extra, dustOptions);
    } else if(particle == org.bukkit.Particle.ITEM_CRACK) {
      ItemStack itemCrackData = new ItemStack(loc.getBlock().getType());
      loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, extra, itemCrackData);
    } else if(particle == org.bukkit.Particle.BLOCK_CRACK || particle == org.bukkit.Particle.BLOCK_DUST || particle == org.bukkit.Particle.FALLING_DUST) {
      loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, extra, loc.getBlock().getType().createBlockData());
    } else {
      loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, extra);
    }
  }

  public static Optional<AttributeInstance> getEntityAttribute(LivingEntity entity, Attribute attribute) {
    return Optional.ofNullable(entity.getAttribute(attribute));
  }

  /**
   * Spawns random firework at location
   *
   * @param location location to spawn firework there
   */
  public static void spawnRandomFirework(Location location) {
    Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta fwm = fw.getFireworkMeta();

    //Get the type
    FireworkEffect.Type type;
    switch(RANDOM.nextInt(4) + 1) {
      case 1:
        type = FireworkEffect.Type.BALL;
        break;
      case 2:
        type = FireworkEffect.Type.BALL_LARGE;
        break;
      case 3:
        type = FireworkEffect.Type.BURST;
        break;
      case 4:
        type = FireworkEffect.Type.CREEPER;
        break;
      case 5:
        type = FireworkEffect.Type.STAR;
        break;
      default:
        type = FireworkEffect.Type.BALL;
        break;
    }

    //Get our random colours
    int r1i = RANDOM.nextInt(250) + 1;
    int r2i = RANDOM.nextInt(250) + 1;
    Color c1 = Color.fromBGR(r1i);
    Color c2 = Color.fromBGR(r2i);

    //Create our effect with this
    FireworkEffect effect = FireworkEffect.builder().flicker(RANDOM.nextBoolean()).withColor(c1).withFade(c2)
        .with(type).trail(RANDOM.nextBoolean()).build();

    //Then apply the effect to the meta
    fwm.addEffect(effect);

    //Generate some random power and set it
    fwm.setPower(RANDOM.nextInt(2) + 1);
    fw.setFireworkMeta(fwm);
  }

  /**
   * Sends centered message in chat for player
   *
   * @param player  message receiver
   * @param message message content to send
   */
  public static void sendCenteredMessage(Player player, String message) {
    if(message == null || message.isEmpty()) {
      player.sendMessage("");
      return;
    }
    message = ChatColor.translateAlternateColorCodes('&', message);

    int messagePxSize = 0;
    boolean previousCode = false;
    boolean isBold = false;

    for(char c : message.toCharArray()) {
      if(c == '§') {
        previousCode = true;
      } else if(previousCode) {
        previousCode = false;
        isBold = c == 'l' || c == 'L';
      } else {
        DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
        messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
        messagePxSize++;
      }
    }
    int halvedMessageSize = messagePxSize / 2, toCompensate = 154 - halvedMessageSize,
        spaceLength = DefaultFontInfo.SPACE.getLength() + 1, compensated = 0;
    StringBuilder sb = new StringBuilder();
    while(compensated < toCompensate) {
      sb.append(' ');
      compensated += spaceLength;
    }
    player.sendMessage(sb.toString() + message);
  }


  public static void sendStartUpMessage(Plugin plugin, String pluginname, PluginDescriptionFile descriptionFile, boolean disclaimer, boolean support) {
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] -_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_-");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] Versions: ");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] Plugin: §6" + descriptionFile.getVersion() + " §r| Server: §6" + plugin.getServer().getVersion() + " §r| Detected: §6" + ServerVersion.Version.getCurrent() + " §r| Software: §6" + plugin.getServer().getName() + " §r| Java: §6" + System.getProperty("java.version"));
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] This plugin was created by §6Plugily Projects §ras part of an §6open source project§r ( https://donate.plugily.xyz )");
    if(plugin.getDescription().getVersion().contains("-debug") || plugin.getConfig().getBoolean("Debug")) {
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DEBUG] §eThe debug mode of this plugin is enabled");
    }
    if(plugin.getDescription().getVersion().contains("-b")) {
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][BETA] §eWe recognize that this is a beta build");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][BETA] §eAs beta already says this version is not meant for production servers!");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][BETA] §eWe are trying our best to provide you with nearly stable builds");
    }
    if(plugin.getDescription().getVersion().contains("-dev")) {
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DEV] §eWe recognize that this is a dev build");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DEV] §c!!!DO NOT USE THIS ON PRODUCTION!!!");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DEV] §eDev builds are just for test purposes and we do not provide any support to them!");
    }
    if(disclaimer) {
      if(ServerVersion.Version.isCurrentEqual(Version.v0_0_R0) || ServerVersion.Version.isCurrentLower(Version.v1_12_R1)) {
        Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
        if(ServerVersion.Version.isCurrentEqual(Version.v0_0_R0)) {
          Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DISCLAIMER] §cIt seems like our system does not know your Server version, you should contact our support!");
        }
        if(ServerVersion.Version.isCurrentLower(Version.v1_12_R1)) {
          Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DISCLAIMER] §cWe noticed that you are using an older version of Minecraft.");
          Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DISCLAIMER] §cPlease keep in mind that newer versions will help improving the security and performance of your server.");
          if(ServerVersion.Version.isCurrentLower(Version.v1_11_R1)) {
            Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][DISCLAIMER] §cWe do not give official support for old Minecraft versions as they are to outdated and slow down dev progress!");
          }
        }
      }
    }
    if(support && ServerVersion.Version.isCurrentEqualOrHigher(Version.v1_11_R1)) {
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][SUPPORT] If you have any problems, you can always contact us on our Discord server! ( https://discord.plugily.xyz )");
      Bukkit.getConsoleSender().sendMessage("[" + pluginname + "][SUPPORT] You can also check out our wiki at https://wiki.plugily.xyz");
    }
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] ");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "]                               §6The plugin got fully enabled! Enjoy the plugin ;)");
    Bukkit.getConsoleSender().sendMessage("[" + pluginname + "] -_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_--_-_-_-");
  }

}
