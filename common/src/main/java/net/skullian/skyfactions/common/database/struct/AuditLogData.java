package net.skullian.skyfactions.common.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.common.user.SkyUser;

@AllArgsConstructor
@Getter
public class AuditLogData {

    private SkyUser player;
    private String factionName;
    private String type;
    private Object[] replacements;
    private long timestamp;
}
