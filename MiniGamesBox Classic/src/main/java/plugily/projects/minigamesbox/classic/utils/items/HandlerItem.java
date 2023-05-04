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
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class HandlerItem {

    private final List<Consumer<PlayerDropItemEvent>> dropHandlers = new ArrayList<>();
    private final List<Consumer<PlayerItemConsumeEvent>> consumeHandlers = new ArrayList<>();
    private final List<Consumer<PlugilyPlayerInteractEvent>> interactHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> inventoryClickHandlers = new ArrayList<>();
    private final ItemStack itemStack;
    private boolean rightClick = false;
    private boolean leftClick = false;
    private boolean physical = false;
    private boolean movementCancel = false;

    private boolean active = false;

    public HandlerItem(ItemStack handlerItemStack) {
        itemStack = handlerItemStack;
    }

    public ItemStack getItemStack() {
        build();
        return itemStack;
    }

    public HandlerItem addDropHandler(Consumer<PlayerDropItemEvent> dropHandler) {
        dropHandlers.add(dropHandler);
        return this;
    }

    public HandlerItem addConsumeHandler(Consumer<PlayerItemConsumeEvent> consumeHandler) {
        consumeHandlers.add(consumeHandler);
        return this;
    }

    public HandlerItem addInteractHandler(Consumer<PlugilyPlayerInteractEvent> interactHandler) {
        interactHandlers.add(interactHandler);
        return this;
    }

    public HandlerItem addInventoryClickHandler(Consumer<InventoryClickEvent> inventoryClickHandler) {
        inventoryClickHandlers.add(inventoryClickHandler);
        return this;
    }

    public HandlerItem setMovementCancel(boolean cancelMovement) {
        movementCancel = cancelMovement;
        return this;
    }

    void handleDrop(PlayerDropItemEvent event) {
        dropHandlers.forEach((consumer) -> consumer.accept(event));
    }

    void handleConsume(PlayerItemConsumeEvent event) {
        consumeHandlers.forEach((consumer) -> consumer.accept(event));
    }

    void handleInteract(PlugilyPlayerInteractEvent event) {
        boolean interactPermit = false;
        if (physical) {
            if (isPhysical(event)) {
                interactPermit = true;
            }
        }
        if (leftClick) {
            if (isLeftClick(event)) {
                interactPermit = true;
            }
        }
        if (rightClick) {
            if (isRightClick(event)) {
                interactPermit = true;
            }
        }
        if (!interactPermit) {
            return;
        }
        interactHandlers.forEach((consumer) -> consumer.accept(event));
    }

    void handleInventoryClickEvent(InventoryClickEvent event) {
        if (movementCancel) {
            if (event.getWhoClicked().getGameMode() != GameMode.SURVIVAL) {
                event.setCancelled(false);
                return;
            }
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            return;
        }
        inventoryClickHandlers.forEach(consumer -> consumer.accept(event));
    }


    public HandlerItem setRightClick(boolean clickRight) {
        rightClick = clickRight;
        return this;
    }

    public HandlerItem setLeftClick(boolean clickLeft) {
        leftClick = clickLeft;
        return this;
    }

    public HandlerItem setPhysical(boolean actionPhysical) {
        physical = actionPhysical;
        return this;
    }

    private boolean isRightClick(PlugilyPlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            return true;
        }
        return event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    private boolean isLeftClick(PlugilyPlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            return true;
        }
        return event.getAction() == Action.LEFT_CLICK_BLOCK;
    }

    private boolean isPhysical(PlugilyPlayerInteractEvent event) {
        return event.getAction() == Action.PHYSICAL;
    }

    public HandlerItem build() {
        if (!active) {
            ItemManager.addItem(this);
            active = true;
        }
        return this;
    }

    public void remove() {
        ItemManager.removeItem(this);
        active = false;
    }

}
