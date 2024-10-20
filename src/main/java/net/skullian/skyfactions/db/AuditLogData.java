package net.skullian.skyfactions.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor
@Getter
public class AuditLogData {

    private OfflinePlayer player;
    private String factionName;
    private String type;
    private String description;
    private long timestamp;
}
