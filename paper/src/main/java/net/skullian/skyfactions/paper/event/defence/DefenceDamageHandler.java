package net.skullian.skyfactions.paper.event.defence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityDeathData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.PaperSharedConstants;
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

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class DefenceDamageHandler implements Listener {

    public static Map<UUID, Map<DamageType, DefenceEntityDeathData>> toDie = new HashMap<>();

    @EventHandler
    public void onProjectileEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null) return;

        if (!(event.getHitEntity() instanceof LivingEntity hitEntity)) return;

        NamespacedKey damageKey = PaperSharedConstants.DEFENCE_DAMAGE_KEY;
        NamespacedKey messageKey = PaperSharedConstants.DEFENCE_DAMAGE_MESSAGE_KEY;

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (container.has(damageKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            event.getEntity().remove();

            int damage = Objects.requireNonNull(container.get(damageKey, PersistentDataType.INTEGER));

            hitEntity.damage(damage);
            hitEntity.knockback(0.5f, 0.5f, 0.5f);

            if (hitEntity.getType().equals(EntityType.PLAYER) && container.has(messageKey, PersistentDataType.STRING)) {
                String message = container.get(messageKey, PersistentDataType.STRING);
                Player player = (Player) hitEntity;
                SkyUser user = SkyApi.getInstance().getUserManager().getUser(player.getUniqueId());

                hitEntity.sendMessage(TextUtility.color(message, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), user));
            }
        }
    }

    @EventHandler
    public void onPlayerDeathFromDefence(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(player.getUniqueId());
        if (toDie.containsKey(player.getUniqueId())) {

            DefenceEntityDeathData data = getData(player.getUniqueId(), event.getDamageSource().getDamageType());
            if (data == null) return;

            String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
            String deathMessage = data.getDEATH_MESSAGE();
            event.deathMessage(TextUtility.color(deathMessage
                    .replaceAll("player_name", player.getName())
                    .replaceAll("defender", "DEFENDER_NAME"), locale, user));

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
                defence.removeDeadEntity(entity.getEntityId());
            }
        }
    }

    private DefenceEntityDeathData getData(UUID uuid, DamageType type) {
        Map<DamageType, DefenceEntityDeathData> map = toDie.get(uuid);
        if (map == null) return null;
        return map.get(type);
    }

    private List<Defence> getDefencesFromData(DefenceEntityDeathData data) {
        if (SkyApi.getInstance().getDefenceAPI().isFaction(data.getOWNER())) return SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().get(data.getOWNER());
            else return SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().get(UUID.fromString(data.getOWNER()));
    }
}
