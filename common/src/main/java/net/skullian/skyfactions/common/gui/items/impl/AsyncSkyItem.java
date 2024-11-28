package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.ObeliskConfig;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public abstract class AsyncSkyItem {

    private ItemData DATA;
    private SkyItemStack STACK;
    private SkyUser PLAYER;
    private Object[] OPTIONALS;

    public AsyncSkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        this.OPTIONALS = optionals;
        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;

        this.STACK = ObeliskConfig.getLoadingItem(player);
        CompletableFuture.runAsync(() -> {
            String locale = SkyApi.getInstance().getPlayerAPI().getLocale(PLAYER.getUniqueId());
            Object[] replacements = replacements();

            SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                    .displayName(DATA.getNAME())
                    .lore(data.getLORE());

            this.STACK = process(builder).build();
            update();
        });
    }

    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) { return builder; }

    public Object[] replacements() {
        return new Object[0];
    }

    public void onClick(SkyClickType clickType, SkyUser user) {}

    public abstract void update();
}
