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

package plugily.projects.minigamesbox.classic.utils.items;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class ItemManager {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static List<HandlerItem> items = new ArrayList<>();

    private ItemManager() {
        throw new UnsupportedOperationException();
    }

    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("ItemManager is already registered");
        } else {
            Bukkit.getPluginManager().registerEvents(new ItemListener(plugin), plugin);
        }
    }

    public static List<HandlerItem> getItems() {
        return items;
    }

    public static final class ItemListener implements Listener {
        private final Plugin plugin;

        public ItemListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onItemDrop(PlayerDropItemEvent event) {
            HandlerItem handlerItem = getInteractItem(event.getItemDrop().getItemStack());
            if (handlerItem == null) {
                return;
            }
            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);
            handlerItem.handleDrop(event);
            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        }

        @EventHandler
        public void onConsumeEvent(PlayerItemConsumeEvent event) {
            HandlerItem handlerItem = getInteractItem(event.getItem());
            if (handlerItem == null) {
                return;
            }
            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);
            handlerItem.handleConsume(event);
            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        }

        @EventHandler
        public void onInteractEvent(CBPlayerInteractEvent event) {
            HandlerItem handlerItem = getInteractItem(event.getItem());
            if (handlerItem == null) {
                return;
            }
            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);
            handlerItem.handleInteract(event);
            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                ItemManager.getItems().clear();
                REGISTERED.set(false);
            }
        }

        private HandlerItem getInteractItem(ItemStack itemStack) {
            for (HandlerItem item : ItemManager.getItems()) {
                if (item.getItemStack() == itemStack) {
                    return item;
                }
            }
            return null;
        }
    }

}
