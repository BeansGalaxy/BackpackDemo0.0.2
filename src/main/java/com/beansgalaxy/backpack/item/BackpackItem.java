package com.beansgalaxy.backpack.item;

import com.beansgalaxy.backpack.entity.BackpackEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class BackpackItem extends Item {
    private String KIND;
    private int MAX_STACKS;
    private int MAX_ITEMS;

    public BackpackItem(int bMaxStacks, String bKIND) {
        super(new Item.Properties().stacksTo(1));
        this.KIND = bKIND;
        this.MAX_STACKS = bMaxStacks;
        this.MAX_ITEMS = bMaxStacks * 64;
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
        entBackpack.initDisplay(KIND, itemstack);
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
}
