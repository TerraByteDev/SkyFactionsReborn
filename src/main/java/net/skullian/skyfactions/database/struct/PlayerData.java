package net.skullian.skyfactions.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PlayerData {

    private final UUID UUID;
    private final String DISCORD_ID;
    private final long LAST_RAID;
    private final String LOCALE;

}
