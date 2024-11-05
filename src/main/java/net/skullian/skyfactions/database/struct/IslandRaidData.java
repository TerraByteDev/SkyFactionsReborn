package net.skullian.skyfactions.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class IslandRaidData {

    private int id;
    private String uuid;
    private long last_raided;
}
