package com.beansgalaxy.backpack.entity;

import com.beansgalaxy.backpack.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

public class BackpackEntity extends HangingEntity {
    private boolean fixed = true;
    public BackpackEntity(EntityType<? extends BackpackEntity> type, Level level) {
        super(type, level);
    }
    public BackpackEntity(Level level, BlockPos blockPos, Direction direction) {
        this(EntityInit.BACKPACK_ENTITY.get(), level, blockPos, direction);
    }
    public BackpackEntity(EntityType<? extends BackpackEntity> type, Level level, BlockPos blkPos, Direction direction) {
        super(type, level, blkPos);
        this.setDirection(direction);
    }

    /** BOUNDING BOX **/
    // WRITES EYE HEIGHT
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) { return 6F / 16; }
    // GETS DIRECTION
    protected void setDirection(Direction dir) {
        Validate.notNull(dir);
        this.direction = dir;
        if (dir.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float)(this.direction.get2DDataValue() * 90));
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }
    // SET THE SIZE OF BOUNDING BOX
    public int getWidth()  { return 8;  }
    public int getHeight() { return 10; }
    public int getDepth()  { return 6;  }
    // LOCKS MODEL TO GRID
    private double offs(int p_31710_) {
        return p_31710_ % 32 == 0 ? 0.5D : 0.0D; }
    // CALCULATES BOUNDING BOX
    protected void recalculateBoundingBox() {
        if (direction != Direction.DOWN && this.direction != null) {
            double x0 = this.pos.getX() + 0.5D;
            double y0 = this.pos.getY();
            double z0 = this.pos.getZ() + 0.5D;
            double w0 = this.getWidth();
            double h = this.getHeight();
            if (this.direction.getAxis().isHorizontal()) {
                double wOff = this.offs(this.getWidth());
                y0 += 0.125D;
                x0 -= (double) this.direction.getStepX() * 0.375D;
                z0 -= (double) this.direction.getStepZ() * 0.375D;
                Direction direction = this.direction.getCounterClockWise();
                x0 += wOff * (double) direction.getStepX();
                z0 += wOff * (double) direction.getStepZ();
                this.setPosRaw(x0, y0, z0);
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
            } else if (this.direction == Direction.UP) {
                this.setPosRaw(x0, y0, z0);
                w0 /= 32.0D;
                h /= 16.0D;
                this.setBoundingBox(new AABB(x0 + w0, y0, z0 - w0, x0 - w0, y0 + h, z0 + w0));
            }
        }

    }

    /** REQUIRED FIELDS **/
    public boolean survives() { return true; }
    public void move(MoverType p_31781_, Vec3 p_31782_) {
        if (!this.fixed) {
            super.move(p_31781_, p_31782_);
        }
    }
    public void push(double p_31817_, double p_31818_, double p_31819_) {
        if (!this.fixed) {
            super.push(p_31817_, p_31818_, p_31819_);
        }
    }
    public SoundEvent getPlaceSound() {
        return SoundEvents.ITEM_FRAME_PLACE;
    }
    public void playPlacementSound() {
        this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
    }
    public boolean shouldRenderAtSqrDistance(double p_31769_) {
        double d0 = 16.0D;
        d0 *= 64.0D * getViewScale();
        return p_31769_ < d0 * d0;
    }
    public void dropItem(@Nullable Entity p_31779_) {
    }
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }
    public void recreateFromPacket(ClientboundAddEntityPacket p_149626_) {
        super.recreateFromPacket(p_149626_);
        this.setDirection(Direction.from3DDataValue(p_149626_.getData()));
    }
}



