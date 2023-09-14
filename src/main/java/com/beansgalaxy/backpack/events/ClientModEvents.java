package com.beansgalaxy.backpack.events;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.client.model.BackpackEntityModel;
import com.beansgalaxy.backpack.client.renderer.BackpackEntityRenderer;
import com.beansgalaxy.backpack.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.BACKPACK_ENTITY.get(), BackpackEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BackpackEntityModel.LAYER_LOCATION, BackpackEntityModel::createBodyLayer);

    }

    /**
     public void registerItemColor(RegisterColorHandlersEvent.Item event) {
     event.register(BackpackLeatherItem::getBackpackColor, ItemInit.LEATHER_BACKPACK.get()); }
    **/

}
