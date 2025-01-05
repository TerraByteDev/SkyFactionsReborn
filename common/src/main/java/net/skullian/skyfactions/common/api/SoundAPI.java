package net.skullian.skyfactions.common.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.skullian.skyfactions.common.SharedConstants;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;

import java.util.concurrent.CompletableFuture;

public abstract class SoundAPI {

    public void playSound(SkyUser player, String identifier, float pitch, float volume) {
        Sound sound = getSound(identifier, pitch, volume);
        if (sound != null) player.playSound(sound, pitch, volume);
    }

    public abstract void playSound(SkyLocation location, String identifier, float pitch, float volume);

    public Sound getSound(String identifier, float pitch, float volume) {
        return Sound.sound(Key.key(identifier), Sound.Source.MASTER, volume, pitch);
    }

    public abstract void playMusic(SkyUser def, SkyUser att);

    public void soundAlarm(SkyUser player) {
        CompletableFuture.runAsync(() -> {
            if (player != null) {
                String name = Settings.ALARM_SOUND.getString();
                float pitch = Settings.ALARM_SOUND_PITCH.getInt();
                int dur = 100;
                int val = 50;
                int it = dur / val + 1;

                for (int i = 0; i < it; i++) {
                    playSound(player, name, pitch, 1f);
                    try {
                        Thread.sleep(val);
                    } catch (InterruptedException error) {
                        SLogger.fatal("Failed to sound alarm for player {} - {}", player.getUniqueId(), error);
                    }
                }
            }
        }, SharedConstants.GLOBAL_EXECUTOR);
    }
}
