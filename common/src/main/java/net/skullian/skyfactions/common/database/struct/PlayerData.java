package net.skullian.skyfactions.common.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PlayerData {

    private String DISCORD_ID;
    private long LAST_RAID;
    private String LOCALE;

}
