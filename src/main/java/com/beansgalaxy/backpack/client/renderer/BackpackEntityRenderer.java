package com.beansgalaxy.backpack.client.renderer;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.client.model.BackpackEntityModel;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;

import java.awt.*;
import java.util.Map;

public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {
    private static final Map<BackpackEntity.Kind, ResourceLocation> resourceLocations = ImmutableMap.of(
            BackpackEntity.Kind.NONE, new ResourceLocation(Backpack.MODID, "textures/entity/backpack/null.png"),
            BackpackEntity.Kind.LEATHER, new ResourceLocation(Backpack.MODID, "textures/entity/backpack/leather.png"),
            BackpackEntity.Kind.ADVENTURE, new ResourceLocation(Backpack.MODID, "textures/entity/backpack/adventure.png"),
            BackpackEntity.Kind.IRON, new ResourceLocation(Backpack.MODID, "textures/entity/backpack/iron.png"));
    private static final ResourceLocation OVERLAY_AMETHYST = new ResourceLocation(Backpack.MODID, "textures/entity/backpack/overlay_amethyst.png");
    private static final ResourceLocation OVERLAY_LEATHER = new ResourceLocation(Backpack.MODID, "textures/entity/backpack/overlay_leather.png");

    private final BackpackEntityModel<BackpackEntity> model;
    private final TextureAtlas armorTrimAtlas;

    public BackpackEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new BackpackEntityModel<>(ctx.bakeLayer(BackpackEntityModel.LAYER_LOCATION));
        this.armorTrimAtlas = ctx.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);

    }

    public ResourceLocation getTextureLocation(BackpackEntity type) {
        BackpackEntity.Kind backpack$kind = type.findKind();
        return resourceLocations.get(backpack$kind);
    }

    public void render(BackpackEntity type, float pRot, float p_115248_, PoseStack pose, MultiBufferSource mbs, int i) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-pRot));
        this.model.setupAnim(type, 0.0F, 0.0F, 0.0F, 50F, 0.0F);
        BackpackEntity.Kind backpack$kind = type.findKind();
        ResourceLocation resourcelocation = resourceLocations.get(backpack$kind);
        VertexConsumer vertexConsumer = mbs.getBuffer(this.model.renderType(resourcelocation));
        Color color = new Color(0xFFFFFF);
        if (backpack$kind == BackpackEntity.Kind.LEATHER)
            color = new Color(type.getColor());
        if (type.isMenu) this.model.isOpenBackpack(this.model.head, true);
        else this.model.isOpenBackpack(this.model.head, false);
        this.model.renderToBuffer(pose, vertexConsumer, i, OverlayTexture.NO_OVERLAY, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0F);
        if (backpack$kind == BackpackEntity.Kind.IRON && type.getTrim() != null)
            BackpackTrim.getBackpackTrim(type.level().registryAccess(), type.getTrim()).ifPresent((p_289638_) -> {
                this.renderTrim(pose, mbs, i, p_289638_, type.isMenu);
            });
        else if (backpack$kind == BackpackEntity.Kind.LEATHER)this.renderOverlay(pose, i, mbs, color, backpack$kind);
        pose.popPose();
        super.render(type, pRot, p_115248_, pose, mbs, i);
    }

    private void renderOverlay(PoseStack pose, int i, MultiBufferSource mbs, Color color, BackpackEntity.Kind backpack$kind) {
        VertexConsumer overlayLayer = mbs.getBuffer(RenderType.entityTranslucent(OVERLAY_LEATHER));
        this.model.renderToBuffer(pose, overlayLayer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (isYellow(color)) {
            VertexConsumer consumerLayer = mbs.getBuffer(RenderType.entityCutout(OVERLAY_AMETHYST));
            this.model.renderToBuffer(pose, consumerLayer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void renderTrim(PoseStack pose, MultiBufferSource mbs, int i, BackpackTrim backpackTrim, Boolean isMenu) {
        if (isMenu) { // LAZY FIX FOR Z-FIGHTING WHEN BACKPACK IS RENDERED IN MENU VIEW
            pose.scale(1.001F, 1.003F, 1.005F);
            pose.translate(0.001, -0.0017, 0.002);
        }
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(backpackTrim.backpackTexture(ArmorMaterials.IRON));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(mbs.getBuffer(Sheets.armorTrimsSheet()));
        this.model.renderToBuffer(pose, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }


    public boolean isYellow(Color color) {
        int red = color.getRed();
        int blue = color.getBlue();
        int green = color.getGreen();

        // BRIGHTNESS
        if (red + green + blue > 600) return false;
        //DARKNESS
        if (red + green <333) return false;

        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        if (min == max) return false;

        float hue;

        if (max == red)
            hue = (green - blue) / (max - min);
        else if (max == green)
            hue = 2f + (blue - red) / (max - min);
        else
            hue = 4f + (red - green) / (max - min);

        hue = hue * 60;
        if (hue < 0) hue = hue + 360;

        // LOWER TOWARDS RED, HIGHER TOWARDS GREEN
        return 40 < Math.round(hue) && 60 > Math.round(hue);
    }
}