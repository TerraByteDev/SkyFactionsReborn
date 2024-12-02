package net.skullian.skyfactions.paper.event.defence;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.paper.api.SpigotRegionAPI;
import net.skullian.skyfactions.common.database.tables.DefenceLocations;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.SpigotDefenceAPI;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.config.types.DefencesConfig;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.paper.defence.SpigotDefencesFactory;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import net.skullian.skyfactions.paper.util.SLogger;
import net.skullian.skyfactions.paper.util.SoundUtil;
import net.skullian.skyfactions.paper.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.util.text.TextUtility;

public class DefencePlacementHandler implements Listener {
    public static Map<String, List<Defence>> loadedFactionDefences = new HashMap<>();
    public static Map<UUID, List<Defence>> loadedPlayerDefences = new HashMap<>();

    public static Map<String, DefenceTextHologram> hologramsMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            Block placed = event.getBlockPlaced();
            returnOwnerDependingOnLocation(placed.getLocation(), player).whenComplete((owner, ex) -> {
                boolean isFaction = SpigotDefenceAPI.isFaction(owner);
                if (ex != null) {
                    ErrorUtil.handleError(player, "place your defence", "SQL_FACTION_GET", ex);
                    event.setCancelled(true);
                    return;
                } else if (owner == null) {
                    event.setCancelled(true);
                    return;
                }

                AtomicBoolean shouldContinue = new AtomicBoolean(false);
                if (isFaction) {
                    SpigotFactionAPI.getFaction(owner).whenComplete((faction, throwable) -> {
                        if (throwable != null) {
                            ErrorUtil.handleError(player, "place your defence", "SQL_FACTION_GET", throwable);
                            event.setCancelled(true);
                            return;
                        } else if (!SpigotDefenceAPI.hasPermissions(DefencesConfig.PERMISSION_PLACE_DEFENCE.getList(), player, faction)) {
                            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(player, locale);
                            return;
                        }

                        List<Defence> loadedDefences = loadedFactionDefences.get(owner);
                        if (loadedDefences != null && loadedDefences.size() >= DefencesConfig.MAX_FACTION_DEFENCES.getInt()) {
                            event.setCancelled(true);

                            Messages.TOO_MANY_DEFENCES_MESSAGE.send(player, locale, "defence_max", DefencesConfig.MAX_FACTION_DEFENCES.getInt());
                            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
                        }

                        shouldContinue.set(true);
                    });
                } else {
                    List<Defence> loadedDefences = loadedPlayerDefences.get(UUID.fromString(owner));
                    if (loadedDefences != null && loadedDefences.size() >= DefencesConfig.MAX_PLAYER_DEFENCES.getInt()) {
                        event.setCancelled(true);

                        Messages.TOO_MANY_DEFENCES_MESSAGE.send(player, locale, "defence_max", DefencesConfig.MAX_PLAYER_DEFENCES.getInt());
                        SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
                        return;
                    }

                    shouldContinue.set(true);
                }

                if (!shouldContinue.get()) return;
                String defenceIdentifier = container.get(defenceKey, PersistentDataType.STRING);

                DefenceStruct defence = SpigotDefencesFactory.defences.getOrDefault(locale, SpigotDefencesFactory.getDefaultStruct()).get(defenceIdentifier);
                if (defence != null) {
                    Location belowLoc = placed.getLocation().clone();
                    belowLoc.setY(belowLoc.getY() - 1);

                    Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                    if (!isAllowedBlock(belowBlock, defence)) {
                        event.setCancelled(true);
                        player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE(), locale, player, "server_name", Messages.SERVER_NAME.getString(locale)));
                        return;
                    }

                    try {
                        DefenceData data = new DefenceData(1, defenceIdentifier, 0, placed.getLocation().getWorld().getName(), placed.getLocation().getBlockX(), placed.getLocation().getBlockY(), placed.getLocation().getBlockZ(), owner, isFaction, locale, 100, defence.getENTITY_CONFIG().isTARGET_HOSTILE_DEFAULT(), defence.getENTITY_CONFIG().isTARGET_PASSIVE_DEFAULT());
                        ObjectMapper mapper = new ObjectMapper();

                        PersistentDataContainer blockContainer = new CustomBlockData(placed, SkyFactionsReborn.getInstance());
                        NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

                        blockContainer.set(defenceKey, PersistentDataType.STRING, defenceIdentifier);
                        blockContainer.set(dataKey, PersistentDataType.STRING, mapper.writeValueAsString(data));

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
            Class<?> clazz = Class.forName(SpigotDefencesFactory.defenceTypes.get(defenceStruct.getTYPE()));
            Constructor<?> constr = clazz.getConstructor(DefenceData.class, DefenceStruct.class);

            instance = (Defence) constr.newInstance(data, defenceStruct);
            if (shouldLoad) instance.onLoad(owner);

            if (isFaction && isPlace) SkyFactionsReborn.getCacheService().getEntry(SpigotFactionAPI.getCachedFaction(owner)).addDefence(instance.getDefenceLocation());
                else if (!isFaction && isPlace) SkyFactionsReborn.getCacheService().getEntry(UUID.fromString(owner)).addDefence(instance.getDefenceLocation());

            addIntoMap(owner, isFaction, instance);

            return instance;
        } catch (Exception error) {
            if (instance != null && isFaction && isPlace) SkyFactionsReborn.getCacheService().getEntry(SpigotFactionAPI.getCachedFaction(owner)).removeDefence(instance.getDefenceLocation());
                else if (instance != null && !isFaction && isPlace) SkyFactionsReborn.getCacheService().getEntry(UUID.fromString(owner)).removeDefence(instance.getDefenceLocation());

            event.ifPresent(blockPlaceEvent -> blockPlaceEvent.setCancelled(true));
            player.ifPresent(value -> ErrorUtil.handleError(value, "place your defence", "DEFENCE_PROCESSING_EXCEPTION", error));
        }

        return null;
    }

