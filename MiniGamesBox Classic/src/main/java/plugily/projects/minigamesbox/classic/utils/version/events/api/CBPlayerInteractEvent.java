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

package plugily.projects.minigamesbox.classic.utils.version.events.api;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CBPlayerInteractEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final ItemStack itemStack;
  private final EquipmentSlot equipmentSlot;
  private final Action action;
  private final BlockFace blockFace;
  private final Block clickedBlock;
  private final Material material;
  private final boolean hasItem;
  private final boolean hasBlock;

  public CBPlayerInteractEvent(Player player, ItemStack itemStack, EquipmentSlot equipmentSlot, Action action, BlockFace blockFace, Block clickedBlock, Material material, boolean hasItem, boolean hasBlock) {
    super(false);
    this.player = player;
    this.itemStack = itemStack;
    this.equipmentSlot = equipmentSlot;
    this.action = action;
    this.blockFace = blockFace;
    this.clickedBlock = clickedBlock;
    this.material = material;
    this.hasItem = hasItem;
    this.hasBlock = hasBlock;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public ItemStack getItem() {
    return itemStack;
  }

  public EquipmentSlot getHand() {
    return equipmentSlot;
  }

  public Action getAction() {
    return action;
  }

  public BlockFace getBlockFace() {
    return blockFace;
  }

  public Block getClickedBlock() {
    return clickedBlock;
  }

  public Material getMaterial() {
    return material;
  }

  public boolean hasItem() {
    return hasItem;
  }

  public boolean hasBlock() {
    return hasBlock;
  }
}
