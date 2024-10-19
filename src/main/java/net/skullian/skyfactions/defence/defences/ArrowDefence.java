package net.skullian.skyfactions.defence.defences;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
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
        if (!isAllowed(getStruct().getPROJECTILE()))
            throw new IllegalArgumentException("Disallowed configured projectile: " + getStruct().getPROJECTILE() + "\nSee https://docs.terrabytedev.com/skyfactions/mechanics/defences/projectiles");

        setTask(
                Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyFactionsReborn.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (!isAllowed(getStruct().getPROJECTILE())) return;

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
                                Object projectileObject;

                                if (type == EntityType.ARROW || type == EntityType.SPECTRAL_ARROW) {
                                    projectileObject = entity.getType() == EntityType.SPECTRAL_ARROW ? SpectralArrow.class : Arrow.class;
                                    world.spawnArrow(defenceLocation, direction, (float) 3.0, 3.0f, (Class) projectileObject);
                                } else {
                                    projectileObject = world.spawnEntity(defenceLocation, type);
                                    ((Entity) projectileObject).setVelocity(((Entity) projectileObject).getVelocity().multiply(3.0f));

                                    applyPDC((Entity) projectileObject);
                                }
                            }
                        });
                    }
                }, 0L, (getRate() * 20L) /* rate in seconds to ticks */)
        );
    }
}
