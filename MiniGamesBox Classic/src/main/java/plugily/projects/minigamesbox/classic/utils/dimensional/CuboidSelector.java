/*
 * TheBridge - Defend your base and try to wipe out the others
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package plugily.projects.minigamesbox.classic.utils.dimensional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.items.HandlerItem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer
 * <p>Created at 18.12.2020
 */
public class CuboidSelector implements Listener {

  private final PluginMain plugin;
  private final Map<Player, Selection> selections = new HashMap<>();

  public CuboidSelector(PluginMain plugin) {
    this.plugin = plugin;
  }

  public void giveSelectorWand(Player p) {

    ItemStack stack =
        new ItemBuilder(Material.BLAZE_ROD)
            .name("&6&lLocation wand")
            .lore("Use this tool to set up location cuboids")
            .lore("Set the first corner with left click")
            .lore("and the second with right click")
            .colorizeItem().build();

    HandlerItem selectorItem = new HandlerItem(stack);
    selectorItem.setLeftClick(true);
    selectorItem.setRightClick(true);
    selectorItem.addDropHandler(dropEvent -> {
      dropEvent.setCancelled(false);
      dropEvent.getItemDrop().remove();
      dropEvent.getPlayer().updateInventory();
      selectorItem.remove();
    });
    selectorItem.addInteractHandler(event -> {
      event.setCancelled(true);
      switch(event.getAction()) {
        case LEFT_CLICK_BLOCK:
          selections.put(event.getPlayer(), new Selection(event.getClickedBlock().getLocation(), null));
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder("&eNow select top corner using right click!").prefix().build());
          break;
        case RIGHT_CLICK_BLOCK:
          if(!selections.containsKey(event.getPlayer())) {
            event.getPlayer()
                .sendMessage(
                    new MessageBuilder("&cPlease select bottom corner using left click first!")
                        .prefix()
                        .build());
            break;
          }
          selections.put(
              event.getPlayer(),
              new Selection(
                  selections.get(event.getPlayer()).getFirstPos(), event.getClickedBlock().getLocation()));
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder("&eNow you can add Location via menu!").prefix().build());
          break;
        case LEFT_CLICK_AIR:
          selections.put(event.getPlayer(), new Selection(event.getPlayer().getLocation(), null));
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder("&eNow select top corner using right click!").prefix().build());
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder(
                      "&cPlease keep in mind to use blocks instead of player location for precise coordinates!")
                      .prefix()
                      .build());
          break;
        case RIGHT_CLICK_AIR:
          if(!selections.containsKey(event.getPlayer())) {
            event.getPlayer()
                .sendMessage(
                    new MessageBuilder("&cPlease select bottom corner using left click first!")
                        .prefix()
                        .build());
            break;
          }
          selections.put(
              event.getPlayer(),
              new Selection(
                  selections.get(event.getPlayer()).getFirstPos(), event.getPlayer().getLocation()));
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder("&eNow you can add Location via menu!").prefix().build());
          event.getPlayer()
              .sendMessage(
                  new MessageBuilder(
                      "&cPlease keep in mind to use blocks instead of player location for precise coordinates!")
                      .prefix()
                      .build());
          break;
        default:
          break;
      }
    });

    p.getInventory().addItem(selectorItem.getItemStack());

    p.sendMessage(new MessageBuilder("&eYou received Location wand!").prefix().build());
    p.sendMessage(new MessageBuilder("&eSelect bottom corner using left click!").prefix().build());
  }

  public Selection getSelection(Player p) {
    return selections.getOrDefault(p, null);
  }

  public void removeSelection(Player p) {
    selections.remove(p);
  }

  public static class Selection {

    private final Location firstPos;
    private final Location secondPos;

    public Selection(Location firstPos, Location secondPos) {
      this.firstPos = firstPos;
      this.secondPos = secondPos;
    }

    public Location getFirstPos() {
      return firstPos;
    }

    public Location getSecondPos() {
      return secondPos;
    }
  }
}
