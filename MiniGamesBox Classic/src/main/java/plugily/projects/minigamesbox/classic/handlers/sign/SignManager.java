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

package plugily.projects.minigamesbox.classic.handlers.sign;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
//todo custom signs.yml (e.g. block behind sign)
public class SignManager implements Listener {

  private final PluginMain plugin;
  private final List<ArenaSign> arenaSigns = new ArrayList<>();
  private final Map<IArenaState, String> gameStateToString = new EnumMap<>(IArenaState.class);
  private final List<String> signLines;

  public SignManager(PluginMain plugin) {
    this.plugin = plugin;
    for(IArenaState arenaState : IArenaState.values()) {
      gameStateToString.put(arenaState, plugin.getLanguageManager().getLanguageMessage("Placeholders.Game-States." + arenaState.getFormattedName()));
    }

    signLines = plugin.getLanguageManager().getLanguageList("Signs.Lines");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    if(!event.getPlayer().hasPermission(plugin.getPluginNamePrefixLong() + ".admin.sign.create")
        || !ComplementAccessor.getComplement().getLine(event, 0).equalsIgnoreCase("[" + plugin.getPluginNamePrefixLong() + "]")) {
      return;
    }
    String line1 = ComplementAccessor.getComplement().getLine(event, 1);
    if(line1.isEmpty()) {
      new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().player(event.getPlayer()).sendPlayer();
      return;
    }
    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!arena.getId().equalsIgnoreCase(line1)) {
        continue;
      }
      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(event, i, new MessageBuilder(signLines.get(i)).arena(arena).build());
      }
      arenaSigns.add(new ArenaSign((Sign) event.getBlock().getState(), arena));
      new MessageBuilder("SIGNS_CREATED").asKey().player(event.getPlayer()).arena(arena).sendPlayer();
      String location = event.getBlock().getWorld().getName() + "," + event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ() + ",0.0,0.0";
      FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(location);
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      return;
    }
    new MessageBuilder("SIGNS_ARENA_NOT_FOUND").asKey().player(event.getPlayer()).sendPlayer();
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent event) {
    ArenaSign arenaSign = getArenaSignByBlock(event.getBlock());
    if(!event.getPlayer().hasPermission(plugin.getPluginNamePrefixLong() + ".admin.sign.break") || arenaSign == null) {
      return;
    }
    arenaSigns.remove(arenaSign);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(!config.isConfigurationSection("instances")) {
      return;
    }

    String location = event.getBlock().getWorld().getName() + "," + event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ() + "," + "0.0,0.0";
    for(String arena : config.getConfigurationSection("instances").getKeys(false)) {
      for(String sign : config.getStringList("instances." + arena + ".signs")) {
        if(!sign.equals(location)) {
          continue;
        }
        List<String> signs = config.getStringList("instances." + arena + ".signs");
        signs.remove(location);
        config.set("instances." + arena + ".signs", signs);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        new MessageBuilder("SIGNS_REMOVED").asKey().player(event.getPlayer()).sendPlayer();
        return;
      }
    }
    new MessageBuilder(ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!").prefix().player(event.getPlayer()).sendPlayer();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoinAttempt(PlugilyPlayerInteractEvent event) {
    if(event.getAction() != Action.RIGHT_CLICK_BLOCK || !(event.getClickedBlock().getState() instanceof Sign)) {
      return;
    }
    ArenaSign arenaSign = getArenaSignByBlock(event.getClickedBlock());
    if(arenaSign == null) {
      return;
    }
    IPluginArena arena = arenaSign.getArena();
    if(arena == null) {
      return;
    }
    plugin.getArenaManager().joinAttempt(event.getPlayer(), arena);
  }

  @Nullable
  private ArenaSign getArenaSignByBlock(Block block) {
    if(block == null) {
      return null;
    }
    for(ArenaSign sign : arenaSigns) {
      if(sign.getSign().getLocation().equals(block.getLocation())) {
        return sign;
      }
    }
    return null;
  }

  public void loadSigns() {
    plugin.getDebugger().debug("Signs load event started");
    long start = System.currentTimeMillis();

    arenaSigns.clear();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection("instances");
    if(section == null) {
      plugin.getDebugger().debug(Level.WARNING, "No arena instances found. Signs won't be loaded");
      return;
    }

    for(String path : section.getKeys(false)) {
      IPluginArena arena = plugin.getArenaRegistry().getArena(path);
      if(arena != null) {
        for(String sign : section.getStringList(path + ".signs")) {
          Location loc = LocationSerializer.getLocation(sign);
          if(loc != null) {
            org.bukkit.block.BlockState state = loc.getBlock().getState();
            if(state instanceof Sign) {
              arenaSigns.add(new ArenaSign((Sign) state, arena));
              if(ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_12)) {
                if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_20)) {
                  ((Sign) state).setWaxed(true);
                } else {
                  ((Sign) state).setEditable(false);
                }
              }
              plugin.getDebugger().debug(Level.WARNING, "Block at location {0} for arena {1} added as sign!", sign, path);
              continue;
            }
          }
          plugin.getDebugger().debug(Level.WARNING, "Block at location {0} for arena {1} is not a sign!", sign, path);
        }
      }
    }
    updateSigns();
    plugin.getDebugger().debug("Sign load event finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void updateSigns() {
    plugin.getDebugger().performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updating signs");
    long start = System.currentTimeMillis();

    for(ArenaSign arenaSign : arenaSigns) {
      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(arenaSign.getSign(), i, new MessageBuilder(signLines.get(i)).arena(arenaSign.getArena()).build());
      }
      if(plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true) && arenaSign.getBehind() != null) {
        Block behind = arenaSign.getBehind();
        try {
          switch(arenaSign.getArena().getArenaState()) {
            case WAITING_FOR_PLAYERS:
              behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, XMaterial.WHITE_STAINED_GLASS.getData());
              }
              break;
            case STARTING:
              behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, XMaterial.YELLOW_STAINED_GLASS.getData());
              }
              break;
            case IN_GAME:
              behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, XMaterial.ORANGE_STAINED_GLASS.getData());
              }
              break;
            case ENDING:
              behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, XMaterial.GRAY_STAINED_GLASS.getData());
              }
              break;
            case RESTARTING:
              behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, XMaterial.BLACK_STAINED_GLASS.getData());
              }
              break;
            default:
              break;
          }
        } catch(Exception ignored) {
        }
      }
      arenaSign.getSign().update();
    }
    plugin.getDebugger().performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updated signs took {0}ms", System.currentTimeMillis() - start);
  }

  public List<ArenaSign> getArenaSigns() {
    return arenaSigns;
  }

  public Map<IArenaState, String> getGameStateToString() {
    return gameStateToString;
  }
}
