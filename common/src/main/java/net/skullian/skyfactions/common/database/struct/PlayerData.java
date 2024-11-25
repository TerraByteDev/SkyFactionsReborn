package net.skullian.skyfactions.common.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Setter
@Getter
public class PlayerData {

    private final UUID UUID;
    private String DISCORD_ID;
    private long LAST_RAID;
    private String LOCALE;

}
