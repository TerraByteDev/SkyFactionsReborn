package net.skullian.skyfactions.common.obelisk;

import lombok.Getter;
import net.skullian.skyfactions.common.config.types.ObeliskConfig;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

@Getter
public class ObeliskItem {

    private final String displayName;
    private final List<String> lore;
    private final int customModelData;

    public ObeliskItem(SkyItemStack itemStack) {
        this.displayName = itemStack.getDisplayName();
        this.lore = itemStack.getLore();
        if (itemStack.getCustomModelData() != -1) {
            this.customModelData = itemStack.getCustomModelData();
        } else {
            this.customModelData = ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt();
        }
    }

    public SkyItemStack getItem(int amount) {
        return SkyItemStack.builder()
                .material("BARRIER")
                .amount(amount)
                .displayName(this.displayName)
                .lore(this.lore)
                .customModelData(this.customModelData)
                .build();
    }


}
