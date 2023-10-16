package com.beansgalaxy.backpack.screen;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation BACKPACK_GUI = new ResourceLocation(Backpack.MODID, "textures/gui/container/backpack.png");
    private final BackpackMenu menu;

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component p_97743_) {
        super(menu, inventory, p_97743_);
        this.menu = menu;
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 217 + this.menu.invOffset;
    }

    protected void init() {
        super.init();
        this.titleLabelY = 10000;
    }

    public void render(GuiGraphics p_282918_, int p_282102_, int p_282423_, float p_282621_) {
        this.renderBackground(p_282918_);
        super.render(p_282918_, p_282102_, p_282423_, p_282621_);
        this.renderTooltip(p_282918_, p_282102_, p_282423_);

    }

    protected void renderBg(GuiGraphics gGraphics, float p_282737_, int p_281678_, int p_281465_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        j += this.menu.invOffset;
        gGraphics.blit(BACKPACK_GUI, i, j - 123, 0, 0, this.imageWidth, this.imageHeight);

        renderEntityInInventory(gGraphics, this.width / 2, j + 24, 168, this.menu.backpack);

    }

    public void renderEntityInInventory(GuiGraphics guiG, int p_283622_, int p_283401_, int scale, BackpackEntity entity) {
        guiG.pose().pushPose();
        guiG.pose().translate(p_283622_ + 3, p_283401_, 40);
        guiG.pose().mulPose(Axis.XP.rotationDegrees(-10));
        guiG.pose().mulPose(Axis.ZP.rotationDegrees(-1));
        guiG.pose().scale(scale, -scale, scale);
        EntityRenderDispatcher entityrenderdispatcher = this.minecraft.getEntityRenderDispatcher();
        Lighting.setupLevel(new Matrix4f().translate(-0.1F, -0.8F, -0.2F));
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 20, 1.0F,
                    guiG.pose(), guiG.bufferSource(), 15728880);
        });
        guiG.flush();
        guiG.pose().popPose();
        Lighting.setupFor3DItems();

    }
}
