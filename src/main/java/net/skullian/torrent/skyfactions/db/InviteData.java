package net.skullian.torrent.skyfactions.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor
@Getter
public class InviteData {

    private OfflinePlayer player;
    private OfflinePlayer inviter;
    private String factionName;
    private String type;
    private long timestamp;

}
