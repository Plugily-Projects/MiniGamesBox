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

package plugily.projects.minigamesbox.classic.utils.version;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticleLegacy;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public final class VersionUtils {

  private static boolean isPaper, isParticleBuilderSupported;
  private static Class<?> iChatBaseComponent, chatMessageTypeClass;
  private static Constructor<?> packetPlayOutChatConstructor, chatComponentTextConstructor, titleConstructor;
  private static Object chatMessageType, titleField, subTitleField;
  public static final List<String> PARTICLE_VALUES;

  static {
    try {
      Class.forName("com.destroystokyo.paper.PaperConfig");
      isPaper = true;
    } catch(ClassNotFoundException e) {
      isPaper = false;
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      PARTICLE_VALUES = Stream.of(Particle.values()).map(Enum::toString).filter(string -> !string.contains("legacy")).collect(Collectors.toList());
    } else {
      PARTICLE_VALUES = Stream.of(XParticleLegacy.values()).map(Enum::toString).collect(Collectors.toList());
    }

    try {
      Particle.class.getMethod("builder");
      isParticleBuilderSupported = true;
    } catch(NoSuchMethodException | NoClassDefFoundError ignored) {
    }

    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R2)) {
      iChatBaseComponent = PacketUtils.classByName("net.minecraft.network.chat", "IChatBaseComponent");
      chatMessageTypeClass = PacketUtils.classByName("net.minecraft.network.chat", "ChatMessageType");

      if(chatMessageTypeClass != null) {
        for(Object obj : chatMessageTypeClass.getEnumConstants()) {
          if(obj.toString().equalsIgnoreCase("GAME_INFO") || obj.toString().equalsIgnoreCase("ACTION_BAR")) {
            chatMessageType = obj;
            break;
          }
        }
      }
      try {
        Class<?> chatcomponentTextClass = PacketUtils.classByName("net.minecraft.network.chat", "ChatComponentText");

        if(chatcomponentTextClass != null) {
          chatComponentTextConstructor = chatcomponentTextClass.getConstructor(String.class);
        }

        Class<?> packetPlayOutChatClass = PacketUtils.classByName("net.minecraft.network.protocol.game", "PacketPlayOutChat");

        if(packetPlayOutChatClass != null) {
          if(chatMessageTypeClass == null) {
            packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponent, byte.class);
          } else if(chatMessageType != null) {
            try {
              packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponent,
                  chatMessageTypeClass);
            } catch(NoSuchMethodException e) {
              packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponent,
                  chatMessageTypeClass, UUID.class);
            }
          }
        }


        Class<?> playOutTitle = PacketUtils.classByName("net.minecraft.network.protocol.game", "PacketPlayOutTitle");
        Class<?>[] titleDeclaredClasses = playOutTitle.getDeclaredClasses();

        if(titleDeclaredClasses.length > 0) {
          titleConstructor = playOutTitle.getConstructor(titleDeclaredClasses[0], iChatBaseComponent, int.class, int.class, int.class);
          titleField = titleDeclaredClasses[0].getField("TITLE").get(null);
          subTitleField = titleDeclaredClasses[0].getField("SUBTITLE").get(null);
        }
      } catch(NoSuchMethodException | NoClassDefFoundError | NoSuchFieldException | IllegalAccessException ignored) {
      }
    }
  }

  private VersionUtils() {
  }

  public static boolean isPaper() {
    return isPaper;
  }

  public static boolean checkOffHand(EquipmentSlot equipmentSlot) {
    return ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1) && equipmentSlot == EquipmentSlot.OFF_HAND;
  }

  public static SkullMeta setPlayerHead(Player player, SkullMeta meta) {
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_12_R1)) {
      meta.setOwner(player.getName());
    } else if(isPaper) {
      if(player.getPlayerProfile().hasTextures()) {
        meta.setPlayerProfile(player.getPlayerProfile());
      }
    } else {
      meta.setOwningPlayer(player);
    }
    return meta;
  }

  private static JavaPlugin plugin;

  public static CompletableFuture<Boolean> teleport(Entity entity, Location location) {
    if(isPaper) {
      // Avoid Future.get() method to be called from the main thread

      if(Bukkit.isPrimaryThread()) { // Checks if the current thread is not async
        return PaperLib.teleportAsync(entity, location);
      }

      if(plugin == null)
        plugin = JavaPlugin.getPlugin(PluginMain.class);

      try {
        return Bukkit.getScheduler().callSyncMethod(plugin, () -> PaperLib.teleportAsync(entity, location)).get();
      } catch(InterruptedException | ExecutionException e) {
        e.printStackTrace();
      } catch(CancellationException e) {
        // ignored
      }
    } else {
      entity.teleport(location);
    }
    return new CompletableFuture<>();
  }

  public static void sendParticles(String particleName, Player player, Location location, int count) {
    if(!isPaper && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      Particle particle = XParticle.getParticle(particleName);
      location.getWorld().spawnParticle(particle, location, count, 0, 0, 0, 0, getParticleDataType(particle, location));
    } else if(isParticleBuilderSupported) {
      Particle particle = XParticle.getParticle(particleName);
      Object dataType = getParticleDataType(particle, location);

      if(dataType == null) {
        particle.builder().location(location).count(count).extra(0).spawn();
      } else {
        particle.builder().location(location).data(dataType).count(count).extra(0).spawn();
      }
    } else {
      try {
        XParticleLegacy.valueOf(particleName).sendToPlayer(player, location, 0, 0, 0, 0, count);
      } catch(Exception ignored) {
      }
    }
  }

  public static void sendParticles(String particleName, Set<Player> players, Location location, int count) {
    if(!isPaper && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      Particle particle = XParticle.getParticle(particleName);
      location.getWorld().spawnParticle(particle, location, count, 0, 0, 0, 0, getParticleDataType(particle, location));
    } else if(isParticleBuilderSupported) {
      Particle particle = XParticle.getParticle(particleName);
      Object dataType = getParticleDataType(particle, location);

      if(dataType == null) {
        particle.builder().location(location).count(count).extra(0).spawn();
      } else {
        particle.builder().location(location).data(dataType).count(count).extra(0).spawn();
      }
    } else {
      try {
        XParticleLegacy.valueOf(particleName).sendToPlayers(players == null ? Bukkit.getOnlinePlayers() : players, location, 0, 0, 0, 0, count, true);
      } catch(Exception ignored) {
      }
    }
  }

  public static void sendParticles(String particle, Set<Player> players, Location location, int count, double offsetX, double offsetY, double offsetZ) {
    sendParticles(particle, players, location, count, offsetX, offsetY, offsetZ, 0);
  }

  public static void sendParticles(String particleName, Set<Player> players, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
    if(!isPaper && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      Particle particle = XParticle.getParticle(particleName);
      Object dataType = getParticleDataType(particle, location);

      if(dataType != null) {
        location.getWorld().spawnParticle(particle, location, count, 0, 0, 0, extra, dataType);
      } else {
        location.getWorld().spawnParticle(particle, location, count, 0, 0, 0, extra);
      }
    } else if(isParticleBuilderSupported) {
      Particle particle = XParticle.getParticle(particleName);
      Object dataType = getParticleDataType(particle, location);

      if(dataType == null) {
        particle.builder().location(location).count(count).offset(offsetX, offsetY, offsetZ).extra(extra).spawn();
      } else {
        particle.builder().location(location).data(dataType).count(count).offset(offsetX, offsetY, offsetZ).extra(extra).spawn();
      }
    } else {
      try {
        XParticleLegacy.valueOf(particleName).sendToPlayers(players == null ? Bukkit.getOnlinePlayers() : players, location, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, true);
      } catch(Exception ignored) {
      }
    }
  }

  private static org.bukkit.Particle.DustTransition dustTransition;

  // Some particle in new versions needs their own data type
  // See https://www.spigotmc.org/threads/343001/
  private static Object getParticleDataType(Particle particle, Location location) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R2) && particle == Particle.REDSTONE) {
      return new Particle.DustOptions(Color.RED, 2);
    }

    if(particle == Particle.ITEM_CRACK) {
      return new ItemStack(location.getBlock().getType());
    }

    if(particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST
        || (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R2) && particle == Particle.FALLING_DUST)
        || (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_18_R1) && particle == Particle.BLOCK_MARKER)) {
      return location.getBlock().getType().createBlockData();
    }

    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_13_R1) && (particle == Particle.LEGACY_BLOCK_CRACK
        || particle == Particle.LEGACY_BLOCK_DUST || particle == Particle.LEGACY_FALLING_DUST)) {
      org.bukkit.Material type = location.getBlock().getType();

      try {
        return type.getData().getDeclaredConstructor().newInstance(type);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_19_R1) && (particle == Particle.SCULK_CHARGE || particle == Particle.SHRIEK)) {
      return 0;
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1)) {
      if(particle == Particle.DUST_COLOR_TRANSITION) {
        if(dustTransition == null) {
          dustTransition = new org.bukkit.Particle.DustTransition(Color.fromRGB(255, 0, 0), Color.fromRGB(255, 255, 255), 1.0F);
        }
        return dustTransition;
      }
      /*
      try {
        if(particle == Particle.VIBRATION) {
          if(isPaper) {
            return new org.bukkit.Vibration(new org.bukkit.Vibration.Destination.BlockDestination(location), 40);
          }
          return new org.bukkit.Vibration(location, new org.bukkit.Vibration.Destination.BlockDestination(location), 40);
        }
      } catch(NoClassDefFoundError ignored) {
      }*/
    }
    return null;
  }

  @Deprecated
  public static List<String> getParticleValues() {
    return PARTICLE_VALUES;
  }

  public static void updateNameTagsVisibility(Player player, Player other, String tag, boolean remove) {
    Scoreboard scoreboard = other.getScoreboard();
    if(scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
      scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    Team team = scoreboard.getTeam(tag);
    if(team == null) {
      team = scoreboard.registerNewTeam(tag);
    }
    team.setCanSeeFriendlyInvisibles(false);
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_11_R1)) {
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    } else {
      team.setNameTagVisibility(NameTagVisibility.NEVER);
    }
    if(!remove) {
      team.addEntry(player.getName());
    } else {
      team.removeEntry(player.getName());
    }
    other.setScoreboard(scoreboard);
  }


  public static Entity getPassenger(Entity ent) {
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R2)) {
      return ent.getPassenger();
    } else if(!ent.getPassengers().isEmpty()) {
      return ent.getPassengers().get(0);
    }

    return null;
  }

  public static void setDurability(ItemStack item, short durability) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      ItemMeta meta = item.getItemMeta();
      if(meta instanceof Damageable) {
        ((Damageable) meta).setDamage(durability);
      }
    } else {
      item.setDurability(durability);
    }
  }

  public static void hidePlayer(JavaPlugin plugin, Player to, Player p) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      to.hidePlayer(plugin, p);
    } else {
      to.hidePlayer(p);
    }
  }

  public static void showPlayer(JavaPlugin plugin, Player to, Player p) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      to.showPlayer(plugin, p);
    } else {
      to.showPlayer(p);
    }
  }

  public static void setPassenger(Entity to, Entity... passengers) {
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R2)) {
      for(Entity ps : passengers) {
        to.setPassenger(ps);
      }
    } else {
      for(Entity ps : passengers) {
        to.addPassenger(ps);
      }
    }
  }

  public static void sendTextComponent(CommandSender sender, TextComponent component) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      if(sender instanceof Player) {
        ((Player) sender).spigot().sendMessage(component);
      } else {
        sender.sendMessage(component.getText());
      }
    } else {
      sender.spigot().sendMessage(component);
    }
  }

  public static void setGlowing(Player player, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      player.setGlowing(value);
    }
  }

  public static void setGlowing(Entity entity, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      entity.setGlowing(value);
    }
  }

  public static void setCollidable(Player player, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      //player.spigot().setCollidesWithEntities(value);
    } else {
      player.setCollidable(value);
    }
  }

  public static void setCollidable(ArmorStand stand, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      //stand.spigot().setCollidesWithEntities(value);
    } else {
      stand.setCollidable(value);
    }
  }

  @Deprecated //bad naming
  public static double getHealth(Player player) {
    return getMaxHealth(player);
  }

  public static double getMaxHealth(LivingEntity entity) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return entity.getMaxHealth();
    }

    java.util.Optional<org.bukkit.attribute.AttributeInstance> at = MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
    return at.map(AttributeInstance::getValue).orElse(20D);
  }

  public static void setMaxHealth(Player player, double health) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      player.setMaxHealth(health);
    } else {
      MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(health));
    }
  }

  public static void setMaxHealth(LivingEntity entity, double health) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      entity.setMaxHealth(health);
    } else {
      MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(health));
    }
  }

  public static ItemStack getItemInHand(Player player) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return player.getItemInHand();
    }
    return player.getInventory().getItemInMainHand();
  }

  public static void setMaterialCooldown(HumanEntity entity, Material material, int ticks) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      ///no method on 1.8!
      return;
    }
    entity.setCooldown(material, ticks);
  }

  public static void setItemInHand(Player player, ItemStack stack) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      player.setItemInHand(stack);
      return;
    }
    player.getInventory().setItemInMainHand(stack);
  }

  public static void setItemInHand(LivingEntity entity, ItemStack stack) {
    org.bukkit.inventory.EntityEquipment equipment = entity.getEquipment();

    if(equipment == null) {
      return;
    }
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      equipment.setItemInHand(stack);
      return;
    }
    equipment.setItemInMainHand(stack);
  }

  public static void setItemInHandDropChance(LivingEntity entity, float chance) {
    org.bukkit.inventory.EntityEquipment equipment = entity.getEquipment();

    if(equipment == null) {
      return;
    }
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      equipment.setItemInHandDropChance(chance);
      return;
    }
    equipment.setItemInMainHandDropChance(chance);
  }

  @Deprecated //for outside use, recommend to use ActionBarManager!
  public static void sendActionBar(Player player, String message) {
    if(player == null)
      return;
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R1)) {
      try {
        if(chatMessageTypeClass == null) {
          PacketUtils.sendPacket(player, packetPlayOutChatConstructor.newInstance(chatComponentTextConstructor.newInstance(message), (byte) 2));
        } else if(chatMessageType != null) {
          if(packetPlayOutChatConstructor.getParameterCount() == 2) {
            PacketUtils.sendPacket(player, packetPlayOutChatConstructor.newInstance(chatComponentTextConstructor.newInstance(message), chatMessageType));
          } else {
            PacketUtils.sendPacket(player, packetPlayOutChatConstructor.newInstance(chatComponentTextConstructor.newInstance(message), chatMessageType, player.getUniqueId()));
          }
        }
      } catch(ReflectiveOperationException e) {
        e.printStackTrace();
      }
    } else if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R3)) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, player.getUniqueId(), new ComponentBuilder(message).create());
    } else {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
  }

  public static void sendTitles(Player player, String title, String subtitle, int fadeInTime, int showTime,
                                int fadeOutTime) {
    if(player == null)
      return;
    if(title == null && subtitle == null) {
      return;
    }
    //avoid null on title
    if(title == null) {
      title = "";
    }
    if(subtitle == null) {
      subtitle = "";
    }

    sendTitle(player, title, fadeInTime, showTime, fadeOutTime);
    sendSubTitle(player, subtitle, fadeInTime, showTime, fadeOutTime);
  }

  public static void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(player == null)
      return;
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R2)) {
      try {
        Object chatTitle = null;
        Class<?>[] declaredClasses = iChatBaseComponent.getDeclaredClasses();
        if(declaredClasses.length > 0) {
          chatTitle = declaredClasses[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");
        } else if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_R2)) {
          Class<?> chatSerializer = PacketUtils.classByName(null, "ChatSerializer");
          chatTitle = iChatBaseComponent.cast(chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + text + "\"}"));
        }

        PacketUtils.sendPacket(player, titleConstructor.newInstance(titleField, chatTitle, fadeInTime, showTime, fadeOutTime));
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static void sendSubTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(player == null)
      return;
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R2)) {
      try {
        Object chatTitle = null;
        Class<?>[] declaredClasses = iChatBaseComponent.getDeclaredClasses();
        if(declaredClasses.length > 0) {
          chatTitle = declaredClasses[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");
        } else if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_R2)) {
          Class<?> chatSerializer = PacketUtils.classByName(null, "ChatSerializer");
          chatTitle = iChatBaseComponent.cast(chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + text + "\"}"));
        }

        PacketUtils.sendPacket(player, titleConstructor.newInstance(subTitleField, chatTitle, fadeInTime, showTime, fadeOutTime));
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(null, text, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static ItemStack getPotion(PotionType type, int tier, boolean splash) {
    ItemStack potion;
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      potion = new Potion(type, tier, splash).toItemStack(1);
    } else {
      potion = new ItemStack(!splash ? Material.POTION : Material.SPLASH_POTION, 1);
      PotionMeta meta = (PotionMeta) potion.getItemMeta();
      meta.setBasePotionData(new PotionData(type, false, tier >= 2 && !splash));
      potion.setItemMeta(meta);
    }
    return potion;
  }

  public static void playSound(Location loc, String sound) {
    XSound.matchXSound(sound).orElse(XSound.BLOCK_ANVIL_HIT).play(loc, 1, 1);
  }

  public static int getWorldMaxHeight(World world) {
    return world.getMaxHeight();
  }

  public static int getWorldMinHeight(World world) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R3)) {
      return world.getMinHeight();
    }
    return 0;
  }

  public static Entity spawnEntity(Location location, EntityType entityType) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_15_R2)) {
      return location.getWorld().spawnEntity(location, entityType);
    }
    if(isPaper) {
      return location.getWorld().spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
    } else {
      return location.getWorld().spawnEntity(location, entityType, false);
    }
  }

}
