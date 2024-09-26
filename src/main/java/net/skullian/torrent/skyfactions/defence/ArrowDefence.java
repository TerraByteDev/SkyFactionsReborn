package net.skullian.torrent.skyfactions.defence;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrowDefence {

    private int level;
    private int damage;
    private int radius;
    private int speed;
    private boolean canDetectInvis;

    public ArrowDefence(String type) {

    }


}
