package com.beansgalaxy.backpack.entity;

import com.beansgalaxy.backpack.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class BackpackEntity extends Entity {
    protected BlockPos pos;
    protected double YPosRaw;
    protected Direction direction;
    public BackpackEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    /** BACKPACK CREATION **/
    // CREATE BACKPACK ENTITY FROM BACKPACK ITEM
    public BackpackEntity(Level level, BlockPos pos, Direction direction, float YRot) {
        this(EntityInit.BACKPACK.get(), level);
        this.YPosRaw = pos.getY() + 2D / 16;
        this.pos = pos;
        this.setDirection(direction);
        if (!direction.getAxis().isHorizontal()) {
            this.setYRot(YRot + 90);
        }
    }
    protected float getEyeHeight(Pose p_31784_, EntityDimensions p_31785_) {
        return 6F / 16;
    }
    /** BOUNDING BOX CALCULATIONS **/
    // MOVES INFO FOR THE BACKPACK
    public Direction getDirection() {
        return this.direction;
    }
    protected void setDirection(Direction direction) {
        if (direction != null) {
            this.direction = direction;
            if (direction.getAxis().isHorizontal()) {
                this.setNoGravity(true);
                this.setYRot((float) direction.get2DDataValue() * 90); }
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.recalculateBoundingBox();
        }
    }
    public void setPos(double x, double y, double z) {
        this.YPosRaw = y;
        this.pos = BlockPos.containing(x, y, z);
        this.recalculateBoundingBox();
        this.hasImpulse = true;
    }
    // BUILDS NEW BOUNDING BOX
    protected void recalculateBoundingBox() {
        double x = this.pos.getX() + 0.5D;
        double y = this.YPosRaw;
        double z = this.pos.getZ() + 0.5D;
        double H = 10D / 16;
        double Wx = 8D / 32;
        double Wz = 8D / 32;
         if (direction != null) {
            if (direction.getAxis().isHorizontal()) {
                double D = 4D / 32;
                double off = 6D / 16;
                int stepX = this.direction.getStepX();
                int stepZ = this.direction.getStepZ();
                H -= 1D / 16;
                Wx -= D * Math.abs(stepX);
                Wz -= D * Math.abs(stepZ);
                x -= off * stepX;
                z -= off * stepZ;
            }
        }
        this.setPosRaw(x, y, z);
        this.setBoundingBox(new AABB(x - Wx, y, z - Wz, x + Wx, y + H, z + Wz));
    }
    /** IMPLEMENTS GRAVITY TO BACKPACK **/
    // INCREASES MOMENTUM
    public void tick() {
        setNoGravity(!notHung());
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
    }
    // ENABLES GRAVITY IF A HUNG BACKPACK LOOSES THE BLOCK HUNG FROM
    public boolean notHung() {
        return !isNoGravity() || this.level().noCollision(getBoundingBox().inflate(0.1D));
    }
    /** DATA MANAGEMENT **/
    // CLIENT
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue());
    }
    public void recreateFromPacket(ClientboundAddEntityPacket p_149626_) {
        super.recreateFromPacket(p_149626_);
        this.setDirection(Direction.from3DDataValue(p_149626_.getData()));
    }
    // NBT
    public void addAdditionalSaveData(CompoundTag Comp) {
        Comp.putByte("Facing", (byte)this.direction.get3DDataValue());
    }
    public void readAdditionalSaveData(CompoundTag Comp) {
        this.setDirection(Direction.from3DDataValue(Comp.getByte("Facing")));
    }

    /** REQUIRED FEILDS **/
    public SoundEvent getPlaceSound() {
        return SoundEvents.ITEM_FRAME_PLACE;
    }
    public void playPlacementSound() {
        this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
    }
    protected boolean repositionEntityAfterLoad() { return false; }
    protected void defineSynchedData() {

    }
}
