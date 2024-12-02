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

    private String displayName;
    @Setter private String material;
    @Setter private int amount = 1;
    @Setter private int customModelData = -1;
    private List<PersistentData> persistentData = new ArrayList<>();
    private List<EnchantData> enchants = new ArrayList<>();
    private List<String> itemFlags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    @Setter private String textures;

    @Builder
    private SkyItemStack(String displayName, String material, int amount, int customModelData, List<PersistentData> persistentData, List<EnchantData> enchants, List<String> itemFlags, List<String> lore, String textures) {
        this.material = material;
        this.displayName = displayName;
        this.amount = amount;
        this.customModelData = customModelData;
        this.persistentData.addAll(persistentData);
        this.enchants.addAll(enchants);
        this.itemFlags.addAll(itemFlags);
        this.lore.addAll(lore);
        this.textures = textures;
    }

    public void lore(List<String> lore) {
        this.lore.addAll(lore);
    }

    public void lore(String lore) {
        this.lore.add(lore);
    }

    public void displayName(String displayName) {
        this.displayName = displayName;
    }

    public void displayName(Component displayName) {
        this.displayName = MiniMessage.miniMessage().serialize(displayName);
    }

    public void addPersistentData(String key, String type, Object data) {
        persistentData.add(new PersistentData(key, type, data));
    }

    public void addPersistentData(PersistentData data) {
        persistentData.add(data);
    }

    public void addEnchant(String enchant, int level, boolean ignoreLevelRestriction) {
        enchants.add(new EnchantData(enchant, level, ignoreLevelRestriction));
    }

    public void addEnchant(EnchantData enchant) {
        enchants.add(enchant);
    }

    public void addItemFlag(String flag) {
        itemFlags.add(flag);
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
