package net.skullian.skyfactions.paper.defence.hologram;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.nms.NMSProvider;
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
        NMSProvider.getInstance().removeHologram(this);
    }

    @Override
    public void update() {
        NMSProvider.getInstance().removeHologram(this);
    }

    @Override
    public void kill() {
        NMSProvider.getInstance().removeHologram(this);
    }

    @Override
    public void updateAffectedPlayers() {
        NMSProvider.getInstance().updateHologram(this);
    }

    @Override
    public String getTextWithoutColor() {
        return ChatColor.stripColor(PlainTextComponentSerializer.plainText().serialize(getTextAsComponent()));
    }
}
