package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

@Getter
@Setter
public abstract class SkyItem extends BaseSkyItem {

    public SkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        super(data, stack, player, optionals, false);
    }

    @Override
    public SkyItemStack getItemStack() {
        Object[] replacements = replacements();

        SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                .serializedBytes(getSTACK().getSerializedBytes())
                .material(getSTACK().getMaterial())
                .displayName(Messages.replace(getDATA().getNAME(), getPLAYER(), replacements))
                .amount(getSTACK().getAmount())
                .customModelData(getSTACK().getCustomModelData())
                .persistentDatas(getSTACK().getPersistentData())
                .enchants(getSTACK().getEnchants())
                .itemFlags(getSTACK().getItemFlags())
                .lore(Messages.replace(getDATA().getLORE(), getPLAYER(), replacements))
                .textures(getSTACK().getTextures())
                .owningPlayerUUID(getSTACK().getOwningPlayerUUID());

        return process(builder).build();
    }
}
