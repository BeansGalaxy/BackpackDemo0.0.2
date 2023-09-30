package com.beansgalaxy.backpack.client.renderer;

import com.beansgalaxy.backpack.client.model.BackpackEntityModel;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.Map;

public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {
    private static final Map<BackpackEntity.Kind, ResourceLocation> resourceLocations = ImmutableMap.of(
            BackpackEntity.Kind.NONE, new ResourceLocation("backpack:textures/entity/backpack_null.png"),
            BackpackEntity.Kind.LEATHER, new ResourceLocation("backpack:textures/entity/backpack_leather.png"),
            BackpackEntity.Kind.ADVENTURE, new ResourceLocation("backpack:textures/entity/backpack_adventure.png"),
            BackpackEntity.Kind.IRON, new ResourceLocation("backpack:textures/entity/backpack_iron.png"));
    private final BackpackEntityModel<BackpackEntity> model;

    public BackpackEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new BackpackEntityModel<>(ctx.bakeLayer(BackpackEntityModel.LAYER_LOCATION));
    }

    public ResourceLocation getTextureLocation(BackpackEntity type) {
        BackpackEntity.Kind backpack$kind = type.getKind();
        return resourceLocations.get(backpack$kind);
    }

    public void render(BackpackEntity type, float pRot, float p_115248_, PoseStack pose, MultiBufferSource p_115250_, int p_115251_) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-pRot));
        this.model.setupAnim(type, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        BackpackEntity.Kind backpack$kind = type.getKind();
        ResourceLocation resourcelocation = resourceLocations.get(backpack$kind);
        VertexConsumer vertexConsumer = p_115250_.getBuffer(this.model.renderType(resourcelocation));
        Color color = new Color(0xFFFFFF);
        if (backpack$kind == BackpackEntity.Kind.LEATHER)
            color = new Color(type.getBackpackColor());
        this.model.renderToBuffer(pose, vertexConsumer, p_115251_, OverlayTexture.NO_OVERLAY, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0F);
        pose.popPose();
        super.render(type, pRot, p_115248_, pose, p_115250_, p_115251_);
    }
}