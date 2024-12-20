package net.skullian.skyfactions.common.database.struct;

import net.skullian.skyfactions.common.user.SkyUser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InviteData {

    private SkyUser player;
    private SkyUser inviter;
    private String factionName;
    private String type;
    private long timestamp;

}
