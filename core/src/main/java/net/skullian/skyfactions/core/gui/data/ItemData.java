package net.skullian.skyfactions.core.gui.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ItemData {

    private String ITEM_ID;
    private char CHARACTER;
    private String NAME;
    private String MATERIAL;
    private String BASE64_TEXTURE;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
}
