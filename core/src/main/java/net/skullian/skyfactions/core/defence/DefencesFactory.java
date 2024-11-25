package net.skullian.skyfactions.core.defence;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotDefenceAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.config.types.DefencesConfig;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.common.defence.struct.DefenceAttributeStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceEffectStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.util.SLogger;
import net.skullian.skyfactions.core.util.SoundUtil;

public class DefencesFactory {

    public static final Map<String, Map<String, DefenceStruct>> defences = new HashMap<>();
    public static final Map<String, String> defenceTypes = new HashMap<>();

    public static final List<String> cachedMaterials = new ArrayList<>();

    public static void registerDefaultDefences() {
        new File(SkyFactionsReborn.getInstance().getDataFolder() + "/language/en/defences").mkdirs();
        try {
            SLogger.info("Saving default defence configs.");
            URI defencesFolder = SkyFactionsReborn.class.getResource("/language/en/defences").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(defencesFolder, new HashMap());
            for (Path yamlPath : fileSystem.getRootDirectories()) {
                Stream<Path> stream = Files.list(yamlPath.resolve("/language/en/defences"));
                stream.forEach(path -> {
                    try {
                        String fullPathName = path.getFileName().toString();
                        String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                        YamlDocument.create(new File(SkyFactionsReborn.getInstance().getDataFolder() + "/language/en/defences/", defenceName + ".yml"), SkyFactionsReborn.getInstance().getResource(String.format("language/en/defences/%s.yml", defenceName)),
                                GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());
                    } catch (IOException err) {
                        err(err);
                    }
                });
            }
        } catch (URISyntaxException | IOException error) {
            err(error);
        }
    }

    private static void err(Exception error) {
        SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
        SLogger.fatal("There was an error loading defences.");
        SLogger.fatal("Please check the below error and proceed accordingly.");
        SLogger.fatal("Plugin will now disable.");
        SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
        error.printStackTrace();
        SkyFactionsReborn.getInstance().disable();
    }
    
    public static void register(File directory, String locale) {
        try {
            File dir = new File(directory + "/defences");
            dir.mkdirs();

            Map<String, DefenceStruct> dMap = new HashMap<>();
            for (File defenceFile : Objects.requireNonNull(dir.listFiles())) {
                String fullPathName = defenceFile.getName();
                String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                SLogger.info("Registering Defence: \u001B[32m{}", defenceName);

                YamlDocument config = YamlDocument.create(defenceFile, GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());

                DefenceStruct struct = createStruct(config, defenceName);
                dMap.put(struct.getIDENTIFIER(), struct);
            }

            defences.put(locale, dMap);
        } catch (IOException error) {
            err(error);
        }
    }

    public static Map<String, DefenceStruct> getDefaultStruct() {
        return defences.get(Settings.DEFAULT_LANGUAGE.getString());
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
        String DURABILITY_LINE = config.getString("HOLOGRAMS.DURABILITY_LINE");
        boolean APPEND_TO_TOP = config.getBoolean("HOLOGRAMS.STOCK_AT_TOP");
        boolean APPEND_DURABILITY_TO_TOP = config.getBoolean("HOLOGRAMS.DURABILITY_AT_TOP");

        DefenceEntityStruct ENTITY_DATA = getEntityConfiguration(config);

        cachedMaterials.add(ITEM_MATERIAL);
        return new DefenceStruct(fileName, COLOR_NAME, TYPE, IDENTIFIER, BUY_COST, SELL_COST, AMMO_COST, REPAIR_STEP, REPAIR_COST, MAX_LEVEL,
                PLACE_SOUND, PLACE_PITCH, BREAK_SOUND, BREAK_PITCH, ACTIVATE_SOUND, ACTIVATE_PITCH, EFFECTS, DEATH_MESSAGES, EFFECT_MESSAGES,
                ATTRIBUTES, EXPERIENCE_DROPS, PROJECTILE, BLOCK_MATERIAL, BLOCK_SKULL, ITEM_MATERIAL, ITEM_SKULL, ITEM_LORE, UPGRADE_LORE,
                PLACEMENT_BLOCKED_MESSAGE, IS_WHITELIST, BLOCKS_LIST, HOLOGRAM_STACK, OUT_OF_STOCK_LINE, DURABILITY_LINE, APPEND_TO_TOP, APPEND_DURABILITY_TO_TOP, ENTITY_DATA);
    }

    private static List<DefenceEffectStruct> getEffects(YamlDocument config) {
        Section section = config.getSection("EFFECTS");
        if (section == null) return Collections.emptyList();

        return section.getRoutesAsStrings(false).stream()
                .map(name -> {
                    Section effect = section.getSection(name);
                    return new DefenceEffectStruct(
                            effect.getString("EFFECT"),
                            effect.getInt("DEFENCE_LEVEL"),
                            effect.getInt("EFFECT_LEVEL"),
                            effect.getInt("DURATION")
                    );
                })
                .collect(Collectors.toList());
    }

