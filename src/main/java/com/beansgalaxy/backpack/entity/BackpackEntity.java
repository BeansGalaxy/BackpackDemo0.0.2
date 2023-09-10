package com.beansgalaxy.backpack.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class BackpackEntity extends HangingEntity {

    public BackpackEntity(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    /**
     *  BOUNDING BOX CALCULATIONS
     */

    public Direction direction = Direction.SOUTH;

    public int getWidth() {
        return 8;
    }
    public int getHeight() {
        return 10;
    }
    public int getDepth() {
        return 6;
    }

    private double offs(int p_31710_) {
        return p_31710_ % 32 == 0 ? 0.5D : 0.0D;
    }
    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null) {
            double x0 = this.pos.getX() + 0.5D;
            double y0 = this.pos.getY();
            double z0 = this.pos.getZ() + 1D;
            double wOff = this.offs(this.getWidth());
            double hOff = this.offs(this.getHeight());
            x0 -= (double) this.direction.getStepX() * 0.46875D;
            z0 -= (double) this.direction.getStepZ() * 0.46875D;
            y0 += hOff;
            Direction direction = this.direction.getCounterClockWise();
            x0 += wOff * (double) direction.getStepX();
            z0 += wOff * (double) direction.getStepZ();
            this.setPosRaw(x0, y0, z0);
            double w0 = this.getWidth();
            double h = this.getHeight();
            double w1 = this.getWidth();
            if (this.direction.getAxis() == Direction.Axis.Z) {
                w1 = this.getDepth();
            } else {
                w0 = this.getDepth();
            }

            w0 /= 32.0D;
            h /= 16.0D;
            w1 /= 32.0D;
            this.setBoundingBox(new AABB(x0 + w0, y0, z0 - w1, x0 - w0, y0 + h, z0 + w1));
        }
    }

    /**
     *  NECESSARY CLASSES AND VARIABLES
     **/

    @Override
    public void dropItem(@Nullable Entity p_31717_) {

    }

    @Override
    public void playPlacementSound() {

    }

    public boolean survives() {
        return true;
    }

}



