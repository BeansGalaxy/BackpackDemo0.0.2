package com.beansgalaxy.backpack.init;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.item.BackpackIronItem;
import com.beansgalaxy.backpack.item.BackpackLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Backpack.MODID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack",
            ()-> new BackpackLeatherItem(4, "leather"));
    public static final RegistryObject<Item> IRON_BACKPACK = ITEMS.register("iron_backpack",
            ()-> new BackpackIronItem(9, "iron"));
}