package net.skullian.skyfactions.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SkyItemStack implements Cloneable {

    private Object serializedBytes;
    private String displayName;
    @Setter private String material;
    @Setter private int amount = 1;
    @Setter private int customModelData = -1;
    private final List<PersistentData> persistentData = new ArrayList<>();
    private final List<EnchantData> enchants = new ArrayList<>();
    private final List<String> itemFlags = new ArrayList<>();
    private final List<String> lore = new ArrayList<>();
    @Setter private String textures;
    @Setter private String owningPlayerUUID = "none";

    @Builder
    private SkyItemStack(Object serializedBytes, String displayName, String material, int amount, int customModelData, List<PersistentData> persistentDatas, PersistentData persistentData, List<EnchantData> enchants, EnchantData enchant, List<String> itemFlags, String itemFlag, List<String> lore, String loreLine, String textures, String owningPlayerUUID) {
        if (serializedBytes != null) this.serializedBytes = serializedBytes;
        if (material != null) this.material = material;
        if (displayName != null) this.displayName = displayName;
        if (amount > 0) this.amount = amount;
        if (customModelData >= 0) this.customModelData = customModelData;
        if (persistentData != null) this.persistentData.add(persistentData);
        if (persistentDatas != null) this.persistentData.addAll(persistentDatas);
        if (enchant != null) this.enchants.add(enchant);
        if (enchants != null) this.enchants.addAll(enchants);
        if (itemFlag != null) this.itemFlags.add(itemFlag);
        if (itemFlags != null) this.itemFlags.addAll(itemFlags);
        if (loreLine != null) this.lore.add(loreLine);
        if (lore != null) this.lore.addAll(lore);
        if (textures != null) this.textures = textures;
        if (owningPlayerUUID != null) this.owningPlayerUUID = owningPlayerUUID;
    }

    public SkyItemStack lore(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public SkyItemStack lore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public SkyItemStack displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SkyItemStack displayName(Component displayName) {
        this.displayName = MiniMessage.miniMessage().serialize(displayName);
        return this;
    }

    public SkyItemStack addPersistentData(String key, String type, Object data) {
        persistentData.add(new PersistentData(key, type, data));
        return this;
    }

    public SkyItemStack addPersistentData(PersistentData data) {
        persistentData.add(data);
        return this;
    }

    public SkyItemStack addEnchant(String enchant, int level, boolean ignoreLevelRestriction) {
        enchants.add(new EnchantData(enchant, level, ignoreLevelRestriction));
        return this;
    }

    public SkyItemStack addEnchant(EnchantData enchant) {
        enchants.add(enchant);
        return this;
    }

    public SkyItemStack addItemFlag(String flag) {
        itemFlags.add(flag);
        return this;
    }

    public boolean hasPersistentData(String key) {
        return persistentData.stream().anyMatch(persistentData -> persistentData.getKey().equals(key));
    }

    public PersistentData getPersistentData(String key) {
        return persistentData.stream().filter(persistentData -> persistentData.getKey().equals(key)).findFirst().orElse(null);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class EnchantData {
        private String enchant;
        private int level;
        private boolean ignoreLevelRestriction;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class PersistentData {
        private String key;
        private String type;
        private Object data;
    }

    @Override
    public SkyItemStack clone() {
        try {
            return (SkyItemStack) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


}
