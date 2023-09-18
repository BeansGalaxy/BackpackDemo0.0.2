package com.beansgalaxy.backpack.init;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.item.BackpackItemIron;
import com.beansgalaxy.backpack.item.BackpackItemLeather;
import com.beansgalaxy.backpack.item.BackpackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Backpack.MODID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack",
            ()-> new BackpackItemLeather(4, "leather"));
    public static final RegistryObject<Item> IRON_BACKPACK = ITEMS.register("iron_backpack",
            ()-> new BackpackItemIron(9, "iron"));
    public static final RegistryObject<Item> ADVENTURE_BACKPACK = ITEMS.register("adventure_backpack",
            ()-> new BackpackType(6, "leather"));
}