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
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.awt.*;
import java.util.Map;

public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {
    private static final Map<BackpackEntity.Kind, ResourceLocation> resourceLocations = ImmutableMap.of(
            BackpackEntity.Kind.NONE, new ResourceLocation("backpack:textures/entity/backpack/null.png"),
            BackpackEntity.Kind.LEATHER, new ResourceLocation("backpack:textures/entity/backpack/leather.png"),
            BackpackEntity.Kind.ADVENTURE, new ResourceLocation("backpack:textures/entity/backpack/adventure.png"),
            BackpackEntity.Kind.IRON, new ResourceLocation("backpack:textures/entity/backpack/iron.png"));
    private static final ResourceLocation OVERLAY_GOLD = new ResourceLocation(Backpack.MODID, "textures/entity/backpack/overlay_gold.png");
    private static final ResourceLocation OVERLAY_DIAMOND = new ResourceLocation(Backpack.MODID, "textures/entity/backpack/overlay_diamond.png");
    private static final ResourceLocation OVERLAY_AMETHYST = new ResourceLocation(Backpack.MODID, "textures/entity/backpack/overlay_amethyst.png");

    private final BackpackEntityModel<BackpackEntity> model;
    private final TextureAtlas armorTrimAtlas;

    public BackpackEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new BackpackEntityModel<>(ctx.bakeLayer(BackpackEntityModel.LAYER_LOCATION));
        this.armorTrimAtlas = ctx.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);

    }

    public ResourceLocation getTextureLocation(BackpackEntity type) {
        BackpackEntity.Kind backpack$kind = type.getKind();
        return resourceLocations.get(backpack$kind);
    }

    public void render(BackpackEntity type, float pRot, float p_115248_, PoseStack pose, MultiBufferSource mbs, int i) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-pRot));
        this.model.setupAnim(type, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        BackpackEntity.Kind backpack$kind = type.getKind();
        ResourceLocation resourcelocation = resourceLocations.get(backpack$kind);
        VertexConsumer vertexConsumer = mbs.getBuffer(this.model.renderType(resourcelocation));
        Color color = new Color(0xFFFFFF);
        ResourceLocation overlayLocation = OVERLAY_DIAMOND;
        if (backpack$kind == BackpackEntity.Kind.LEATHER) {
            color = new Color(type.getBackpackColor());
            if (!isYellow(color)) overlayLocation = OVERLAY_GOLD;
            else overlayLocation = OVERLAY_AMETHYST;
        }
        this.model.renderToBuffer(pose, vertexConsumer, i, OverlayTexture.NO_OVERLAY, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0F);
        VertexConsumer consumerLayer = mbs.getBuffer(RenderType.entityTranslucent(overlayLocation));
        this.model.renderToBuffer(pose, consumerLayer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (backpack$kind == BackpackEntity.Kind.IRON && !type.getBackpackTrim().getString("material").isEmpty())
            BackpackTrim.getBackpackTrim(type.level().registryAccess(), type.getBackpackTrim()).ifPresent((p_289638_) -> {
                this.renderTrim(ArmorMaterials.IRON, pose, mbs, i, p_289638_);
            });
        pose.popPose();
        super.render(type, pRot, p_115248_, pose, mbs, i);
    }

    private void renderTrim(ArmorMaterial armorMaterial, PoseStack pose, MultiBufferSource mbs, int i, BackpackTrim armorTrim) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(armorTrim.backpackTexture(armorMaterial));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(mbs.getBuffer(Sheets.armorTrimsSheet()));
        this.model.renderToBuffer(pose, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isYellow(Color color) {
        int red = color.getRed();
        int blue = color.getBlue();
        int green = color.getGreen();

        if (red + green + blue > 650) return false;
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

        return 35 < Math.round(hue) && 60 > Math.round(hue);
    }
}