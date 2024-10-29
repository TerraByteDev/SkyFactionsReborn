package net.skullian.skyfactions.npc.factory;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.npc.SkyNPC;

public class CitizensFactory implements SkyNPCFactory, Listener {

    @Override
    public boolean isNPC(Entity entity) {
        // Citizens' docs tell you to check for metadata, but we use this to avoid compat issues
        // other plugins that may use the same metadata system.
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    @Override
    public SkyNPC create(String id, String name, Location location, String skin, EntityType entityType, boolean isFaction) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(entityType, UUID.randomUUID(), Integer.parseInt(id), name);
        npc.spawn(location);

        if (skin.equalsIgnoreCase("none") && npc.hasTrait(SkinTrait.class)) {
            npc.getTraitNullable(SkinTrait.class).clearTexture();
        } else if (entityType == EntityType.PLAYER) {
            npc.getOrAddTrait(SkinTrait.class).setSkinName(skin
                .replace("texture:", "")
                .replace("player:", "")
                .replace("url:", "")
            );
        }

        return new SkyCitizen(npc, isFaction);
    }

    @Override
    public SkyNPC getNPC(String id) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id));
        return npc == null ? null : new SkyCitizen(npc, false);
    }

    @EventHandler
    public void onInteract(NPCClickEvent event) {
        SkyFactionsReborn.npcManager.onClick(new SkyCitizen(event.getNPC(), false), event.getClicker());
    }

    @AllArgsConstructor
    @Getter
    public static class SkyCitizen implements SkyNPC {

        private NPC npc;
        private boolean faction;

        @Override
        public String getId() {
            return Integer.toString(npc.getId());
        }

        @Override
        public String getDisplayName() {
            return npc.getName();
        }

        @Override
        public Location getLocation() {
            return npc.getStoredLocation();
        }

        @Override
        public boolean isPresent() {
            return npc.isSpawned();
        }

        @Override
        public Entity getEntity() {
            return npc.getEntity();
        }

        @Override
        public void remove() {
            npc.despawn(DespawnReason.REMOVAL);
            CitizensAPI.getNPCRegistry().deregister(npc);
        }

    }

}