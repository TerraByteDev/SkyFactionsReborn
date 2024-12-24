package net.skullian.skyfactions.common.defence;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.struct.DefenceAttributeStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceEffectStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.util.SLogger;

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

@Getter
public abstract class DefenceFactory {

    private final Map<String, Map<String, DefenceStruct>> defences = new HashMap<>();
    private final Map<String, String> defenceTypes = new HashMap<>();

    private final List<String> cachedMaterials = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void registerDefaultDefences() {
        new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath() + "/language/en_US/defences").mkdirs();

        try {
            URI defencesFolder = Objects.requireNonNull(DefenceFactory.class.getResource("/language/en_US/defences")).toURI();
            try (FileSystem fileSystem = FileSystems.newFileSystem(defencesFolder, new HashMap<>())) {
                for (Path yamlPath : fileSystem.getRootDirectories()) {
                    try (Stream<Path> stream = Files.list(yamlPath.resolve("/language/en_US/defences"))) {
                        stream.forEach(path -> {
                            try {
                                String fullPathName = path.getFileName().toString();
                                String defenceName = fullPathName.substring(0, fullPathName.length() - 4);

                                YamlDocument.create(new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath() + "/language/en_US/defences/", defenceName + ".yml"), Objects.requireNonNull(DefenceFactory.class.getClassLoader().getResourceAsStream(String.format("language/en_US/defences/%s.yml", defenceName))),
                                        GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
                            } catch (IOException error) {
                                handleError(error);
                            }
                        });
                    } catch (IOException error) {
                        handleError(error);
                    }
                }
            }
        } catch (URISyntaxException | IOException error) {
            handleError(error);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void register(File directory, String locale) {
        try {
            File dir = new File(directory + "/defences");
            dir.mkdirs();

            Map<String, DefenceStruct> dMap = new HashMap<>();
            for (File defenceFile : Objects.requireNonNull(dir.listFiles())) {
                String fullPathName = defenceFile.getName();
                String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                SLogger.setup("Registering Defence: <#05eb2f>{}<#4294ed>", false, defenceName);

                YamlDocument config = YamlDocument.create(defenceFile, GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());

                DefenceStruct struct = createStruct(config, defenceName);
                dMap.put(struct.getIDENTIFIER(), struct);

                defences.put(locale, dMap);
            }
        } catch (IOException error) {
            handleError(error);
        }
    }

    public Map<String, DefenceStruct> getDefaultStruct() {
        return defences.get(Settings.DEFAULT_LANGUAGE.getString());
    }

    public DefenceStruct createStruct(YamlDocument config, String fileName) {
        String COLOR_NAME = config.getString("name");
        String TYPE = config.getString("type");
        String IDENTIFIER = config.getString("identifier");

        int BUY_COST = config.getInt("cost.buy");
        int SELL_COST = config.getInt("cost.sell");
        String AMMO_COST = config.getString("cost.ammo-cost");
        String REPAIR_STEP = config.getString("cost.repair-step");
        String REPAIR_COST = config.getString("cost.repair-cost");

        int MAX_LEVEL = config.getInt("max-level");

        String PLACE_SOUND = config.getString("sounds.place");
        int PLACE_PITCH = config.getInt("sounds.place-pitch");
        String BREAK_SOUND = config.getString("sounds.break");
        int BREAK_PITCH = config.getInt("sounds.break-pitch");
        String ACTIVATE_SOUND = config.getString("sounds.activate");
        int ACTIVATE_PITCH = config.getInt("sounds.activate-pitch");

        List<DefenceEffectStruct> EFFECTS = getEffects(config);

        List<String> DEATH_MESSAGES = config.getStringList("messages.death");
        List<String> EFFECT_MESSAGES = config.getStringList("messages.damage"); // can be healing, etc

        DefenceAttributeStruct ATTRIBUTES = getAttributes(config);

        Map<String, String> EXPERIENCE_DROPS = getXPFormulas(config);

        String PROJECTILE = config.getString("projectile");

        String BLOCK_MATERIAL = config.getString("block.material");
        String BLOCK_SKULL = config.getString("block.skull");

        String ITEM_MATERIAL = config.getString("item.material");
        String ITEM_SKULL = config.getString("item.skull");
        List<String> ITEM_LORE = config.getStringList("item.lore");
        List<String> UPGRADE_LORE = config.getStringList("upgrade.lore");

        String PLACEMENT_BLOCKED_MESSAGE = config.getString("placement.defence-incorrect-block");
        boolean IS_WHITELIST = config.getBoolean("placement.whitelist");
        List<String> BLOCKS_LIST = getPlacementBlocks(config);

        List<String> HOLOGRAM_STACK = config.getStringList("holograms.lines");
        String OUT_OF_STOCK_LINE = config.getString("holograms.out-of-stock-line");
        String DURABILITY_LINE = config.getString("holograms.durability-line");
        boolean APPEND_TO_TOP = config.getBoolean("holograms.stock-at-top");
        boolean APPEND_DURABILITY_TO_TOP = config.getBoolean("holograms.durability-at-top");

        DefenceEntityStruct ENTITY_DATA = getEntityConfiguration(config);

        cachedMaterials.add(ITEM_MATERIAL);
        return new DefenceStruct(fileName, COLOR_NAME, TYPE, IDENTIFIER, BUY_COST, SELL_COST, AMMO_COST, REPAIR_STEP, REPAIR_COST, MAX_LEVEL,
                PLACE_SOUND, PLACE_PITCH, BREAK_SOUND, BREAK_PITCH, ACTIVATE_SOUND, ACTIVATE_PITCH, EFFECTS, DEATH_MESSAGES, EFFECT_MESSAGES,
                ATTRIBUTES, EXPERIENCE_DROPS, PROJECTILE, BLOCK_MATERIAL, BLOCK_SKULL, ITEM_MATERIAL, ITEM_SKULL, ITEM_LORE, UPGRADE_LORE,
                PLACEMENT_BLOCKED_MESSAGE, IS_WHITELIST, BLOCKS_LIST, HOLOGRAM_STACK, OUT_OF_STOCK_LINE, DURABILITY_LINE, APPEND_TO_TOP, APPEND_DURABILITY_TO_TOP, ENTITY_DATA);
    }

    public List<DefenceEffectStruct> getEffects(YamlDocument config) {
        Section section = config.getSection("effects");
        if (section == null) return Collections.emptyList();

        return section.getRoutesAsStrings(false).stream()
                .map(name -> {
                    Section effect = section.getSection(name);
                    return new DefenceEffectStruct(
                            effect.getString("effect"),
                            effect.getInt("defence-level"),
                            effect.getInt("effect-level"),
                            effect.getInt("duration")
                    );
                })
                .collect(Collectors.toList());
    }

    public DefenceEntityStruct getEntityConfiguration(YamlDocument config) {
        boolean OVERRIDE = config.getBoolean("entities.override-global-config");

        List<String> PASSIVE_LIST = new ArrayList<>();
        List<String> HOSTILE_LIST = new ArrayList<>();
        List<String> ENTITY_LIST = new ArrayList<>();

        if (!OVERRIDE) {
            if (config.getBoolean("entities.allow-hostile-targeting")) {
                HOSTILE_LIST.addAll(DefencesConfig.GLOBAL_HOSTILE_ENTITIES.getList());
            }
            if (config.getBoolean("entities.allow-passive-targeting")) {
                PASSIVE_LIST.addAll(DefencesConfig.GLOBAL_PASSIVE_ENTITIES.getList());
            }
            ENTITY_LIST.addAll(DefencesConfig.GLOBAL_ENTITIES_ENTITY_LIST.getList());
        }

        if (config.getBoolean("entities.whitelist")) {
            ENTITY_LIST.addAll(config.getStringList("entities.entity-list"));
        }

        return new DefenceEntityStruct(
                OVERRIDE,
                config.getBoolean("entities.allow-hostile-targeting"),
                config.getBoolean("entities.allow-toggle_hostile_targeting"),
                config.getBoolean("entities.target-hostile-on-default"),
                config.getBoolean("entities.allow-passive-targeting"),
                config.getBoolean("entities.allow-toggle_passive_targeting"),
                config.getBoolean("entities.target-passive-on-default"),
                config.getBoolean("entities.allow-attack-players"),
                config.getBoolean("entities.whitelist"),
                PASSIVE_LIST,
                HOSTILE_LIST,
                ENTITY_LIST
        );
    }

    public DefenceAttributeStruct getAttributes(YamlDocument config) {
        return new DefenceAttributeStruct(
                config.getString("attributes.range"),
                config.getString("attributes.cooldown"),
                config.getString("attributes.target-max"),
                config.getString("attributes.max-ammo"),
                config.getString("upgrade-cost"),
                config.getString("attributes.damage"),
                config.getString("attributes.distance"),
                config.getString("attributes.healing"),
                config.getString("attributes.explosion-damage-percent"),
                config.getInt("attributes.target-hostile-mobs-level"),
                config.getInt("attributes.target-passive-mobs-level")
        );
    }

    public Map<String, String> getXPFormulas(YamlDocument config) {
        Section drops = config.getSection("experience-drops");
        if (drops == null) return Collections.emptyMap();

        return drops.getRoutesAsStrings(false).stream()
                .collect(Collectors.toMap(mobType -> mobType, drops::getString));
    }

    public abstract List<String> getPlacementBlocks(YamlDocument config);

    private void handleError(Exception error) {
        SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
        SLogger.fatal("There was an error loading defences.");
        SLogger.fatal("Please check the below error and proceed accordingly.");
        SLogger.fatal("Plugin will now disable.");
        SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
        SLogger.fatal(error);
        SkyApi.disablePlugin();
    }
}
