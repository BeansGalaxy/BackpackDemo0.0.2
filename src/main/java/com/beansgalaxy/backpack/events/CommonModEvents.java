package com.beansgalaxy.backpack.events;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.beansgalaxy.backpack.init.EntityInit;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) { }
}
