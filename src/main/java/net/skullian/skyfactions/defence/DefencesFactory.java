package net.skullian.skyfactions.defence;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.lumine.mythic.bukkit.utils.events.extra.ArmorEquipEvent;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.config.ConfigHandler;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.struct.DefenceAttributeStruct;
import net.skullian.skyfactions.defence.struct.DefenceEffectStruct;
import net.skullian.skyfactions.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DefencesFactory {

    public static final Map<String, DefenceStruct> defences = new HashMap<>();
    public static final Map<String, String> defenceTypes = new HashMap<>();

    public static final List<String> cachedMaterials = new ArrayList<>();

    public static void registerDefaultDefences() {
        try {
            SLogger.info("Saving default defence configs.");
            URI defencesFolder = SkyFactionsReborn.class.getResource("/defences").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(defencesFolder, new HashMap());
            for (Path yamlPath : fileSystem.getRootDirectories()) {
                Stream<Path> stream = Files.list(yamlPath.resolve("/defences"));
                stream.forEach(path -> {
                    String fullPathName = path.getFileName().toString();
                    String defenceName = fullPathName.substring(0, fullPathName.length() - 4);

                    new ConfigHandler(SkyFactionsReborn.getInstance(), "defences/" + defenceName);
                });
            }
        } catch (URISyntaxException | IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading defences.");
            SLogger.fatal("Please check the below error and proceed accordingly.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            SkyFactionsReborn.getInstance().getServer().getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }
    }

    public static void register() {
        try {
            defences.clear();
            defenceTypes.put("ARROW", "net.skullian.skyfactions.defence.defences.ArrowDefence");

            File dir = new File(SkyFactionsReborn.getInstance().getDataFolder() + "/defences");
            for (File defenceFile : Objects.requireNonNull(dir.listFiles())) {
                String fullPathName = defenceFile.getName();
                String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                SLogger.info("Registering Defence: \u001B[32m{}", defenceName);

                YamlDocument config = YamlDocument.create(defenceFile, GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());

                DefenceStruct struct = createStruct(config, defenceName);
                defences.put(struct.getIDENTIFIER(), struct);
            }
        } catch (IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading defences.");
            SLogger.fatal("Please check the below error and proceed accordingly.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            SkyFactionsReborn.getInstance().getServer().getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }
    }

    private static DefenceStruct createStruct(YamlDocument config, String fileName) {
        String COLOR_NAME = config.getString("NAME");
        String TYPE = config.getString("TYPE");
        String IDENTIFIER = config.getString("IDENTIFIER");

        int BUY_COST = config.getInt("COST.BUY");
        int SELL_COST = config.getInt("COST.SELL");
        String AMMO_COST = config.getString("COST.AMMO_COST");
        String REPAIR_STEP = config.getString("COST.REPAIR_STEP");
        String REPAIR_COST = config.getString("COST.REPAIR_COST");

        int MAX_LEVEL = config.getInt("MAX_LEVEL");

        String PLACE_SOUND = config.getString("SOUNDS.PLACE");
        int PLACE_PITCH = config.getInt("SOUNDS.PLACE_PITCH");
        String BREAK_SOUND = config.getString("SOUNDS.BREAK");
        int BREAK_PITCH = config.getInt("SOUNDS.BREAK_PITCH");
        String ACTIVATE_SOUND = config.getString("SOUNDS.ACTIVATE");
        int ACTIVATE_PITCH = config.getInt("SOUNDS.ACTIVATE_PITCH");

        List<DefenceEffectStruct> EFFECTS = getEffects(config);

        List<String> DEATH_MESSAGES = config.getStringList("MESSAGES.DEATH");
        List<String> EFFECT_MESSAGES = config.getStringList("MESSAGES.DAMAGE"); // can be healing, etc

        DefenceAttributeStruct ATTRIBUTES = getAttributes(config);

        Map<String, String> EXPERIENCE_DROPS = getXPFormulas(config);

        String PROJECTILE = config.getString("PROJECTILE");

        String BLOCK_MATERIAL = config.getString("BLOCK.MATERIAL");
        String BLOCK_SKULL = config.getString("BLOCK.SKULL");

        String ITEM_MATERIAL = config.getString("ITEM.MATERIAL");
        String ITEM_SKULL = config.getString("ITEM.SKULL");
        List<String> ITEM_LORE = config.getStringList("ITEM.LORE");
        List<String> UPGRADE_LORE = config.getStringList("UPGRADE.LORE");

        String PLACEMENT_BLOCKED_MESSAGE = config.getString("PLACEMENT.DEFENCE_INCORRECT_BLOCK");
        boolean IS_WHITELIST = config.getBoolean("PLACEMENT.WHITELIST");
        List<String> BLOCKS_LIST = getPlacementBlocks(config);

        List<String> HOLOGRAM_STACK = config.getStringList("HOLOGRAMS.LINES");
        String OUT_OF_STOCK_LINE = config.getString("HOLOGRAMS.OUT_OF_STOCK_LINE");
        boolean APPEND_TO_TOP = config.getBoolean("HOLOGRAMS.STOCK_AT_TOP");

        DefenceEntityStruct ENTITY_DATA = getEntityConfiguration(config);

        cachedMaterials.add(ITEM_MATERIAL);
        return new DefenceStruct(fileName, COLOR_NAME, TYPE, IDENTIFIER, BUY_COST, SELL_COST, AMMO_COST, REPAIR_STEP, REPAIR_COST, MAX_LEVEL,
                PLACE_SOUND, PLACE_PITCH, BREAK_SOUND, BREAK_PITCH, ACTIVATE_SOUND, ACTIVATE_PITCH, EFFECTS, DEATH_MESSAGES, EFFECT_MESSAGES,
                ATTRIBUTES, EXPERIENCE_DROPS, PROJECTILE, BLOCK_MATERIAL, BLOCK_SKULL, ITEM_MATERIAL, ITEM_SKULL, ITEM_LORE, UPGRADE_LORE,
                PLACEMENT_BLOCKED_MESSAGE, IS_WHITELIST, BLOCKS_LIST, HOLOGRAM_STACK, OUT_OF_STOCK_LINE, APPEND_TO_TOP, ENTITY_DATA);
    }

    private static List<DefenceEffectStruct> getEffects(YamlDocument config) {
        Section section = config.getSection("EFFECTS");
        List<DefenceEffectStruct> effectStructs = new ArrayList<>();

        if (section == null) return effectStructs;
        for (String name : section.getRoutesAsStrings(false)) {
            Section effect = section.getSection(name);
            String effectType = effect.getString("EFFECT");
            int defenceLevel = effect.getInt("DEFENCE_LEVEL");
            int effectLevel = effect.getInt("EFFECT_LEVEL");
            int duration = effect.getInt("DURATION");

            effectStructs.add(new DefenceEffectStruct(effectType, defenceLevel, effectLevel, duration));
        }

        return effectStructs;
    }

    private static DefenceEntityStruct getEntityConfiguration(YamlDocument config) {
        boolean OVERRIDE = config.getBoolean("ENTITIES.OVERRIDE_GLOBAL_CONFIG");

        boolean ALLOW_HOSTILE = config.getBoolean("ENTITIES.ALLOW_HOSTILE_TARGETING");
        boolean ALLOW_TOGGLE_HOSTILE = config.getBoolean("ENTITIES.ALLOW_TOGGLE_HOSTILE_TARGETING");
        boolean TARGET_HOSTILE = config.getBoolean("ENTITIES.TARGET_HOSTILE_ON_DEFAULT");

        boolean ALLOW_PASSIVE = config.getBoolean("ENTITIES.ALLOW_PASSIVE_TARGETING");
        boolean ALLOW_TOGGLE_PASSIVE = config.getBoolean("ENTITIES.ALLOW_TOGGLE_PASSIVE_TARGETING");
        boolean TARGET_PASSIVE = config.getBoolean("ENTITIES.TARGET_PASSIVE_ON_DEFAULT");

        boolean ALLOW_ATTACK_PLAYERS = config.getBoolean("ENTITIES.ALLOW_ATTACK_PLAYERS");

        boolean IS_WHITELIST = config.getBoolean("ENTITIES.WHITELIST");

        List<String> PASSIVE_LIST = new ArrayList<>();
        List<String> HOSTILE_LIST = new ArrayList<>();
        List<String> ENTITY_LIST = new ArrayList<>();
        if (!OVERRIDE && ALLOW_HOSTILE)
            HOSTILE_LIST.addAll(DefencesConfig.GLOBAL_HOSTILE_ENTITIES.getList());
        if (!OVERRIDE && ALLOW_PASSIVE)
            PASSIVE_LIST.addAll(DefencesConfig.GLOBAL_PASSIVE_ENTITIES.getList());

        if (!OVERRIDE) ENTITY_LIST.addAll(DefencesConfig.GLOBAL_ENTITIES_ENTITY_LIST.getList());

        if (IS_WHITELIST) ENTITY_LIST.addAll(config.getStringList("ENTITIES.ENTITY_LIST"));

        return new DefenceEntityStruct(OVERRIDE, ALLOW_HOSTILE, ALLOW_TOGGLE_HOSTILE, TARGET_HOSTILE, ALLOW_PASSIVE, ALLOW_TOGGLE_PASSIVE, TARGET_PASSIVE,
                ALLOW_ATTACK_PLAYERS, IS_WHITELIST, PASSIVE_LIST, HOSTILE_LIST, ENTITY_LIST);
    }

    private static DefenceAttributeStruct getAttributes(YamlDocument config) {
        String RANGE = config.getString("ATTRIBUTES.RANGE");
        String COOLDOWN = config.getString("ATTRIBUTES.COOLDOWN");
        String TARGET_MAX = config.getString("ATTRIBUTES.TARGET_MAX");
        String MAX_AMMO = config.getString("ATTRIBUTES.MAX_AMMO");
        String UPGRADE_COST = config.getString("UPGRADE_COST");
        String DAMAGE = config.getString("ATTRIBUTES.DAMAGE");
        String DISTANCE = config.getString("ATTRIBUTES.DISTANCE");
        String HEALING = config.getString("ATTRIBUTES.HEALING");

        String EXPLOSION_DAMAGE_PERCENT = config.getString("ATTRIBUTES.EXPLOSION_DAMAGE_PERCENT");
        String MINE_DAMAGE_PERCENT = config.getString("ATTRIBUTES.MINE_DAMAGE_PERCENT");

        int HOSTILE_MOBS_TARGET_LEVEL = config.getInt("ATTRIBUTES.TARGET_HOSTILE_MOBS_LEVEL");
        int PASSIVE_MOBS_TARGET_LEVEL = config.getInt("ATTRIBUTES.TARGET_PASSIVE_MOBS_LEVEL");

        return new DefenceAttributeStruct(RANGE, COOLDOWN, TARGET_MAX, MAX_AMMO, UPGRADE_COST, DAMAGE, DISTANCE, HEALING, EXPLOSION_DAMAGE_PERCENT, MINE_DAMAGE_PERCENT, HOSTILE_MOBS_TARGET_LEVEL, PASSIVE_MOBS_TARGET_LEVEL);
    }

    private static Map<String, String> getXPFormulas(YamlDocument config) {
        Section drops = config.getSection("EXPERIENCE_DROPS");
        // Mob Name / Expression
        Map<String, String> formulas = new HashMap<>();
        if (drops == null) return formulas;

        for (String mobType : drops.getRoutesAsStrings(false)) {
            String dropExpression = drops.getString(mobType);

            formulas.put(mobType, dropExpression);
        }

        return formulas;
    }

    public static String solveFormula(String formula, int level) {
        if (formula == null) return "N/A";
        try {
            Expression expression = new ExpressionBuilder(formula)
                    .variable("level")
                    .build()
                    .setVariable("level", level);

            if (!expression.validate().isValid()) return "N/A";

            return String.valueOf(Math.round(expression.evaluate()));
        } catch (Exception e) {
            SLogger.fatal("Encountered an error when trying to evaluate defence formulas: {}", e.getMessage());
            e.printStackTrace();
        }

        return "N/A";
    }

    public static void addDefence(Player player, DefenceStruct defence, Faction faction) {
        ItemStack stack = DefenceAPI.createDefenceStack(defence);

        if (faction != null) {
            // assume faction type
            faction.subtractRunes(defence.getBUY_COST());
            player.getInventory().addItem(stack);
            SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, "%defence_name%", TextUtility.color(defence.getNAME()));

            faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_PURCHASE, "%player_name%", player.getName(), "%defence_name%", TextUtility.color(defence.getNAME()));
        } else {

            RunesAPI.removeRunes(player.getUniqueId(), defence.getBUY_COST());
            player.getInventory().addItem(stack);
            SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, "%defence_name%", TextUtility.color(defence.getNAME()));
        }
    }

    private static List<String> getPlacementBlocks(YamlDocument config) {
        List<String> matchingMaterials = new ArrayList<>();
        List<String> list = config.getStringList("PLACEMENT.BLOCKS");

        for (String block : list) {
            boolean isWildCard = block.startsWith("*");
            String cleaned = isWildCard ? block.substring(1) : block;

            Material match = Material.matchMaterial(cleaned);
            if (match != null) {
                matchingMaterials.add(match.name());
            } else {
                for (Material material : Material.values()) {
                    if (material.name().toLowerCase().contains(cleaned.toLowerCase())) {
                        matchingMaterials.add(material.name());
                    }
                }
            }
        }

        return matchingMaterials;
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        if (DefenceAPI.isDefence(event.getNewArmorPiece())) {
            event.setCancelled(true);
        }
    }
}
