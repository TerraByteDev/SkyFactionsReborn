package net.skullian.skyfactions.gui.items;

import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class PaginationBackItem extends PageItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private String MORE_PAGES_LORE;
    private String NO_PAGES_LORE;
    private ItemStack STACK;

    public PaginationBackItem(PaginationItemData data, ItemStack stack) {
        super(false);
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.MORE_PAGES_LORE = data.getMORE_PAGES_LORE();
        this.NO_PAGES_LORE = data.getNO_PAGES_LORE();
        this.STACK = stack;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.legacyColor(NAME, null, null))
                .addLoreLines(gui.hasNextPage()
                        ? TextUtility.legacyColor(MORE_PAGES_LORE.replace("next_page", String.valueOf(gui.getCurrentPage() + 2)).replace("total_pages", String.valueOf(gui.getPageAmount())), null, null)
                        : TextUtility.legacyColor(NO_PAGES_LORE, null, null));

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            getGui().goBack();
        }

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
    }
}
