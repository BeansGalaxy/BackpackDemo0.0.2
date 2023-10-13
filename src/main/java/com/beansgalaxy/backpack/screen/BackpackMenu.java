package com.beansgalaxy.backpack.screen;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackMenu extends AbstractContainerMenu {
    public final BackpackEntity backpack;
    private Container container;
    private final Level level;
    public int yOffset = 45;

    public BackpackMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(Backpack.BACKPACK_MENU.get(), id, inventory, new BackpackEntity(inventory.player.level(), extraData.readNbt()));
    }

    public BackpackMenu(MenuType<?> type, int id, Inventory inventory, BackpackEntity entity) {
        super(type, id);
        this.backpack = entity;
        this.container = (Container) entity;
        this.level = inventory.player.level();
        checkContainerSize(entity, 6);
        entity.startOpen(inventory.player);

        for(int j = 0; j < 6; ++j) {
            this.addSlot(new Slot(entity, j, 44 + j * 18, 20));
        }

        for(int l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51 + yOffset));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 109 + yOffset));
        }


    }


    public boolean stillValid(Player player) {
        return true;
    }

    public ItemStack quickMoveStack(Player p_39651_, int p_39652_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39652_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_39652_ < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void removed(Player p_39251_) {
        super.removed(p_39251_);
        this.container.stopOpen(p_39251_);
    }
}