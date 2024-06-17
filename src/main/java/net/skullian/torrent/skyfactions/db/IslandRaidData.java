package net.skullian.torrent.skyfactions.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class IslandRaidData {

    private int id;
    private String uuid;
    private int last_raided;
}
