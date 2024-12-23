package net.skullian.skyfactions.common.island;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("LombokSetterMayBeUsed, NonFinalFieldInEnum")
@Getter
public enum IslandModificationAction {

    CREATE(0),
    REMOVE(0);

    @Setter
    private int id;
    IslandModificationAction(int id) {
        this.id = id;
    }

}
