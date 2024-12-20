package net.skullian.skyfactions.paper.defence.defences;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.defence.SpigotDefence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArrowDefence extends SpigotDefence {

    public ArrowDefence(DefenceData defenceData, DefenceStruct defenceStruct) {
        super(defenceData, defenceStruct);
    }

    @Override
    public void onEnable() {
        if (!isAllowed(getStruct().getPROJECTILE())) {
            throw new IllegalArgumentException("Disallowed configured projectile: " + getStruct().getPROJECTILE() + "\nSee https://docs.terrabytedev.com/skyfactions/mechanics/defences/projectiles");
        }

        //✨✨ hacky!!
        setTask(getService().scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            if (!isAllowed(getStruct().getPROJECTILE()) || !canShoot()) return;

            List<Object> entities = getRandomEntity(getDefenceLocation().getWorldName());
            for (Object entityObject : entities) {
                LivingEntity entity = (LivingEntity) entityObject;

                World world = entity.getWorld();
                Location defenceLocation = SpigotAdapter.adapt(getDefenceLocation());
                Vector direction = entity.getLocation().subtract(defenceLocation).toVector().normalize();
                EntityType type = EntityType.valueOf(getStruct().getPROJECTILE());
                Entity projectile = spawnProjectile(world, defenceLocation, direction, type);
                applyPDC(projectile);
                onShoot();
            }
        }), 0L, getRate(), TimeUnit.SECONDS));
    }

    private Entity spawnProjectile(World world, Location location, Vector direction, EntityType type) {
        Entity projectile;
        if (type == EntityType.ARROW || type == EntityType.SPECTRAL_ARROW) {
            Class<? extends AbstractArrow> arrowClass = (type == EntityType.SPECTRAL_ARROW) ? SpectralArrow.class : Arrow.class;
            projectile = world.spawnArrow(location, direction, 3.0f, 3.0f, arrowClass);
        } else {
            projectile = world.spawnEntity(location, type);
            projectile.setVelocity(projectile.getVelocity().multiply(3.0f));
        }
        return projectile;
    }
}
