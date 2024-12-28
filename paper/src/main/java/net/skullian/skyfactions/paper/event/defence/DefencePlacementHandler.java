package net.skullian.skyfactions.paper.event.defence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.common.SharedConstants;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.tables.DefenceLocations;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.PaperSharedConstants;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DefencePlacementHandler implements Listener {
    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(player.getUniqueId());
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = PaperSharedConstants.DEFENCE_IDENTIFIER_KEY;

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            Block placed = event.getBlockPlaced();
            returnOwnerDependingOnLocation(placed.getLocation(), player).whenComplete((owner, ex) -> {
                boolean isFaction = SkyApi.getInstance().getDefenceAPI().isFaction(owner);
                if (ex != null) {
                    ErrorUtil.handleError(player, "place your defence", "SQL_FACTION_GET", ex);
                    event.setCancelled(true);
                    return;
                } else if (owner == null) {
                    event.setCancelled(true);
                    return;
                }

                boolean shouldContinue = false;
                if (isFaction) {
                    Faction fetchedFaction = SkyApi.getInstance().getFactionAPI().getCachedFaction(owner);
                    if (fetchedFaction != null) {
                        if (!SkyApi.getInstance().getDefenceAPI().hasPermissions(DefencesConfig.PERMISSION_PLACE_DEFENCE.getList(), user, fetchedFaction)) {
                            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(player, locale);
                        } else {
                            List<Defence> loadedDefences = SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().get(owner);
                            if (loadedDefences != null && loadedDefences.size() >= DefencesConfig.MAX_FACTION_DEFENCES.getInt()) {
                                event.setCancelled(true);

                                Messages.TOO_MANY_DEFENCES_MESSAGE.send(player, locale, "defence_max", DefencesConfig.MAX_FACTION_DEFENCES.getInt());
                                SkyApi.getInstance().getSoundAPI().playSound(user, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
                            } else shouldContinue = true;
                        }
                    }
                } else {
                    List<Defence> loadedDefences = SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().get(UUID.fromString(owner));
                    if (loadedDefences != null && loadedDefences.size() >= DefencesConfig.MAX_PLAYER_DEFENCES.getInt()) {
                        event.setCancelled(true);

                        Messages.TOO_MANY_DEFENCES_MESSAGE.send(player, locale, "defence_max", DefencesConfig.MAX_PLAYER_DEFENCES.getInt());
                        SkyApi.getInstance().getSoundAPI().playSound(user, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
                    } else shouldContinue = true;
                }

                if (!shouldContinue) return;
                String defenceIdentifier = container.get(defenceKey, PersistentDataType.STRING);
                if (defenceIdentifier == null) return;

                DefenceStruct defence = SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(locale, SkyApi.getInstance().getDefenceFactory().getDefaultStruct()).get(defenceIdentifier);
                if (defence != null) {
                    Location belowLoc = placed.getLocation().clone();
                    belowLoc.setY(belowLoc.getY() - 1);

                    Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                    if (!isAllowedBlock(belowBlock, defence)) {
                        event.setCancelled(true);
                        player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE(), locale, user, "server_name", Messages.SERVER_NAME.getString(locale)));
                        return;
                    }

                    try {
                        DefenceData data = new DefenceData(1, defenceIdentifier, 0, placed.getLocation().getWorld().getName(), placed.getLocation().getBlockX(), placed.getLocation().getBlockY(), placed.getLocation().getBlockZ(), owner, isFaction, locale, 100, defence.getENTITY_CONFIG().isTARGET_HOSTILE_DEFAULT(), defence.getENTITY_CONFIG().isTARGET_PASSIVE_DEFAULT());
                        ObjectMapper mapper = new ObjectMapper();

                        PersistentDataContainer blockContainer = new CustomBlockData(placed, SkyFactionsReborn.getInstance());

                        blockContainer.set(defenceKey, PersistentDataType.STRING, defenceIdentifier);
                        blockContainer.set(PaperSharedConstants.DEFENCE_DATA_KEY, PersistentDataType.STRING, mapper.writeValueAsString(data));

                        createDefence(data, defence, owner, isFaction, Optional.of(player), Optional.of(event), true, true);
                    } catch (Exception error) {
                        event.setCancelled(true);
                        ErrorUtil.handleError(event.getPlayer(), "place your defence", "DEFENCE_PROCESSING_EXCEPTION", error);
                    }
                }else {
                    event.setCancelled(true);
                    ErrorUtil.handleError(player, "place your defence", "DEFENCE_PROCESSING_EXCEPTION", new IllegalArgumentException("Failed to find defence with the name of " + defenceIdentifier));
                }
            });
        }
    }

    private static Defence createDefence(DefenceData data, DefenceStruct defenceStruct, String owner, boolean isFaction, Optional<Player> player, Optional<BlockPlaceEvent> event, boolean isPlace, boolean shouldLoad) {
        Defence instance = null;
        try {
            Class<?> clazz = Class.forName(SkyApi.getInstance().getDefenceFactory().getDefenceTypes().get(defenceStruct.getTYPE()));
            Constructor<?> constr = clazz.getConstructor(DefenceData.class, DefenceStruct.class);

            instance = (Defence) constr.newInstance(data, defenceStruct);
            if (shouldLoad) instance.onLoad(owner);

            if (isFaction && isPlace) SkyApi.getInstance().getCacheService().getEntry(Objects.requireNonNull(SkyApi.getInstance().getFactionAPI().getCachedFaction(owner))).addDefence(instance.getDefenceLocation());
                else if (!isFaction && isPlace) SkyApi.getInstance().getCacheService().getEntry(UUID.fromString(owner)).addDefence(instance.getDefenceLocation());

            addIntoMap(owner, isFaction, instance);

            return instance;
        } catch (Exception error) {
            if (instance != null && isFaction && isPlace) SkyApi.getInstance().getCacheService().getEntry(Objects.requireNonNull(SkyApi.getInstance().getFactionAPI().getCachedFaction(owner))).removeDefence(instance.getDefenceLocation());
                else if (instance != null && !isFaction && isPlace) SkyApi.getInstance().getCacheService().getEntry(UUID.fromString(owner)).removeDefence(instance.getDefenceLocation());

            event.ifPresent(blockPlaceEvent -> blockPlaceEvent.setCancelled(true));
            player.ifPresent(value -> ErrorUtil.handleError(value, "place your defence", "DEFENCE_PROCESSING_EXCEPTION", error));
        }

        return null;
    }

    private static void addIntoMap(String owner, boolean isFaction, Defence defence) {
        if (isFaction) {
            Map<String, List<Defence>> loadedFactionDefences = SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences();
            if (!loadedFactionDefences.containsKey(owner)) {
                loadedFactionDefences.put(owner, List.of(defence));
            } else {
                List<Defence> defences = new ArrayList<>(loadedFactionDefences.computeIfAbsent(owner, k -> new ArrayList<>()));
                defences.add(defence);
                loadedFactionDefences.replace(owner, defences);
            }
        } else {
            Map<UUID, List<Defence>> loadedPlayerDefences = SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences();

            UUID playeruuid = UUID.fromString(owner);
            if (!loadedPlayerDefences.containsKey(playeruuid)) {
                loadedPlayerDefences.put(playeruuid, List.of(defence));
            } else {
                List<Defence> defences = new ArrayList<>(loadedPlayerDefences.computeIfAbsent(playeruuid, k -> new ArrayList<>()));
                defences.add(defence);
                loadedPlayerDefences.put(playeruuid, defences);
            }
        }
    }

    private CompletableFuture<String> returnOwnerDependingOnLocation(Location location, Player player) {
        SkyLocation skyLocation = SpigotAdapter.adapt(location);

        if (location.getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString()) && SkyApi.getInstance().getRegionAPI().isLocationInRegion(skyLocation, "sfr_player_" + player.getUniqueId())) {
            return CompletableFuture.completedFuture(player.getUniqueId().toString());
        } else if (location.getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
            return SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).thenApply((faction) -> {
                if (faction != null && SkyApi.getInstance().getRegionAPI().isLocationInRegion(skyLocation, "sfr_faction_" + faction.getName())) {
                    return faction.getName();
                } else return null;
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    private boolean isAllowedBlock(Block block, DefenceStruct defenceStruct) {
        boolean isWhitelist = defenceStruct.isIS_WHITELIST();

        if (isWhitelist) return defenceStruct.getPLACEMENT_LIST().contains(block.getType().name());
        else return !defenceStruct.getPLACEMENT_LIST().contains(block.getType().name());
    }

    public static void addPlacedDefences(String factionName) {
        if (SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().get(factionName) != null) return;
        SkyApi.getInstance().getDatabaseManager().getDefencesManager().getDefenceLocations(DefenceLocations.DEFENCE_LOCATIONS.FACTIONNAME.eq(factionName), "faction").whenComplete((locations, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to fetch defence locations for faction {} - {}", factionName, ex);
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (SkyLocation location : locations) {
                Block block = SpigotAdapter.adapt(location).getBlock();

                NamespacedKey defenceKey = PaperSharedConstants.DEFENCE_IDENTIFIER_KEY;
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    try {
                        String name = container.get(defenceKey, PersistentDataType.STRING);
                        String data = container.get(PaperSharedConstants.DEFENCE_DATA_KEY, PersistentDataType.STRING);
                        ObjectMapper mapper = new ObjectMapper();
                        DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                        DefenceStruct defence = SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(defenceData.getLOCALE(), SkyApi.getInstance().getDefenceFactory().getDefaultStruct()).get(name);
                        if (defence != null) {

                            Defence instance = createDefence(defenceData, defence, factionName, true, Optional.empty(), Optional.empty(), false, false);

                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);

                    } catch (IOException error) {
                        SLogger.fatal("Failed to write defence data to JSON string - {}", error);
                    }
                }
            }

            SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().put(factionName, defences);
        });
    }

    public static void addPlacedDefences(Player player) {
        if (SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().get(player.getUniqueId()) != null) return;
        SkyApi.getInstance().getDatabaseManager().getDefencesManager().getDefenceLocations(DefenceLocations.DEFENCE_LOCATIONS.UUID.eq(player.getUniqueId().toString()), "player").whenComplete((locations, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to fetch defence locations for player {} - {}", player.getUniqueId(), ex);
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (SkyLocation location : locations) {
                Block block = SpigotAdapter.adapt(location).getBlock();

                NamespacedKey defenceKey = PaperSharedConstants.DEFENCE_IDENTIFIER_KEY;
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(PaperSharedConstants.DEFENCE_DATA_KEY, PersistentDataType.STRING);
                    DefenceStruct defence = SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), SkyApi.getInstance().getDefenceFactory().getDefaultStruct()).get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Defence instance = createDefence(defenceData, defence, player.getUniqueId().toString(), false, Optional.of(player), Optional.empty(), false, Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean());
                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (IOException error) {
                        SLogger.fatal("Failed to write defence data to JSON string - {}", error);
                    }
                }
            }

            SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().put(player.getUniqueId(), defences);
        });
    }
}
