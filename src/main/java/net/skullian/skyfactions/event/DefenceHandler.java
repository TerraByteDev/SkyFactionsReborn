package net.skullian.skyfactions.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.retrooper.packetevents.protocol.world.damagetype.DamageType;
import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.hologram.TextHologram;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefenceHandler implements Listener {
    public static final Map<String, List<Defence>> loadedFactionDefences = new HashMap<>();
    public static final Map<UUID, List<Defence>> loadedPlayerDefences = new HashMap<>();

    public static final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();
    public static final Map<UUID, Map<DamageType, String>> toDie = new HashMap<>();

    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String defenceIdentifier = container.get(defenceKey, PersistentDataType.STRING);
            System.out.println(defenceIdentifier);

            DefenceStruct defence = DefencesFactory.defences.get(defenceIdentifier);
            if (defence != null) {
                Block placed = event.getBlockPlaced();
                Location belowLoc = placed.getLocation().clone();
                belowLoc.setY(belowLoc.getY() - 1);

                Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                if (!isAllowedBlock(belowBlock, defence)) {
                    event.setCancelled(true);
                    player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE().replace("%server_name%", Messages.SERVER_NAME.getString())));
                    return;
                }

                returnOwnerDependingOnLocation(placed.getLocation(), player).whenComplete((owner, ex) -> {
                    try {
                        if (ex != null) {
                            ErrorHandler.handleError(player, "place your defence", "SQL_FACTION_GET", ex);
                            return;
                        } else if (owner == null) {
                            event.setCancelled(true);
                            return;
                        }


                        boolean isFaction = isFaction(owner);
                        DefenceData data = new DefenceData(1, defenceIdentifier, 0, placed.getLocation().getWorld().getName(), placed.getLocation().getBlockX(), placed.getLocation().getBlockY(), placed.getLocation().getBlockZ(), owner, isFaction, defence.getENTITY_CONFIG().isTARGET_HOSTILE_DEFAULT(), defence.getENTITY_CONFIG().isTARGET_PASSIVE_DEFAULT());
                        ObjectMapper mapper = new ObjectMapper();

                        PersistentDataContainer blockContainer = new CustomBlockData(placed, SkyFactionsReborn.getInstance());
                        NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

                        blockContainer.set(defenceKey, PersistentDataType.STRING, defenceIdentifier);
                        blockContainer.set(dataKey, PersistentDataType.STRING, mapper.writeValueAsString(data));

                        Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(defenceIdentifier);
                        System.out.println(defenceClass);
                        Defence instance = defenceClass.getDeclaredConstructor().newInstance(data);

                        instance.onLoad(owner);
                        addIntoMap(owner, isFaction, instance);
                    } catch (Exception error) {
                        event.setCancelled(true);
                        ErrorHandler.handleError(event.getPlayer(), "place your defence", "DEFENCE_PROCESSING_EXCEPTION", error);
                    }
                });
            } else {
                event.setCancelled(true);
                ErrorHandler.handleError(player, "place your defence", "DEFENCE_PROCESSING_EXCEPTION", new IllegalArgumentException("Failed to find defence with the name of " + defenceIdentifier));
            }
        }
    }

    private void addIntoMap(String owner, boolean isFaction, Defence defence) {
        if (isFaction) {
            if (!loadedFactionDefences.containsKey(owner)) {
                loadedFactionDefences.put(owner, List.of(defence));
            } else {
                List<Defence> defences = loadedFactionDefences.get(owner);
                defences.add(defence);
                loadedFactionDefences.replace(owner, defences);
            }
        } else {
            UUID playeruuid = UUID.fromString(owner);
            if (!loadedPlayerDefences.containsKey(playeruuid)) {
                loadedPlayerDefences.put(playeruuid, List.of(defence));
            } else {
                List<Defence> defences = loadedPlayerDefences.get(owner);
                defences.add(defence);
                loadedPlayerDefences.replace(playeruuid, defences);
            }
        }
    }

    private boolean isFaction(String uuid) {
        try {
            UUID.fromString(uuid);
            return false; // is player uuid
        } catch (Exception ignored) {
            return true; // is false
        }
    }

    private CompletableFuture<String> returnOwnerDependingOnLocation(Location location, Player player) {
        if (location.getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString()) && FactionAPI.isLocationInRegion(location, player.getUniqueId().toString())) {
            return CompletableFuture.completedFuture(player.getUniqueId().toString());
        } else if (location.getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
            return FactionAPI.getFaction(player).thenApply((faction) -> {
                if (faction != null && FactionAPI.isLocationInRegion(location, faction.getName())) {
                    return faction.getName();
                } else return null;
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
            Messages.DEFENCE_DESTROY_DENY.send(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityDamageFromDefence(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager(); // get the damager

        NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-damage");
        PersistentDataContainer container = damager.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.INTEGER)) {
            int damage = container.get(key, PersistentDataType.INTEGER);

            event.setDamage(damage);
        }
    }

    @EventHandler
    public void onProjectileEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null) return;

        if (!(event.getHitEntity() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) event.getHitEntity();

        NamespacedKey damageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-damage");
        NamespacedKey messageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "damage-message");

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (container.has(damageKey, PersistentDataType.INTEGER)) {
            int damage = container.get(damageKey, PersistentDataType.INTEGER);

            hitEntity.damage(damage);

            if (hitEntity.getType().equals(EntityType.PLAYER) && container.has(messageKey, PersistentDataType.STRING)) {
                String message = container.get(messageKey, PersistentDataType.STRING);
                hitEntity.sendMessage(TextUtility.color(message));
            }
        }
    }

    @EventHandler
    public void onPlayerDeathFromDefence(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (toDie.containsKey(player.getUniqueId())) {
            Map<DamageType, String> damages = new HashMap<>();

            String deathMessage = damages.get(event.getDamageSource().getDamageType());
            if (deathMessage != null) {

                event.setDeathMessage(TextUtility.color(deathMessage
                        .replaceAll("%player_name%", player.getName())
                        .replaceAll("%defender%", "DEFENDER_NAME")));
            }
        }
    }

    private boolean isAllowedBlock(Block block, DefenceStruct defenceStruct) {
        boolean isWhitelist = defenceStruct.isIS_WHITELIST();

        if (isWhitelist) return defenceStruct.getPLACEMENT_LIST().contains(block.getType().name());
        else return !defenceStruct.getPLACEMENT_LIST().contains(block.getType().name());
    }

    public static void addPlacedDefences(String factionName) {
        if (loadedFactionDefences.get(factionName) != null) return;
        SkyFactionsReborn.databaseHandler.getDefenceLocations(factionName).whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();
                if (block == null) continue;

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    DefenceStruct defence = DefencesFactory.defences.get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(name);
                            Defence instance = defenceClass.getDeclaredConstructor().newInstance(defenceData);

                            instance.onLoad(factionName);
                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }

            loadedFactionDefences.put(factionName, defences);
        });
    }

    public static void addPlacedDefences(Player player) {
        if (loadedPlayerDefences.get(player.getUniqueId()) != null) return;
        SkyFactionsReborn.databaseHandler.getDefenceLocations(player.getUniqueId()).whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();
                if (block == null) continue;

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    DefenceStruct defence = DefencesFactory.defences.get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(name);
                            Defence instance = defenceClass.getDeclaredConstructor().newInstance(defenceData);

                            instance.onLoad(player.getUniqueId().toString());
                            defences.add(instance);

                            Messages.DEFENCE_PLACE_SUCCESS.send(player, "%defence_name%", defence.getNAME());
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }

            loadedPlayerDefences.put(player.getUniqueId(), defences);
        });
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        String fromWorldName = event.getFrom().getWorld().getName();
        String toWorldName = event.getTo().getWorld().getName();
        boolean fromWasFaction = fromWorldName.equals(Settings.ISLAND_FACTION_WORLD.getString());
        boolean fromWasPlayer = fromWorldName.equals(Settings.ISLAND_PLAYER_WORLD.getString());
        boolean toWasFaction = toWorldName.equals(Settings.ISLAND_FACTION_WORLD.getString());
        boolean toWasPlayer = toWorldName.equals(Settings.ISLAND_PLAYER_WORLD.getString());

        if (fromWasFaction) {
            FactionAPI.getFaction(player).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (faction == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getFrom(), faction.getName())) return;

                FactionAPI.modifyDefenceOperation(faction.getName(), FactionAPI.DefenceOperation.DISABLE);
            });
        } else if (toWasFaction) {
            FactionAPI.getFaction(player).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (faction == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getTo(), faction.getName())) return;

                FactionAPI.modifyDefenceOperation(faction.getName(), FactionAPI.DefenceOperation.ENABLE);
            });
        } else if (fromWasPlayer) {
            IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (island == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getFrom(), player.getUniqueId().toString())) return;

                IslandAPI.disableDefencesOnExit(player);
            });
        } else if (toWasPlayer) {
            IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (island == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getTo(), player.getUniqueId().toString())) return;

                IslandAPI.enableDefencesOnEntry(player);
            });
        }
    }
}
