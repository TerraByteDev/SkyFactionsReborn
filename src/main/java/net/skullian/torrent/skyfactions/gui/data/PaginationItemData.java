package net.skullian.torrent.skyfactions.gui.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaginationItemData {

    private String ITEM_ID;
    private char CHARACTER;
    private String NAME;
    private String MATERIAL;
    private String BASE64_TEXTURE;
    private String SOUND;
    private int PITCH;
    private String MORE_PAGES_LORE;
    private String NO_PAGES_LORE;
}
