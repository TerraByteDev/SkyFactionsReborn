package net.skullian.skyfactions.block;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowBlockHandler {

    public void addSlowDig(Player player, int duration) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, -1, false, false, false), true);
    }

    public void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
    }
}