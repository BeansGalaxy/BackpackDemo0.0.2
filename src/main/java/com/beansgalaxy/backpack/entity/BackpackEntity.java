package com.beansgalaxy.backpack.entity;

import com.beansgalaxy.backpack.Backpack;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class BackpackEntity extends Entity implements BackpackContainer {
    private static final EntityDataAccessor<String> BACKPACK_KIND = SynchedEntityData.defineId(BackpackEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> BACKPACK_COLOR = SynchedEntityData.defineId(BackpackEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> BACKPACK_TRIM = SynchedEntityData.defineId(BackpackEntity.class, EntityDataSerializers.COMPOUND_TAG);
    protected static int DEFAULT_BACKPACK_COLOR = 9062433;
    protected BlockPos pos;
    protected double YPosRaw;
    protected Direction direction;
    protected CompoundTag backpackTrim;

    public BackpackEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    /** BACKPACK CREATION **/
    // CREATE BACKPACK ENTITY FROM BACKPACK ITEM
    public BackpackEntity(Level level, BlockPos pos, Direction direction, float YRot, String kind, ItemStack item) {
        this(Backpack.ENTITY.get(), level);
        this.setBackpackKind(kind);
        if (item.getTagElement("display") != null)
            this.setBackpackColor(item.getTagElement("display").getInt("color"));
        else if (item.getTagElement("Trim") != null)
            this.setBackpackTrim(item.getTagElement("Trim"));
        this.YPosRaw = pos.getY() + 2D / 16;
        this.pos = pos;
        this.setDirection(direction);
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
        double H = 9D / 16;
        double Wx = 8D / 32;
        double Wz = 8D / 32;
         if (direction != null) {
            if (direction.getAxis().isHorizontal()) {
                double D = 4D / 32;
                double off = 6D / 16;
                int stepX = this.direction.getStepX();
                int stepZ = this.direction.getStepZ();
                Wx -= D * Math.abs(stepX);
                Wz -= D * Math.abs(stepZ);
                x -= off * stepX;
                z -= off * stepZ;
            } else {
                Wx -= 1D / 16;
                Wz -= 1D / 16;
            }
        }
        this.setPosRaw(x, y, z);
        this.setBoundingBox(new AABB(x - Wx, y, z - Wz, x + Wx, y + H, z + Wz));
    }

    /** IMPLEMENTS GRAVITY WHEN HUNG BACKPACKS LOOSE SUPPORTING BLOCK **/
    public void tick() {
        this.setNoGravity(this.isNoGravity() && !this.level().noCollision(this, this.getBoundingBox().inflate(0.1, -0.1, 0.1)));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
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
    public void addAdditionalSaveData(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.getItemStacks());
        tag.putByte("Facing", (byte)this.direction.get3DDataValue());
        tag.putString("Kind", getBackpackKind());
        tag.putInt("Color", getBackpackColor());
        tag.put("Trim", getBackpackTrim());
    }
    public void readAdditionalSaveData(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, this.getItemStacks());
        this.setDirection(Direction.from3DDataValue(tag.getByte("Facing")));
        this.setBackpackKind(tag.getString("Kind"));
        this.setBackpackColor(tag.getInt("Color"));
        this.setBackpackTrim(tag.getCompound("Trim"));
    }
    // LOCAL
    public void setBackpackKind(String kind) {
        this.entityData.set(BACKPACK_KIND, kind);
    }
    public String getBackpackKind() {
        return this.entityData.get(BACKPACK_KIND);
    }
    public void setBackpackColor(int color) {
        this.entityData.set(BACKPACK_COLOR, color);
    }
    public int getBackpackColor() {
        return this.entityData.get(BACKPACK_COLOR);
    }
    public CompoundTag getBackpackTrim() {
        return this.entityData.get(BACKPACK_TRIM);
    }
    public void setBackpackTrim(CompoundTag trim) {
        this.entityData.set(BACKPACK_TRIM, trim);
    }
    protected void defineSynchedData() {
        this.entityData.define(BACKPACK_KIND, "");
        this.entityData.define(BACKPACK_COLOR, DEFAULT_BACKPACK_COLOR);
        CompoundTag CompoundTag = new CompoundTag();
        this.entityData.define(BACKPACK_TRIM, CompoundTag);
    }

    /** FOR BACKPACK RENDERER **/
    // TELLS RENDERER THE CURRENT BACKPACK'S KIND
    public Kind getKind() {
        int k = 0;
        switch (this.getBackpackKind()) {
            case "leather" -> k = 1;
            case "adventure" -> k = 2;
            case "iron" -> k = 3;
            default -> k = 0;
        }

        return Kind.byInt(k);
    }

    // PACKAGES UP THE BACKPACK'S KIND FOR THE RENDERER
    public static enum Kind {
        IRON(3),
        ADVENTURE(2),
        LEATHER(1),
        NONE(0);

        private static final List<BackpackEntity.Kind> BY_INT = Stream.of(values()).sorted(Comparator.comparingInt((kind) -> {
            return kind.kindString;
        })).collect(ImmutableList.toImmutableList());
        private final int kindString;

        private Kind(int p_28900_) {
            this.kindString = p_28900_;
        }

        public static Kind byInt(int type) {
            for(Kind BackpackEntity$kind : BY_INT) {
                if (type == BackpackEntity$kind.kindString) {
                    return BackpackEntity$kind;
                }
            }
            return NONE;
        }
    }

    /** COLLISIONS AND INTERACTIONS **/
    public boolean canCollideWith(Entity that) {
        return (that.canBeCollidedWith() || that.isPushable()) && !this.isPassengerOfSameVehicle(that);
    }
    public boolean canBeCollidedWith() {
        return true;
    }
    public boolean hurt(DamageSource p_31715_, float p_31716_) {
        if (this.isInvulnerableTo(p_31715_)) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                this.kill();
                this.markHurt();
                this.dropItem(p_31715_.getEntity());
            }
            return true;
        }
    }
    public void dropItem(@Nullable Entity entity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(this.getBreakSound(), 1.0F, 1.0F);
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (player.getAbilities().instabuild) return;
                this.spawnAtLocation(getBackpackTypeStack(), entity);
            } else this.spawnAtLocation(Backpack.LEATHER_BACKPACK.get());
    }   }
    @Nullable
    public ItemEntity spawnAtLocation(ItemStack stack, Entity entity) {
        if (stack.isEmpty()) {
            return null;
        } else if (this.level().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
            itementity.setPickUpDelay(0);
            if (getBackpackColor() != DEFAULT_BACKPACK_COLOR) itementity.getItem().getOrCreateTagElement("display").putInt("color", getBackpackColor());
            if (captureDrops() != null) captureDrops().add(itementity);
            else
                this.level().addFreshEntity(itementity);
            return itementity;
        }
    }
    // GIVES THE CORRECT BACKPACK TYPE WHEN BROKEN
    protected ItemStack getBackpackTypeStack() {
        switch (this.getBackpackKind()) {
            case "leather" -> {
                return Backpack.LEATHER_BACKPACK.get().getDefaultInstance();
            }
            case "iron" -> {
                return Backpack.IRON_BACKPACK.get().getDefaultInstance();
            }
            default -> {
                return Backpack.ADVENTURE_BACKPACK.get().getDefaultInstance();
            }
        }
    }


    /** REQUIRED FEILDS **/
    public SoundEvent getPlaceSound() {
        return SoundEvents.ITEM_FRAME_PLACE;
    }
    public void playPlacementSound() {
        this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
    }
    public SoundEvent getBreakSound() {
        return SoundEvents.ITEM_FRAME_BREAK;
    }
    protected boolean repositionEntityAfterLoad() { return false; }
    public boolean isPickable() {
        return true;
    }


    /** INVENTORY SCREEN **/

    // PREFORMS THIS ACTION WHEN IT IS RIGHT-CLICKED
    public InteractionResult interact(Player player, InteractionHand p_270576_) {
        InteractionResult interactionresult = this.interactWithContainerVehicle(player);
        if (interactionresult.consumesAction()) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinAi.angerNearbyPiglins(player, true);
        }
        return interactionresult;
    }

    // COMMUNICATES WITH "BackpackContainer"
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }



    /** EVERYTHING BELOW IS FOR TESTING **/

}
