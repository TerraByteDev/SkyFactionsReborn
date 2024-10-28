package net.skullian.skyfactions.npc.factory;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.util.NpcLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.npc.SkyNPC;
import net.skullian.skyfactions.util.text.TextUtility;

public class ZNPCsPlusFactory implements SkyNPCFactory, Listener {

    @Override
    public boolean isNPC(Entity entity) {
        return false; // ZNPCs is packet based!
    }

    @Override
    public SkyNPC create(String id, String name, Location location, String skin, EntityType entityType, boolean isFaction) {
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().create(
            id,
            location.getWorld(),
            NpcApiProvider.get().getNpcTypeRegistry().getByName(entityType.name()),
            new NpcLocation(location)
        );
        entry.enableEverything();
        entry.getNpc().getHologram().insertLine(0, TextUtility.color(name));

        return new SkyZNPCs(entry, isFaction); 
    }

    @Override
    public SkyNPC getNPC(String id) {
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().getById(id);
        
        return entry == null ? null : new SkyZNPCs(entry, false);
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        SkyFactionsReborn.npcManager.onClick(new SkyZNPCs(event.getEntry(), false), event.getPlayer());
    }

    @AllArgsConstructor
    @Getter
    public static class SkyZNPCs implements SkyNPC {

        private NpcEntry npc;
        private boolean faction;

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