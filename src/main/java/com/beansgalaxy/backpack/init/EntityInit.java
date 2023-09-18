package com.beansgalaxy.backpack.init;

import com.beansgalaxy.backpack.Backpack;
import com.beansgalaxy.backpack.entity.BackpackEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Backpack.MODID);

    public static final RegistryObject<EntityType<BackpackEntity>> BACKPACK = ENTITIES.register("backpack",
            () -> EntityType.Builder.<BackpackEntity>of(BackpackEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(Backpack.MODID, "backpack").toString())
            );
    }

