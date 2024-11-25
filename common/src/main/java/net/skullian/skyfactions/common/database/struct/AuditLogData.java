package net.skullian.skyfactions.common.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor
@Getter
public class AuditLogData {

    private OfflinePlayer player;
    private String factionName;
    private String type;
    private Object[] replacements;
    private long timestamp;
}
