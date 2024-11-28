package net.skullian.skyfactions.core.gui.screens;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public abstract class SpigotScreen extends Screen {
    protected final SkyUser player;
    protected Window window;
    private final Gui.Builder.Normal builder;

    public SpigotScreen(String guiPath, SkyUser player) {
        super(guiPath, player);
        this.player = player;

        this.builder = Gui.normal().setStructure(guiData.getLAYOUT());
    }

    @Override
    protected final void init() {
        registerItems();
        Gui gui = builder.build();

        window = Window.single()
                .setViewer(SpigotAdapter.adapt(player).getPlayer())
                .setTitle(TextUtility.legacyColor(guiData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), player))
                .setGui(gui)
                .build();
    }

    @Override
    public final void show() {
        if (Bukkit.isPrimaryThread()) {
            showUnsafe();
        } else {
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::showUnsafe);
        }
    }

    private void showUnsafe() {
        SkyApi.getInstance().getSoundAPI().playSound(player, guiData.getOPEN_SOUND(), guiData.getOPEN_PITCH(), 1f);
        window.open();
    }

    @Override
    protected abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);

    protected void postRegister(Gui.Builder.Normal builder) {
    }

    @Override
    protected void registerItems() {
        List<ItemData> data = GUIAPI.getItemData(guiPath, player);
        for (ItemData itemData : data) {
            BaseSkyItem item = handleItem(itemData);
            builder.addIngredient(itemData.getCHARACTER(), SpigotAdapter.adapt(item.getSTACK(), player, true));
        }

        postRegister(builder);
    }
}
