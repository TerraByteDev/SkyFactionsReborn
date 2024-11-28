package net.skullian.skyfactions.core.event.defence;

import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotDefenceAPI;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityDeathData;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefenceDamageHandler implements Listener {

    public static Map<UUID, Map<DamageType, DefenceEntityDeathData>> toDie = new HashMap<>();

    @EventHandler
    public void onProjectileEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null) return;

        if (!(event.getHitEntity() instanceof LivingEntity hitEntity)) return;

        NamespacedKey damageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-damage");
        NamespacedKey messageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "damage-message");

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (container.has(damageKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            event.getEntity().remove();

            int damage = container.get(damageKey, PersistentDataType.INTEGER);

            hitEntity.damage(damage);
            hitEntity.knockback(0.5f, 0.5f, 0.5f);

            if (hitEntity.getType().equals(EntityType.PLAYER) && container.has(messageKey, PersistentDataType.STRING)) {
                String message = container.get(messageKey, PersistentDataType.STRING);
                Player player = (Player) hitEntity;
                hitEntity.sendMessage(TextUtility.color(message, SpigotPlayerAPI.getLocale(player.getUniqueId()), player));
            }
        }
    }

    @EventHandler
    public void onPlayerDeathFromDefence(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (toDie.containsKey(player.getUniqueId())) {

            DefenceEntityDeathData data = getData(player.getUniqueId(), event.getDamageSource().getDamageType());
            if (data == null) return;

            String deathMessage = data.getDEATH_MESSAGE();
            event.deathMessage(TextUtility.color(deathMessage
                    .replaceAll("player_name", player.getName())
                    .replaceAll("defender", "DEFENDER_NAME"), SpigotPlayerAPI.getLocale(player.getUniqueId()), player));

            removeDeadEntity(event.getPlayer(), data);
        }
    }

    @EventHandler
    public void onEntityDeathFromDefence(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        DefenceEntityDeathData data = getData(entity.getUniqueId(), event.getDamageSource().getDamageType());
        if (data == null) return;

        removeDeadEntity(entity, data);
    }

    private void removeDeadEntity(LivingEntity entity, DefenceEntityDeathData data) {
        for (Defence defence : getDefencesFromData(data)) {
            if (defence.getDefenceLocation().equals(data.getDEFENCE_LOCATION())) {
                defence.removeDeadEntity(entity);
            }
        }
    }

    private DefenceEntityDeathData getData(UUID uuid, DamageType type) {
        Map<DamageType, DefenceEntityDeathData> map = toDie.get(uuid);
        if (map == null) return null;
        return map.get(type);
    }

    private List<Defence> getDefencesFromData(DefenceEntityDeathData data) {
        if (SpigotDefenceAPI.isFaction(data.getOWNER())) return DefencePlacementHandler.loadedFactionDefences.get(data.getOWNER());
        else return DefencePlacementHandler.loadedPlayerDefences.get(UUID.fromString(data.getOWNER()));
    }
}