    private static void addIntoMap(String owner, boolean isFaction, Defence defence) {
        if (isFaction) {
            if (!loadedFactionDefences.containsKey(owner)) {
                loadedFactionDefences.put(owner, List.of(defence));
            } else {
                List<Defence> defences = new ArrayList<>(loadedFactionDefences.computeIfAbsent(owner, k -> new ArrayList<>()));
                defences.add(defence);
                loadedFactionDefences.replace(owner, defences);
            }
        } else {
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
        if (location.getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString()) && SpigotRegionAPI.isLocationInRegion(location, "sfr_player_" + player.getUniqueId())) {
            return CompletableFuture.completedFuture(player.getUniqueId().toString());
        } else if (location.getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
            return SpigotFactionAPI.getFaction(player.getUniqueId()).thenApply((faction) -> {
                if (faction != null && SpigotRegionAPI.isLocationInRegion(location, "sfr_faction_" + faction.getName())) {
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
        if (loadedFactionDefences.get(factionName) != null) return;
        SkyFactionsReborn.getDatabaseManager().getDefencesManager().getDefenceLocations(DefenceLocations.DEFENCE_LOCATIONS.FACTIONNAME.eq(factionName), "faction").whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                try {

                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    ObjectMapper mapper = new ObjectMapper();
                    DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                    DefenceStruct defence = SpigotDefencesFactory.defences.getOrDefault(defenceData.getLOCALE(), SpigotDefencesFactory.getDefaultStruct()).get(name);
                        if (defence != null) {

                            Defence instance = createDefence(defenceData, defence, factionName, true, Optional.empty(), Optional.empty(), false, false);

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
        SkyFactionsReborn.getDatabaseManager().getDefencesManager().getDefenceLocations(DefenceLocations.DEFENCE_LOCATIONS.UUID.eq(player.getUniqueId().toString()), "player").whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    DefenceStruct defence = SpigotDefencesFactory.defences.getOrDefault(SpigotPlayerAPI.getLocale(player.getUniqueId()), SpigotDefencesFactory.getDefaultStruct()).get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Defence instance = createDefence(defenceData, defence, player.getUniqueId().toString(), false, Optional.of(player), Optional.empty(), false, Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean());
                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }

            loadedPlayerDefences.put(player.getUniqueId(), defences);
        });
    }

    public static void refresh() {
        SLogger.info("Refreshing existing loaded defences...");

        loadedPlayerDefences.values().stream()
                .flatMap(List::stream)
                .forEach(Defence::refresh);

        loadedFactionDefences.values().stream()
                .flatMap(List::stream)
                .forEach(Defence::refresh);
    }
}
