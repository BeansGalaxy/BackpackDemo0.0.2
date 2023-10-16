package com.beansgalaxy.backpack.item;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class BackpackType extends Item implements Equipable, DyeableItemInterface {
    public BackpackType(int bpStacks, String bpType) {
        super(new Item.Properties().stacksTo(1));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        this.MAX_STACK = bpStacks;
        this.MAX_ITEMS = bpStacks * 64;
        this.type = bpType;
    }
    public String type;
    public int MAX_STACK;
    private int MAX_ITEMS;
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    /** CREATES BACKPACK ENTITY FROM BACKPACK ITEM **/
    // PLACES BACKPACK
    public InteractionResult useOn(UseOnContext ctx) {
        Direction direction = ctx.getClickedFace();
        BlockPos blockpos = ctx.getClickedPos().relative(direction);
        Player player = ctx.getPlayer();
        ItemStack itemstack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        BackpackEntity entBackpack = new BackpackEntity(level, blockpos, direction);
        entBackpack.initDisplay(type, itemstack);
        if (!direction.getAxis().isHorizontal() && player != null)
            entBackpack.setYRot(this.rotFromBlock(blockpos, player) + 90);
        if (!level.isClientSide) {
            entBackpack.playPlacementSound();
            level.gameEvent(player, GameEvent.ENTITY_PLACE, entBackpack.position());
            level.addFreshEntity(entBackpack);
        }
        itemstack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private float rotFromBlock(BlockPos Bpos, Player player) {
        Vec3 Cpos = Bpos.getCenter();
        float YRot = (float) Math.toDegrees(Math.atan2(Cpos.z - player.getZ(), Cpos.x - player.getX()));
        if (YRot < -180 ) YRot += 360;
            else if (YRot > 180) YRot -= 360;
        return YRot;
    }

    /** UNDER THE HOOD CALCULATIONS **/
    // UNSURE  : SOMETHING TO DO WITH ALLOWING THE PLAYER TO PLACE ITEMS INTO BACKPACK
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
        if (pStack.getCount() != 1 || pAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack = pSlot.getItem();
            if (itemstack.isEmpty()) { // IF THE BACKPACK IS EMPTY
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((p_150740_) -> {
                    add(pStack, pSlot.safeInsert(p_150740_));
                });
            } else if (itemstack.getItem().canFitInsideContainerItems()) {
                int i = (MAX_ITEMS - getContentWeight(pStack)) / getWeight(itemstack);
                int j = add(pStack, pSlot.safeTake(itemstack.getCount(), i, pPlayer));
                if (j > 0) {
                    this.playInsertSound(pPlayer);
                }
            }
            return true;
        }
    }
    // UNSURE
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack bStack, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        if (pStack.getCount() != 1) return false;
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer)) {
            if (bStack.isEmpty()) {
                removeOne(pStack).ifPresent((p_186347_) -> {
                    this.playRemoveOneSound(pPlayer);
                    pAccess.set(p_186347_);
                });

            } else {
                int i = add(pStack, bStack);
                if (i > 0) {
                    this.playInsertSound(pPlayer);
                    bStack.shrink(i);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    //THE IMPUTED ITEMS MAX STACK SIZE IS FLIPPED TO GET BUNDLE FULLNESS
    private static int getWeight(ItemStack pStack) {
        if (pStack.is(Backpack.LEATHER_BACKPACK.get())) return 8 + getContentWeight(pStack);
        if (pStack.is(Backpack.ADVENTURE_BACKPACK.get())) return 8 + getContentWeight(pStack);
        if (pStack.is(Backpack.IRON_BACKPACK.get())) return 8 + getContentWeight(pStack);
        if (pStack.is(Items.BUNDLE)) {
            return 4 + getContentWeight(pStack);
        } else {
            if ((pStack.is(Items.BEEHIVE) || pStack.is(Items.BEE_NEST)) && pStack.hasTag()) {
                CompoundTag compoundtag = BlockItem.getBlockEntityData(pStack);
                if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / pStack.getMaxStackSize();
        }
    }
    // ADDS WEIGHT OF ALL ITEMS CURRENTLY INSIDE BACKPACK
    private static int getContentWeight(ItemStack pStack) {
        return getContents(pStack).mapToInt((p_186356_) -> {
            return getWeight(p_186356_) * p_186356_.getCount();
        }).sum();
    }
    // VIEWS ALL ITEMS INSIDE BACKPACK
    private static Stream<ItemStack> getContents(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    /** GUI INTERACTIONS **/
    // DETERMINES IF INSTERTED ITEM MATCHES WITH ONE INSIDE THE BACKPACK
    private static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList) {
        return pStack.is(Items.BUNDLE) ? Optional.empty() : pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast)
                .filter((p_186350_) -> ItemStack.isSameItemSameTags(ItemStack.of(p_186350_), pStack)).findFirst();
    }
    // STORES ITEMS INTO BACKPACK
    private int add(ItemStack pBundleStack, ItemStack pInsertedStack) {
        if (!pInsertedStack.isEmpty() && pInsertedStack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = pBundleStack.getOrCreateTag();
            if (!compoundtag.contains("Items")) {
                compoundtag.put("Items", new ListTag());
            }

            int i = getContentWeight(pBundleStack);  // GETS WEIGHT OF TOTAL ITEMS IN BACKPACK
            int j = getWeight(pInsertedStack);  // GETS WEIGHT OF ONLY THE INSERTED ITEM
            int k = Math.min(pInsertedStack.getCount(), (MAX_ITEMS - i) / j); // RETURNS THE INSERTED STACK OR THE REMAINING SPACE OVER THE WEIGHT OF THE INSERTED ITEM
            if (k == 0) {
                return 0;
            } else {
                // CHECKS IF THE SAME ITEM BEING INSERTED IS ALREADY IN THE BACKPACK AND STACKS THEM ACCORDINGLY
                ListTag listtag = compoundtag.getList("Items", 10);
                Optional<CompoundTag> optional = getMatchingItem(pInsertedStack, listtag);
                if (optional.isPresent() && j > 64) {
                    CompoundTag compoundtag1 = optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    itemstack.grow(k);
                    itemstack.save(compoundtag1);
                    listtag.remove(compoundtag1);
                    listtag.add(0, (Tag)compoundtag1);
                } else {
                    ItemStack itemstack1 = pInsertedStack.copyWithCount(k);
                    CompoundTag compoundtag2 = new CompoundTag();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, (Tag)compoundtag2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }
    // REMOVES ITEMS FROM BACKPACK
    private static Optional<ItemStack> removeOne(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains("Items")) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            if (listtag.isEmpty()) {
                return Optional.empty();
            } else {
                int i = 0;
                CompoundTag compoundtag1 = listtag.getCompound(0);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.remove(0);
                if (listtag.isEmpty()) {
                    pStack.removeTagKey("Items");
                }

                return Optional.of(itemstack);
            }
        }
    }

    /** GUI DISPLAY ELEMENTS **/
    // DRAWS THE BAG'S FULLNESS BAR
    public boolean isBarVisible(ItemStack pStack) {
        return getContentWeight(pStack) > 0;
    }
    public int getBarWidth(ItemStack pStack) {
        return Math.min(1 + 12 * getContentWeight(pStack) / MAX_ITEMS, 13);
    }
    public int getBarColor(ItemStack pStack) {
        return BAR_COLOR;
    }
    //SOMEHOW ADDS THE LITTLE MENU THINGY TO SEE WHAT'S INSIDE THE BUNDLE OR TO GRAB THE ITEMS INSIDE
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(pStack).forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(pStack) - MAX_ITEMS + 64));
    }
    //Adds amount the bundle is filled to the tool tip
    private static int getStackWeightRemainder(ItemStack pStack) {
        return getContentWeight(pStack) % 64;
    }
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(getStackWeightRemainder(pStack) > 0) {
            pTooltipComponents.add(Component.translatable("item.backpack.backpack_remainder",
                    getStackWeightRemainder(pStack)).withStyle(ChatFormatting.GRAY)); }
        pTooltipComponents.add(Component.translatable("item.backpack.backpack_fullness",
                getContentWeight(pStack) / 64, MAX_STACK, "").withStyle(ChatFormatting.GRAY));

    }
    public void onDestroyed(ItemEntity pItemEntity) {
        ItemUtils.onContainerDestroyed(pItemEntity, getContents(pItemEntity.getItem()));
    }

    /** SOUND **/
    float Volume = 0.8F;
    float Pitch = 0.8F;
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }
    private void playRemoveOneSound(Entity pEntity) {
        if(Objects.equals(type, "iron")) {
            Volume = 0.4F;
            Pitch = 0.6F;
            pEntity.playSound(SoundEvents.ARMOR_EQUIP_IRON, Volume, 1F +
                    pEntity.level().getRandom().nextFloat() * 0.4F);
        }
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, Volume, Pitch +
                pEntity.level().getRandom().nextFloat() * 0.4F);
    }
    private void playInsertSound(Entity pEntity) {
        if(Objects.equals(type, "iron")) {
            Volume = 0.4F;
            Pitch = 0.6F;
            pEntity.playSound(SoundEvents.ARMOR_EQUIP_IRON, Volume, 1.6F +
                    pEntity.level().getRandom().nextFloat() * 0.4F);
        }
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, Volume, Pitch +
                pEntity.level().getRandom().nextFloat() * 0.4F);
    }


}

