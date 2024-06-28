package net.skullian.torrent.skyfactions.util.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GUIData {


    private String TITLE;
    private String OPEN_SOUND;
    private int OPEN_PITCH;

    private String[] LAYOUT;
    private List<String> ITEMS;

}
