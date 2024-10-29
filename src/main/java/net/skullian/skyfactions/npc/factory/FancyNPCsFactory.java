package net.skullian.skyfactions.npc.factory;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.npc.SkyNPC;

public class FancyNPCsFactory implements SkyNPCFactory, Listener {

    @Override
    public boolean isNPC(Entity entity) {
        return false;
    }

    @Override
    public SkyNPC create(String id, String name, Location location, String skin, EntityType entityType, boolean isFaction) {
        NpcData data = new NpcData(id, null, location);
        SkinFetcher.SkinData fetched = new SkinFetcher.SkinData(skin
            .replace("url:", "")
            .replace("texture:", "")
            .replace("player:", ""), 
            null, null
        );
        data.setType(entityType);
        data.setSkin(fetched);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);

        return new SkyFancyNPC(npc, isFaction);
    }

    @Override
    public SkyNPC getNPC(String id) {
        Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(id);
        return npc == null ? null : new SkyFancyNPC(npc, false);
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        SkyFactionsReborn.npcManager.onClick(new SkyFancyNPC(event.getNpc(), false), event.getPlayer());
    }
    
    @AllArgsConstructor
    @Getter
    public static class SkyFancyNPC implements SkyNPC {

        private Npc npc;
        private boolean faction;

        @Override
        public String getId() {
            return npc.getData().getId();
        }

        @Override
        public String getDisplayName() {
            return npc.getData().getDisplayName();
        }

        @Override
        public Location getLocation() {
            return npc.getData().getLocation();
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public Entity getEntity() {
            return null;
        }

        @Override
        public void remove() {
            npc.removeForAll();
            FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
        }
        
    }

}