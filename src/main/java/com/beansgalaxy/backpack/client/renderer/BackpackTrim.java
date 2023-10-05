package com.beansgalaxy.backpack.client.renderer;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BackpackTrim extends ArmorTrim {
    public static final Codec<BackpackTrim> CODEC = RecordCodecBuilder.create((p_267058_) -> {
        return p_267058_.group(TrimMaterial.CODEC.fieldOf("material").forGetter(BackpackTrim::material),
                TrimPattern.CODEC.fieldOf("pattern").forGetter(BackpackTrim::pattern)).apply(p_267058_, BackpackTrim::new);
    });
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Function<ArmorMaterial, ResourceLocation> backpackTexture;
    private final Holder<TrimMaterial> material;



    public BackpackTrim(Holder<TrimMaterial> p_267249_, Holder<TrimPattern> p_267212_) {
        super(p_267249_, p_267212_);
        this.material = p_267249_;
        this.backpackTexture = Util.memoize((p_267932_) -> {
            ResourceLocation resourcelocation = p_267212_.value().assetId();
            String s = this.getColorPaletteSuffix(p_267932_);
            return resourcelocation.withPath((p_266864_) -> {
                return "trims/models/backpack/" + p_266864_ + "_" + s;
            });
        });
    }

    private String getColorPaletteSuffix(ArmorMaterial p_268122_) {
        Map<ArmorMaterials, String> map = this.material.value().overrideArmorMaterials();
        return p_268122_ instanceof ArmorMaterials && map.containsKey(p_268122_) ? map.get(p_268122_) : this.material.value().assetName();
    }

    public static Optional<BackpackTrim> getBackpackTrim(RegistryAccess p_266952_, CompoundTag compoundtag) {
        if (compoundtag != null) {
            BackpackTrim armortrim = CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, p_266952_), compoundtag).resultOrPartial(LOGGER::error).orElse((BackpackTrim)null);
            return Optional.ofNullable(armortrim);
        } else {
            return Optional.empty();
        }
    }

    public ResourceLocation backpackTexture(ArmorMaterial p_268143_) {
        return this.backpackTexture.apply(p_268143_);
    }
}
