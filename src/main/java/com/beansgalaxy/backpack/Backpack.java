package com.beansgalaxy.backpack;

import com.beansgalaxy.backpack.init.EntityInit;
import com.beansgalaxy.backpack.init.ItemInit;
import com.beansgalaxy.backpack.item.BackpackLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Backpack.MODID)
public class Backpack { public static final String MODID = "backpack";

    public Backpack() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITIES.register(bus);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientRegisterHandler {
        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) -> (layer != 1 ? ((BackpackLeatherItem) stack.getItem()).getColor(stack) : 16777215), ItemInit.LEATHER_BACKPACK.get());
        }
    }

}
