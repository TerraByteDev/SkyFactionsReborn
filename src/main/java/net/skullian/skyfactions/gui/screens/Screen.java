package net.skullian.skyfactions.gui.screens;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.AirItem;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
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

        this.guiData = GUIAPI.getGUIData(guiPath, player);
    }

    protected final void initWindow() {
        window = Window.single()
                .setViewer(player)
                .setTitle(TextUtility.legacyColor(guiData.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
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
        List<ItemData> data = GUIAPI.getItemData(guiPath, player);
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
