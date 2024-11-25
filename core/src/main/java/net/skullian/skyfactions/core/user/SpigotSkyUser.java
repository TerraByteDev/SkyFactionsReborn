package net.skullian.skyfactions.core.user;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotSkyUser extends SkyUser {

    private PlayerData data;
    private int gems;
    private int runes;
    private PlayerIsland island;
    private String belongingFaction;
    private List<NotificationData> notifications;
    private List<InviteData> incomingInvites;
    private JoinRequestData activeJoinRequest;

    public SpigotSkyUser(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            this.data = SkyApi.getInstance().getDatabaseManager().getPlayerManager().getPlayerData(uuid).join();
            this.gems = SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getGems(uuid).join();
            this.runes = SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getRunes(uuid).join();
            this.island = SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getPlayerIsland(uuid).join();
            this.belongingFaction = SkyApi.getInstance().getDatabaseManager().getFactionsManager().getFaction(uuid).join();
            SkyApi.getInstance().getDatabaseManager().getFactionsManager().getFaction(uuid).join();
        });
    }

    @Override
    public PlayerData getPlayerData() {
        return data;
    }

    @Override
    public Object getPlayerInstance() {
        return Bukkit.getOfflinePlayer(data.getUUID());
    }

    @Override
    public int getGems() {
        return gems;
    }

    @Override
    public int getRunes() {
        return runes;
    }

    @Override
    public SkyIsland getIsland() {
        return island;
    }

    @Override
    public String getBelongingFaction() {
        return belongingFaction;
    }

    @Override
    public List<NotificationData> getNotifications() {
        return notifications;
    }

    @Override
    public List<InviteData> getIncomingInvites() {
        return incomingInvites;
    }

    @Override
    public JoinRequestData getActiveJoinRequest() {
        return activeJoinRequest;
    }
}
