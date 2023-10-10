package com.beansgalaxy.backpack.entity;

import com.beansgalaxy.backpack.Backpack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface BackpackContainer extends Container, MenuProvider {

    NonNullList<ItemStack> getItemStacks();
    Vec3 position();
    Level level();

    default int getContainerSize() {
        return 27;
    }
    default ItemStack getItem(int p_38218_) {
        return this.getItemStacks().get(p_38218_);
    }
    default ItemStack removeItem(int p_38220_, int p_38221_) {
        return ContainerHelper.removeItem(this.getItemStacks(), p_38220_, p_38221_);
    }
    default ItemStack removeItemNoUpdate(int i) {
        ItemStack itemstack = this.getItemStacks().get(i);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.getItemStacks().set(i, ItemStack.EMPTY);
            return itemstack;
        }
    }
    default void setItem(int i, ItemStack stack) {
        this.getItemStacks().set(i, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

    }
    boolean isRemoved();
    default boolean stillValid(Player player) {
        return !this.isRemoved() && this.position().closerThan(player.position(), 8.0D);
    }
    default void clearContent() {
        this.getItemStacks().clear();
    }

    default boolean isEmpty() {
        for(ItemStack itemstack : this.getItemStacks()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    default void setChanged() {}
}
