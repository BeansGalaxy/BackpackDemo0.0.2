package com.beansgalaxy.backpack;

import com.beansgalaxy.backpack.entity.BackpackEntity;
import com.beansgalaxy.backpack.item.BackpackItem;
import com.beansgalaxy.backpack.screen.BackpackMenu;
import com.beansgalaxy.backpack.screen.BackpackScreen;
import com.beansgalaxy.backpack.item.BackpackType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Backpack.MODID)
public class Backpack {
    public static final String MODID = "backpack";

    public Backpack() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);

        bus.addListener(this::addCreative);
        bus.addListener(this::setupClient);
    }

    // REGISTER ENTITIES
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final RegistryObject<EntityType<BackpackEntity>> ENTITY = ENTITIES.register("backpack",
            () -> EntityType.Builder.<BackpackEntity>of(BackpackEntity::new, MobCategory.MISC).build(new ResourceLocation(Backpack.MODID, "backpack").toString()));

    // REGISTER MENUS
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Backpack.MODID);
    public static final  RegistryObject<MenuType<BackpackMenu>> BACKPACK_MENU = registerMenuType("backpack_menu", BackpackMenu::new);
    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    // REGISTER ITEMS
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack", ()-> new BackpackType(4, "leather"));
    public static final RegistryObject<Item> IRON_BACKPACK = ITEMS.register("iron_backpack", ()-> new BackpackType(9, "iron"));
    public static final RegistryObject<Item> ADVENTURE_BACKPACK = ITEMS.register("adventure_backpack", ()-> new BackpackType(6, "adventure"));
    public static final RegistryObject<Item> NULL_BACKPACK = ITEMS.register("null_backpack", ()-> new BackpackItem(4, ""));

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(LEATHER_BACKPACK);
            event.accept(IRON_BACKPACK);
            event.accept(ADVENTURE_BACKPACK);
            event.accept(NULL_BACKPACK);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(final FMLClientSetupEvent event) {
        MenuScreens.register(BACKPACK_MENU.get(), BackpackScreen::new);
    }

}
