package com.beansgalaxy.backpack;

import com.beansgalaxy.backpack.init.EntityInit;
import com.beansgalaxy.backpack.init.ItemInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Backpack.MODID)
public class Backpack { public static final String MODID = "backpack";

    public Backpack() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITIES.register(bus);
    }
}
