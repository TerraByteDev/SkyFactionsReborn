package net.skullian.skyfactions.paper.npc.factory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.npc.factory.SkyNPCFactory;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.api.entity.EntityPropertyRegistry;
import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.skin.SkinDescriptor;
import lol.pyr.znpcsplus.api.skin.SkinDescriptorFactory;
import lol.pyr.znpcsplus.util.NpcLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.npc.SkyNPC;
import net.skullian.skyfactions.common.util.text.TextUtility;

public class ZNPCsPlusFactory implements SkyNPCFactory, Listener {

    @Override
    public boolean isNPC(Object entity) {
        return false; // ZNPCs is packet based!
    }

    @Override
    public SkyNPC create(String id, String name, SkyLocation sLocation, String skin, Object entityType, boolean isFaction) {
        Location location = SpigotAdapter.adapt(sLocation);
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().create(
            id,
            location.getWorld(),
            NpcApiProvider.get().getNpcTypeRegistry().getByName(((EntityType) entityType).name()),
            new NpcLocation(location)
        );
        
        if (!skin.equalsIgnoreCase("none")) {
            EntityPropertyRegistry registry = NpcApiProvider.get().getPropertyRegistry();
            EntityProperty<SkinDescriptor> skinProperty = registry.getByName("skin", SkinDescriptor.class);
            entry.getNpc().setProperty(skinProperty, getSkinDescriptor(skin));
        }

        entry.getNpc().getHologram().insertLine(0, TextUtility.legacyColor(name, null, null));
        entry.enableEverything();
        
        return new SkyZNPCs(entry, isFaction); 
    }
    
    public static SkinDescriptor getSkinDescriptor(String skin) {
        SkinDescriptorFactory factory = NpcApiProvider.get().getSkinDescriptorFactory();

        if (isValidURL(skin.replace("url:", ""))) {
            return factory.createUrlDescriptor(skin.replace("url:", ""), "classic");
        } else {
            return skin.startsWith("player:") ? factory.createRefreshingDescriptor(skin.replace("player:", "")) : factory.createStaticDescriptor(skin.replace("texture:", ""), null);
        }
    }

    private static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public SkyNPC getNPC(String id, boolean faction) {
        NpcEntry entry = NpcApiProvider.get().getNpcRegistry().getById(id);
        
        return entry == null ? null : new SkyZNPCs(entry, faction);
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        SkyFactionsReborn.getNpcManager().onClick(new SkyZNPCs(event.getEntry(), false), SkyApi.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId()));
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
        public SkyLocation getLocation() {
            return SpigotAdapter.adapt(npc.getNpc().getLocation().toBukkitLocation(npc.getNpc().getWorld()));
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

        @Override
        public void updateDisplayName(String name) {
            npc.getNpc().getHologram().insertLine(0, name);
        }

        @Override
        public void updateEntityType(String type) {
            npc.getNpc().setType(NpcApiProvider.get().getNpcTypeRegistry().getByName(EntityType.valueOf(type).name()));
        }

        @Override
        public void updateSkin(String skin) {
            EntityPropertyRegistry registry = NpcApiProvider.get().getPropertyRegistry();
            EntityProperty<SkinDescriptor> skinProperty = registry.getByName("skin", SkinDescriptor.class);
            npc.getNpc().setProperty(skinProperty, getSkinDescriptor(skin));
        }

        @Override
        public String getEntityType() {
            return EntityType.valueOf(npc.getNpc().getType().getName()).name();
        }
    }
}