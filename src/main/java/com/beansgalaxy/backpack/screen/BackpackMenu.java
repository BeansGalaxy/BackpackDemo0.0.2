package com.beansgalaxy.backpack.screen;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import net.minecraft.core.NonNullList;
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
    public int invOffset = 114;
    private int bpOffset = 2;


    public BackpackMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(Backpack.BACKPACK_MENU.get(), id, inventory, new BackpackEntity(inventory.player.level(), extraData.readNbt()));
    }

    public BackpackMenu(MenuType<?> type, int id, Inventory inventory, BackpackEntity entity) {
        super(type, id);
        this.backpack = entity;
        this.container = (Container) entity;
        this.level = inventory.player.level();
        checkContainerSize(entity, 7);
        entity.startOpen(inventory.player);

        this.addSlot(new Slot(entity, 0, -111, -180));
        this.addSlot(new Slot(entity, 1, 80, 60));
        createSlots(entity);

        for(int l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51 + invOffset));
            }
        }
        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 109 + invOffset));
        }

    }

    private void createSlots(Container container) {
        int columns = 15;
        int rows = 4;
        int spacing = 17;
        int columnOffset = 1;
        int invCenter = 8 + (4 * 18); // = 80
        int bpCenter = (columns / 2) * spacing;        // = -54
        int x = invCenter - bpCenter;
        int y = invOffset - (rows * spacing) + 32;

        for(int r = 0; r < rows; ++r)
            for(int j = 0; j < columns; ++j)
                this.addSlot(new Slot(container, j + 2 + r * columns, x + j * spacing, y + r * spacing) {
                    public boolean mayPlace(ItemStack p_40231_) {
                        return false;
                    }
                });
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
                if (slot.container == this.container) {
                    for (int j = slot.index; j < 62; j++) {
                        NonNullList<ItemStack> itemList = backpack.getItemStacks();
                        itemList.set(j, itemList.get(j + 1));
                    }
                }
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