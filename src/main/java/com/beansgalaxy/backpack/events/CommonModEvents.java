package com.beansgalaxy.backpack.events;

import com.beansgalaxy.backpack.Backpack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        BlockTagsProvider blockTagGenerator = generator.addProvider(event.includeServer(),
                new BlockTagsProvider(packOutput, lookupProvider, Backpack.MODID, existingFileHelper) {
                    protected void addTags(HolderLookup.Provider p_256380_) { }});
        generator.addProvider(event.includeServer(),
                new ItemTagsProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), Backpack.MODID, existingFileHelper) {
                    protected void addTags(HolderLookup.Provider p_256380_) {
                        this.tag(ItemTags.TRIMMABLE_ARMOR).add(Backpack.IRON_BACKPACK.get());
                    }});
    }
}
