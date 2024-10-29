package net.skullian.skyfactions.db;

import org.bukkit.OfflinePlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InviteData {

    private OfflinePlayer player;
    private OfflinePlayer inviter;
    private String factionName;
    private String type;
    private long timestamp;

}
