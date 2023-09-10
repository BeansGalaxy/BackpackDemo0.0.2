package com.beansgalaxy.backpack.init;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.item.BackpackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Backpack.MODID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("leather_backpack",
            ()-> new BackpackType(6, "leather"));
    public static final RegistryObject<Item> IRON_BACKPACK = ITEMS.register("iron_backpack",
            ()-> new BackpackType(9, "iron"));
}