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


import org.bukkit.event.block.Action;
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

    private final List<Consumer<PlayerDropItemEvent>> dropHandlers;
    private final List<Consumer<PlayerItemConsumeEvent>> consumeHandlers;
    private final List<Consumer<PlugilyPlayerInteractEvent>> interactHandlers;
    private final ItemStack itemStack;
    private boolean rightClick = false;
    private boolean leftClick = false;
    private boolean physical = false;

    public HandlerItem(ItemStack itemStack) {
        this.dropHandlers = new ArrayList<>();
        this.consumeHandlers = new ArrayList<>();
        this.interactHandlers = new ArrayList<>();
        this.itemStack = itemStack;
        ItemManager.getItems().add(this);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void addDropHandler(Consumer<PlayerDropItemEvent> dropHandler) {
        dropHandlers.add(dropHandler);
    }

    public void addConsumeHandler(Consumer<PlayerItemConsumeEvent> consumeHandler) {
        consumeHandlers.add(consumeHandler);
    }

    public void addInteractHandler(Consumer<PlugilyPlayerInteractEvent> interactHandler) {
        interactHandlers.add(interactHandler);
    }

    void handleDrop(PlayerDropItemEvent event) {
        this.dropHandlers.forEach((c) -> c.accept(event));
    }

    void handleConsume(PlayerItemConsumeEvent event) {
        this.consumeHandlers.forEach((c) -> c.accept(event));
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
        this.interactHandlers.forEach((c) -> c.accept(event));
    }


    public void setRightClick(boolean rightClick) {
        this.rightClick = rightClick;
    }

    public void setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
    }

    public void setPhysical(boolean physical) {
        this.physical = physical;
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

    public void remove() {
        ItemManager.getItems().remove(this);
    }

}
