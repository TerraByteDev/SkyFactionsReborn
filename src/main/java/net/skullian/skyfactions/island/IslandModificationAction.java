package net.skullian.skyfactions.island;

import lombok.Getter;
import lombok.Setter;

public enum IslandModificationAction {

    CREATE(0),
    REMOVE(0);

    @Getter
    @Setter
    private int id;
    IslandModificationAction(int id) {
        this.id = id;
    }

}
