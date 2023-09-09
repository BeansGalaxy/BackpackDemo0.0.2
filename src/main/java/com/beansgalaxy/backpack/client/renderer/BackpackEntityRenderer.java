package com.beansgalaxy.backpack.client.renderer;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.client.model.BackpackEntityModel;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Backpack.MODID, "textures/entity/leather_backpack");

    public BackpackEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(BackpackEntity entity) {
        return TEXTURE;
    }

}
**/
public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Backpack.MODID, "textures/entity/leather_backpack.png");
    private final BackpackEntityModel<BackpackEntity> model;

    public BackpackEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new BackpackEntityModel<>(ctx.bakeLayer(BackpackEntityModel.LAYER_LOCATION));
    }

    public void render(BackpackEntity p_115246_, float p_115247_, float p_115248_, PoseStack pose, MultiBufferSource p_115250_, int p_115251_) {
        pose.pushPose();
        pose.scale(1.0F, -1.0F, -1.0F);
        pose.translate(0.0F, -23.0F / 16, 2.0F / 16);
        this.model.setupAnim(p_115246_, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = p_115250_.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(pose, vertexconsumer, p_115251_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
        super.render(p_115246_, p_115247_, p_115248_, pose, p_115250_, p_115251_);
    }

    public ResourceLocation getTextureLocation(BackpackEntity p_115244_) {
        return TEXTURE;
    }
}