package net.skullian.skyfactions.npc.factory;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.util.NpcLocation;
import lombok.AllArgsConstructor;
import net.skullian.skyfactions.npc.SkyNPC;
import net.skullian.skyfactions.util.text.TextUtility;

public class ZNPCsFactory implements SkyNPCFactory {

    @Override
    public boolean isNPC(Entity entity) {
        return false; // ZNPCs is packet based!
    }

    @Override
    public SkyNPC create(String id, String name, Location location, String skin, EntityType entityType) {
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().create(
            id,
            location.getWorld(),
            NpcApiProvider.get().getNpcTypeRegistry().getByName(entityType.name()),
            new NpcLocation(location)
        );
        entry.enableEverything();
        entry.getNpc().getHologram().insertLine(0, TextUtility.color(name));

        return new SkyZNPCs(entry); 
    }

    @Override
    public SkyNPC getNPC(String id) {
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().getById(id);
        
        return entry == null ? null : new SkyZNPCs(entry);
    }

    @AllArgsConstructor
    public static class SkyZNPCs implements SkyNPC {

        private NpcEntry npc;

        @Override
        public String getId() {
            return npc.getId();
        }

        @Override
        public Location getLocation() {
            return npc.getNpc().getLocation().toBukkitLocation(npc.getNpc().getWorld());
        }

        @Override
        public boolean isPresent() {
            return npc.getNpc().isEnabled();
        }

        @Override
        public Entity getEntity() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return npc.getNpc().getHologram().lineCount() == 0 ? getId()
                : npc.getNpc().getHologram().getLine(0);
        }

        @Override
        public void remove() {
            NpcApiProvider.get().getNpcRegistry().delete(getId());
        }

    }

}