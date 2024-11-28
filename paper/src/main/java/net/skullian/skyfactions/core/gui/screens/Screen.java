package net.skullian.skyfactions.core.gui.screens;

import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.GUIData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.AirItem;
import net.skullian.skyfactions.core.util.SLogger;
import net.skullian.skyfactions.core.util.SoundUtil;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public abstract class Screen {
    protected final Player player;
    protected final String guiPath;

    protected final GUIData guiData;
    protected Window window;

    public Screen(Player player, String guiPath) {
        this.player = player;
        this.guiPath = guiPath;

        this.guiData = SpigotGUIAPI.getGUIData(guiPath, player);
    }

    protected final void initWindow() {
        window = Window.single()
                .setViewer(player)
                .setTitle(TextUtility.legacyColor(guiData.getTITLE(), SpigotPlayerAPI.getLocale(player.getUniqueId()), player))
                .setGui(registerItems())
                .build();
    }

    public final void show() {
        if (Bukkit.isPrimaryThread()) {
            showUnsafe();
        } else {
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::showUnsafe);
        }
    }

    private void showUnsafe() {
        SoundUtil.playSound(player, guiData.getOPEN_SOUND(), guiData.getOPEN_PITCH(), 1f);
        window.open();
    }

    protected abstract @Nullable Item handleItem(@NotNull ItemData itemData);

    protected void postRegister(Gui.Builder.Normal builder) {
    }

    private Gui registerItems() {
        Gui.Builder.Normal builder = Gui.normal().setStructure(guiData.getLAYOUT());
        List<ItemData> data = SpigotGUIAPI.getItemData(guiPath, player);
        for (ItemData itemData : data) {
            Item item = handleItem(itemData);

            if (item == null) {
                SLogger.warn("Screen inheritor handleItem returned null for {} ({})", itemData.getITEM_ID(), guiPath);
                item = new AirItem(player);
            }

            builder.addIngredient(itemData.getCHARACTER(), item);
        }

        postRegister(builder);
        return builder.build();
    }
}
