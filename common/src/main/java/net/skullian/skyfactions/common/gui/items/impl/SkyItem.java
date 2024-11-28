package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

@Getter
@Setter
public abstract class SkyItem extends BaseSkyItem{

    public SkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        super(data, stack, player, optionals);

        Object[] replacements = replacements();

        SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                .displayName(Messages.replace(getDATA().getNAME(), getPLAYER(), replacements))
                .lore(Messages.replace(data.getLORE(),getPLAYER(), replacements));

        setSTACK(process(builder).build());
    }
}
