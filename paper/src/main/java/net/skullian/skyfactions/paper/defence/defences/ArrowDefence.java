package net.skullian.skyfactions.paper.defence.defences;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class ArrowDefence extends Defence {

    public ArrowDefence(DefenceData defenceData, DefenceStruct defenceStruct) {
        super(defenceData, defenceStruct);
    }

    @Override
    public void enable() {
        if (!isAllowed(getStruct().getPROJECTILE())) {
            throw new IllegalArgumentException("Disallowed configured projectile: " + getStruct().getPROJECTILE() + "\nSee https://docs.terrabytedev.com/skyfactions/mechanics/defences/projectiles");
        }

        setTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyFactionsReborn.getInstance(), () -> {
            if (!isAllowed(getStruct().getPROJECTILE()) || !canShoot()) return;

            getRandomEntity(getDefenceLocation().getWorld()).whenComplete((entities, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    Bukkit.getScheduler().cancelTask(getTask());
                    return;
                } else if (entities.isEmpty()) return;

                for (LivingEntity entity : entities) {
                    World world = entity.getWorld();
                    Location defenceLocation = getDefenceLocation();
                    Vector direction = entity.getLocation().subtract(defenceLocation).toVector().normalize();
                    EntityType type = EntityType.valueOf(getStruct().getPROJECTILE());
                    Entity projectile = spawnProjectile(world, defenceLocation, direction, type);
                    applyPDC(projectile);
                    onShoot();
                }
            });
        }, 0L, getRate() * 20L));
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
