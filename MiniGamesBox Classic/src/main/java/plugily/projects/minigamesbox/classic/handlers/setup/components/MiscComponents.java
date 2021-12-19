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


package plugily.projects.minigamesbox.classic.handlers.setup.components;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.sign.ArenaSign;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.FastInv;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class MiscComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(FastInv gui) {
    PluginArena arena = setupInventory.getArena();
    if(arena == null) {
      return;
    }
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    PluginMain plugin = setupInventory.getPlugin();
    ItemStack bungeeItem;
    if(!plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
      ItemBuilder itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial());
      itemBuilder.name(plugin.getChatManager().colorRawMessage("&e&lAdd Game Sign"));
      itemBuilder.lore(ChatColor.GRAY + "Target a sign and click this.");
      itemBuilder.lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)");
      bungeeItem = itemBuilder
          .build();
    } else {
      bungeeItem = new ItemBuilder(Material.BARRIER)
          .name(plugin.getChatManager().colorRawMessage("&c&lAdd Game Sign"))
          .lore(ChatColor.GRAY + "Option disabled with Bungee Cord module.")
          .lore(ChatColor.DARK_GRAY + "Bungee mode is meant to be one arena per server")
          .lore(ChatColor.DARK_GRAY + "If you wish to have multi arena, disable bungee in config!")
          .build();
    }
    gui.setItem(5, bungeeItem, e -> {
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        return;
      }
      e.getWhoClicked().closeInventory();

      Location location = player.getTargetBlock(null, 10).getLocation();
      Block block = location.getBlock();

      if(!(block.getState() instanceof Sign)) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cPlease look at sign to add as a game sign!"));
        return;
      }

      if(location.distance(e.getWhoClicked().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()
          && e.getClick() != ClickType.SHIFT_LEFT) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
            + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so."));
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&cYou can ignore this warning and add sign with Shift + Left Click, but for now &c&loperation is cancelled"));
        return;
      }

      plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) block.getState(), arena));
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("SIGNS_CREATED"));

      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(location.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0,0.0");
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    });

    gui.setItem(6, new ItemBuilder(Material.NAME_TAG)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Map Name"))
        .lore(ChatColor.GRAY + "Click to set arena map name")
        .lore("", plugin.getChatManager().colorRawMessage("&a&lCurrently: &e" + config.getString("instances." + arena.getId() + ".mapname")))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(plugin).withPrompt(new StringPrompt() {
        @Override
        public String getPromptText(ConversationContext context) {
          return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat arena name! You can use color codes.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          String name = plugin.getChatManager().colorRawMessage(input);
          player.sendRawMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + arena.getId() + " set to " + name));
          arena.setMapName(name);
          config.set("instances." + arena.getId() + ".mapname", arena.getMapName());
          ConfigUtils.saveConfig(plugin, config, "arenas");

          new SetupInventory(arena, player).openInventory();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    });

    gui.setItem(16, new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&6&l► Patreon Addon ◄ &8(AD)"))
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/addon/overview"));
    });

    gui.setItem(17, new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lView Setup Video"))
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check out this video: " + SetupInventory.VIDEO_LINK));
    });
  }

}
