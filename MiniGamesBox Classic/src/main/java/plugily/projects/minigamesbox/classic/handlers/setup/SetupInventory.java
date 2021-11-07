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


package plugily.projects.minigamesbox.classic.handlers.setup;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.handlers.setup.components.ArenaRegisterComponent;
import plugily.projects.minigamesbox.classic.handlers.setup.components.MiscComponents;
import plugily.projects.minigamesbox.classic.handlers.setup.components.PlayerAmountComponents;
import plugily.projects.minigamesbox.classic.handlers.setup.components.SpawnComponents;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.inventory.normal.FastInv;

import java.util.Random;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plugily.xyz";
  private static final Random random = new Random();
  private static Main plugin;
  private final FileConfiguration config;
  private final Arena arena;
  private final Player player;
  private FastInv gui;
  private final SetupUtilities setupUtilities;

  public SetupInventory(Arena arena, Player player) {
    config = ConfigUtils.getConfig(plugin, "arenas");
    this.arena = arena;
    this.player = player;
    setupUtilities = new SetupUtilities(plugin, config, arena);
    prepareGui();
  }

  public static void init(Main plugin) {
    SetupInventory.plugin = plugin;
  }

  private void prepareGui() {
    gui = new FastInv(18, "Village Defense Arena Setup");

    prepareComponents(gui);
  }

  private void prepareComponents(FastInv gui) {
    SpawnComponents spawnComponents = new SpawnComponents();
    spawnComponents.prepare(this);
    spawnComponents.injectComponents(gui);

    PlayerAmountComponents playerAmountComponents = new PlayerAmountComponents();
    playerAmountComponents.prepare(this);
    playerAmountComponents.injectComponents(gui);

    MiscComponents miscComponents = new MiscComponents();
    miscComponents.prepare(this);
    miscComponents.injectComponents(gui);

    ArenaRegisterComponent arenaRegisterComponent = new ArenaRegisterComponent();
    arenaRegisterComponent.prepare(this);
    arenaRegisterComponent.injectComponents(gui);
  }

  private void sendProTip(Player p) {
    switch(random.nextInt(7 + 1)) {
      case 0:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We also got premade setups, check them out on &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/setup/maps"));
        break;
      case 1:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz"));
        break;
      case 2:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7PlaceholderApi plugin is supported with our plugin! Check here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/placeholders/placeholderapi"));
        break;
      case 3:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame: https://patreon.com/plugily"));
        break;
      case 4:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/"));
        break;
      case 5:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + " &7or discord https://discord.plugily.xyz"));
        break;
      case 6:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7If you like our plugins: You can support us on patreon https://patreon.com/plugily"));
        break;
      case 7:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/" + plugin.getPluginNamePrefixLong().toLowerCase()));
        break;
      default:
        break;
    }
  }

  public void openInventory() {
    sendProTip(player);
    gui.open(player);
  }

  public Main getPlugin() {
    return plugin;
  }

  public FileConfiguration getConfig() {
    return config;
  }

  public Arena getArena() {
    return arena;
  }

  public Player getPlayer() {
    return player;
  }

  public FastInv getGui() {
    return gui;
  }

  public SetupUtilities getSetupUtilities() {
    return setupUtilities;
  }
}
