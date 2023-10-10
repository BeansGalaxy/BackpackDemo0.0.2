package com.beansgalaxy.backpack.events;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.client.model.BackpackEntityModel;
import com.beansgalaxy.backpack.client.renderer.BackpackEntityRenderer;
import com.beansgalaxy.backpack.item.BackpackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Backpack.ENTITY.get(), BackpackEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BackpackEntityModel.LAYER_LOCATION, BackpackEntityModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> (layer != 1 ? ((BackpackType) stack.getItem()).getColor(stack) : 16777215), Backpack.LEATHER_BACKPACK.get());
    }

}
