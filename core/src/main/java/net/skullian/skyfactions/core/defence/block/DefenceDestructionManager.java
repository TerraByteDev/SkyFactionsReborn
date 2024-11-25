package net.skullian.skyfactions.core.defence.block;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class DefenceDestructionManager {
    public static void addSlowDig(Player player, int duration) {
        if(player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) removeSlowDig(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, -1, false, false, false), true);
    }

    public static void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
    }
}
