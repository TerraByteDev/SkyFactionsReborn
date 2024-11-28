package net.skullian.skyfactions.core.npc.factory;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.npc.factory.SkyNPCFactory;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.core.api.adapter.SpigotAdapter;
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
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.common.npc.SkyNPC;

public class FancyNPCsFactory implements SkyNPCFactory, Listener {

    @Override
    public boolean isNPC(Object entity) {
        return false;
    }

    @Override
    public SkyNPC create(String id, String name, SkyLocation location, String skin, Object entityType, boolean isFaction) {
        NpcData data = new NpcData(id, null, SpigotAdapter.adapt(location));


        if (!skin.equalsIgnoreCase("none")) {
            SkinFetcher.SkinData fetched = new SkinFetcher.SkinData(skin
                .replace("url:", "")
                .replace("texture:", "")
                .replace("player:", ""), 
                null, null
            );
            data.setSkin(fetched);
        }
        data.setType((EntityType) entityType);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        npc.create();
        npc.spawnForAll();

        return new SkyFancyNPC(npc, isFaction);
    }

    @Override
    public SkyNPC getNPC(String id, boolean faction) {
        Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(id);
        return npc == null ? null : new SkyFancyNPC(npc, faction);
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        SkyFactionsReborn.getNpcManager().onClick(new SkyFancyNPC(event.getNpc(), false), SkyApi.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId()));
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

        @Override
        public void updateDisplayName(String name) {
            npc.getData().setDisplayName(name);
            npc.updateForAll();
        }

        @Override
        public void updateEntityType(EntityType type) {
            npc.getData().setType(type);
            npc.updateForAll();
        }

        @Override
        public void updateSkin(String skin) {
            SkinFetcher.SkinData fetched = new SkinFetcher.SkinData(skin
                .replace("url:", "")
                .replace("texture:", "")
                .replace("player:", ""), 
                null, null
            );
            npc.getData().setSkin(fetched);
            npc.updateForAll();
        }

        @Override
        public EntityType getEntityType() {
            return npc.getData().getType();
        }
        
    }

}