    private static DefenceEntityStruct getEntityConfiguration(YamlDocument config) {
        boolean OVERRIDE = config.getBoolean("ENTITIES.OVERRIDE_GLOBAL_CONFIG");

        List<String> PASSIVE_LIST = new ArrayList<>();
        List<String> HOSTILE_LIST = new ArrayList<>();
        List<String> ENTITY_LIST = new ArrayList<>();

        if (!OVERRIDE) {
            if (config.getBoolean("ENTITIES.ALLOW_HOSTILE_TARGETING")) {
                HOSTILE_LIST.addAll(DefencesConfig.GLOBAL_HOSTILE_ENTITIES.getList());
            }
            if (config.getBoolean("ENTITIES.ALLOW_PASSIVE_TARGETING")) {
                PASSIVE_LIST.addAll(DefencesConfig.GLOBAL_PASSIVE_ENTITIES.getList());
            }
            ENTITY_LIST.addAll(DefencesConfig.GLOBAL_ENTITIES_ENTITY_LIST.getList());
        }

        if (config.getBoolean("ENTITIES.WHITELIST")) {
            ENTITY_LIST.addAll(config.getStringList("ENTITIES.ENTITY_LIST"));
        }

        return new DefenceEntityStruct(
                OVERRIDE,
                config.getBoolean("ENTITIES.ALLOW_HOSTILE_TARGETING"),
                config.getBoolean("ENTITIES.ALLOW_TOGGLE_HOSTILE_TARGETING"),
                config.getBoolean("ENTITIES.TARGET_HOSTILE_ON_DEFAULT"),
                config.getBoolean("ENTITIES.ALLOW_PASSIVE_TARGETING"),
                config.getBoolean("ENTITIES.ALLOW_TOGGLE_PASSIVE_TARGETING"),
                config.getBoolean("ENTITIES.TARGET_PASSIVE_ON_DEFAULT"),
                config.getBoolean("ENTITIES.ALLOW_ATTACK_PLAYERS"),
                config.getBoolean("ENTITIES.WHITELIST"),
                PASSIVE_LIST,
                HOSTILE_LIST,
                ENTITY_LIST
        );
    }

    private static DefenceAttributeStruct getAttributes(YamlDocument config) {
        return new DefenceAttributeStruct(
                config.getString("ATTRIBUTES.RANGE"),
                config.getString("ATTRIBUTES.COOLDOWN"),
                config.getString("ATTRIBUTES.TARGET_MAX"),
                config.getString("ATTRIBUTES.MAX_AMMO"),
                config.getString("UPGRADE_COST"),
                config.getString("ATTRIBUTES.DAMAGE"),
                config.getString("ATTRIBUTES.DISTANCE"),
                config.getString("ATTRIBUTES.HEALING"),
                config.getString("ATTRIBUTES.EXPLOSION_DAMAGE_PERCENT"),
                config.getInt("ATTRIBUTES.TARGET_HOSTILE_MOBS_LEVEL"),
                config.getInt("ATTRIBUTES.TARGET_PASSIVE_MOBS_LEVEL")
        );
    }

    private static Map<String, String> getXPFormulas(YamlDocument config) {
        Section drops = config.getSection("EXPERIENCE_DROPS");
        if (drops == null) return Collections.emptyMap();

        return drops.getRoutesAsStrings(false).stream()
                .collect(Collectors.toMap(mobType -> mobType, drops::getString));
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
        ItemStack stack = SpigotDefenceAPI.createDefenceStack(defence, player);
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        if (faction != null) {
            // assume faction type
            faction.subtractRunes(defence.getBUY_COST());
            player.getInventory().addItem(stack);
            SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "defence_name", defence.getNAME());

            faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_PURCHASE, "player_name", player.getName(), "defence_name", defence.getNAME(), locale, player);
        } else {

            SpigotRunesAPI.removeRunes(player.getUniqueId(), defence.getBUY_COST());
            player.getInventory().addItem(stack);
            SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "defence_name", defence.getNAME(), locale, player);
        }
    }

    private static List<String> getPlacementBlocks(YamlDocument config) {
        return config.getStringList("PLACEMENT.BLOCKS").stream()
                .flatMap(block -> {
                    boolean isWildCard = block.startsWith("*");
                    String cleaned = isWildCard ? block.substring(1) : block;
                    Material match = Material.matchMaterial(cleaned);

                    if (match != null) {
                        return Stream.of(match.name());
                    } else {
                        return Arrays.stream(Material.values())
                                .filter(material -> material.name().toLowerCase().contains(cleaned.toLowerCase()))
                                .map(Material::name);
                    }
                })
                .collect(Collectors.toList());
    }
}
