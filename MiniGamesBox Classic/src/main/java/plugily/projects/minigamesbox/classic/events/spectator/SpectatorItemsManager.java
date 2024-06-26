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
package plugily.projects.minigamesbox.classic.events.spectator;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.events.spectator.settings.SpectatorSettingsMenu;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.minigamesbox.number.NumberUtils;

import java.util.Collections;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class SpectatorItemsManager implements Listener {

  private final PluginMain plugin;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemsManager(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin);
  }

  public void openSpectatorMenu(Player player, IPluginArena arena) {
    NormalFastInv gui = new NormalFastInv(plugin.getBukkitHelper().serializeInt(arena.getPlayers().size()), new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_MENU_NAME").asKey().build());

    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack cloneSkull = skull.clone();
      SkullMeta meta = VersionUtils.setPlayerHead(arenaPlayer, (SkullMeta) cloneSkull.getItemMeta());
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(new MessageBuilder("IN_GAME_SPECTATOR_TARGET_PLAYER_HEALTH").asKey().integer((int) NumberUtils.round(arenaPlayer.getHealth(), 2)).player(arenaPlayer).arena(arena).build()));
      cloneSkull.setItemMeta(meta);
      gui.addItem(cloneSkull, event -> {
        new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_TELEPORT").asKey().arena(arena).player(arenaPlayer).send(event.getWhoClicked());
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        VersionUtils.teleport(humanEntity, arenaPlayer.getLocation());
      });
    }
    gui.open(player);
  }

  public SpectatorSettingsMenu getSpectatorSettingsMenu() {
    return spectatorSettingsMenu;
  }
}
