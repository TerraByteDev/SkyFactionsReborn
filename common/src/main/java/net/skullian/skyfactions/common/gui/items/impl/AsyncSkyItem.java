package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.ObeliskConfig;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public abstract class AsyncSkyItem extends BaseSkyItem {

    private boolean isLoading;
    private SkyItemStack loadingStack;

    public AsyncSkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        super(data, stack, player, optionals, true);

        this.loadingStack = ObeliskConfig.getLoadingItem(player);
    }

    @Override
    public SkyItemStack getItemStack() {
        if (isLoading) return loadingStack;

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
