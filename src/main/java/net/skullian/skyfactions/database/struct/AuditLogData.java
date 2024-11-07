package net.skullian.skyfactions.database.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.List;

@AllArgsConstructor
@Getter
public class AuditLogData {

    private OfflinePlayer player;
    private String factionName;
    private String type;
    private Object[] replacements;
    private long timestamp;
}
