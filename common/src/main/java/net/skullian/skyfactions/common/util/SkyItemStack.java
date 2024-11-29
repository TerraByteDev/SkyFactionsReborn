package net.skullian.skyfactions.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class SkyItemStack {

    private String displayName;
    private final String material;
    private int amount = 1;
    private int customModelData = -1;
    private final List<PersistentData> persistentData = new ArrayList<>();
    private final List<EnchantData> enchants = new ArrayList<>();
    private final List<String> itemFlags = new ArrayList<>();
    private final List<String> lore = new ArrayList<>();
    private final String textures;

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


}
