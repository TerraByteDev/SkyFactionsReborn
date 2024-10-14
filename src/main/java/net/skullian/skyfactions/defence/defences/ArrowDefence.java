package net.skullian.skyfactions.defence.defences;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class ArrowDefence extends Defence {

    public ArrowDefence(DefenceData defenceData, DefenceStruct defenceStruct) {
        super(defenceData, defenceStruct);
    }

    @Override
    public void enable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyFactionsReborn.getInstance(), new Runnable() {
            @Override
            public void run() {
                getRandomEntity(getDefenceLocation().getWorld()).whenComplete((entities, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        Bukkit.getScheduler().cancelTask(getTask());
                        return;
                    } else if (entities.isEmpty()) return;

                    for (LivingEntity entity : entities) {

                    }
                });
            }
        }, 0L, (getRate() * 20L) /* rate in seconds to ticks */);
    }
}
