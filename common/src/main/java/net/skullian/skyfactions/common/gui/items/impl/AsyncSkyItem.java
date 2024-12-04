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

    public AsyncSkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        super(data, stack, player, optionals, true);

        setSTACK(ObeliskConfig.getLoadingItem(player));
        CompletableFuture.runAsync(() -> {
            Object[] replacements = replacements();

            SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                    .displayName(Messages.replace(getDATA().getNAME(), getPLAYER(), replacements))
                    .lore(Messages.replace(data.getLORE(),getPLAYER(), replacements));

            setSTACK(process(builder).build());
            update();
        });
    }

    @Override
    public SkyItemStack getItemStack() {
        Object[] replacements = replacements();

        SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                .displayName(Messages.replace(getDATA().getNAME(), getPLAYER(), replacements))
                .lore(Messages.replace(getDATA().getLORE(),getPLAYER(), replacements));

        return process(builder).build();
    }
}
