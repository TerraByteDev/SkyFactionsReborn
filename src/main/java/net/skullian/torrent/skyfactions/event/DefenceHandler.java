package net.skullian.torrent.skyfactions.event;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.defence.Defence;
import net.skullian.torrent.skyfactions.defence.DefencesFactory;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SLogger;
import net.skullian.torrent.skyfactions.util.hologram.TextHologram;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefenceHandler implements Listener {
    public static final Map<String, ? extends Defence> loadedFactionDefences = new HashMap<>();
    public static final Map<UUID, ? extends Defence> loadedPlayerDefences = new HashMap<>();

    public static final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-name");

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String defenceName = container.get(defenceKey, PersistentDataType.STRING);

            DefenceStruct defence = DefencesFactory.defences.get(defenceName);
            if (defence != null) {
                Block placed = event.getBlockPlaced();
                Location belowLoc = placed.getLocation().clone();
                belowLoc.setY(belowLoc.getY() - 1);

                Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                if (!isAllowedBlock(belowBlock, defence)) {
                    event.setCancelled(true);
                    player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE().replace("%server_name%", Messages.SERVER_NAME.getString())));
                }
            } else {
                event.setCancelled(true);
                ErrorHandler.handleError(player, "place your defence", "UNKNOWN_DEFENCE", new IllegalArgumentException("Failed to find defence with the name of " + defenceName));
            }
        }
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-name");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
            Messages.DEFENCE_DESTROY_DENY.send(event.getPlayer());
        }
    }

    private boolean isAllowedBlock(Block block, DefenceStruct defenceStruct) {
        boolean isWhitelist = defenceStruct.isIS_WHITELIST();
        if (isWhitelist) return defenceStruct.getPLACEMENT_LIST().contains(block.getType());
        else return !defenceStruct.getPLACEMENT_LIST().contains(block.getType());
    }

    private static void addPlacedDefences(String factionName) {
        SkyFactionsReborn.db.getDefenceLocations(factionName).whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            for (Location location : locations) {
                Block block = location.getBlock();
                if (block == null) continue;

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-name");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    DefenceStruct defence = DefencesFactory.defences.get(name);

                    if (defence != null) {


                    } else SLogger.fatal("Failed to find defence with the name of " + name);
                }
            }
        });
    }

    private static void createHologram(Location location, DefenceStruct defence, String playerName) {
        String text = String.join("\n", defence.getHOLOGRAM_LIST());

        TextHologram hologram = new TextHologram(playerName + "_" + defence.getFILE_NAME() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ())
                .setText(TextUtility.color(text))
                .setSeeThroughBlocks(true)
                .setBillboard(Display.Billboard.VERTICAL)
                .setShadow(true)
                .setScale(1.0F, 1.0F, 1.0F);

        hologram.spawn(location.add(0, 1, 0));
        hologramsMap.put(hologram.getId(), hologram);
    }
}
