package net.skullian.skyfactions.paper.defence.hologram;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.bukkit.ChatColor;

public class SpigotDefenceTextHologram extends DefenceTextHologram {

    public SpigotDefenceTextHologram(String id, RenderMode renderMode, String owner, DefenceStruct defence, DefenceData data) {
        super(id, renderMode, owner, defence, data);
    }

    public SpigotDefenceTextHologram(String id, String owner, DefenceStruct defence, DefenceData data) {
        super(id, owner, defence, data);
    }

    @Override
    public void spawn(SkyLocation location) {
        SkyApi.getInstance().getNMSProvider().getInstance().spawnHologram(this);
    }

    @Override
    public void update() {
        SkyApi.getInstance().getNMSProvider().getInstance().updateHologram(this);
    }

    @Override
    public void kill() {
        SkyApi.getInstance().getNMSProvider().getInstance().removeHologram(this);
    }

    @Override
    public void updateAffectedPlayers() {
        SkyApi.getInstance().getNMSProvider().getInstance().updateHologram(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getTextWithoutColor() {
        return ChatColor.stripColor(PlainTextComponentSerializer.plainText().serialize(getTextAsComponent()));
    }
}
