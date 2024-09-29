package net.skullian.torrent.skyfactions.defence;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.ConfigHandler;
import net.skullian.torrent.skyfactions.defence.struct.DefenceAttributeStruct;
import net.skullian.torrent.skyfactions.defence.struct.DefenceEffectStruct;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.util.SLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DefencesRegistry {

    private static final Map<String, DefenceStruct> defences = new HashMap<>();

    public static void register() {
        try {
            URI defencesFolder = SkyFactionsReborn.class.getResource("/defences").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(defencesFolder, new HashMap());
            for (Path yamlPath : fileSystem.getRootDirectories()) {
                Stream<Path> stream = Files.list(yamlPath.resolve("/defences"));
                stream.forEach(path -> {
                    String fullPathName = path.getFileName().toString();
                    String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                    SLogger.info("Registering Defence: \u001B[32m{}", defenceName);

                    ConfigHandler handler = new ConfigHandler(SkyFactionsReborn.getInstance(), "defences/" + defenceName);
                    handler.saveDefaultConfig();

                    DefenceStruct struct = createStruct(handler.getConfig(), defenceName);
                    defences.put(defenceName, struct);
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

    private static DefenceStruct createStruct(FileConfiguration config, String fileName) {
        String COLOR_NAME = config.getString("NAME");
        String TYPE = config.getString("TYPE");

        int BUY_COST = config.getInt("COST.BUY");
        int SELL_COST = config.getInt("COST.SELL");
        int AMMO_COST = config.getInt("AMMO_COST");

        int MAX_LEVEL = config.getInt("MAX_LEVEL");

        String PLACE_SOUND = config.getString("SOUNDS.PLACE");
        int PLACE_PITCH = config.getInt("SOUNDS.PLACE_PITCH");
        String BREAK_SOUND = config.getString("SOUNDS.BREAK");
        int BREAK_PITCH = config.getInt("SOUNDS.BREAK_PITCH");
        String ACTIVATE_SOUND = config.getString("SOUNDS.ACTIVATE");
        int ACTIVATE_PITCH = config.getInt("SOUNDS.ACTIVATE_PITCH");

        List<DefenceEffectStruct> EFFECTS = getEffects(config);

        List<String> MESSAGES = config.getStringList("MESSAGES");

        DefenceAttributeStruct ATTRIBUTES = getAttributes(config);

        Map<String, String> EXPERIENCE_DROPS = getXPFormulas(config);

        String PROJECTILE = config.getString("PROJECTILE");
        String PARTICLE = config.getString("PARTICLE");

        String BLOCK_MATERIAL = config.getString("BLOCK.MATERIAL");
        String BLOCK_SKULL = config.getString("BLOCK.SKULL");

        String ITEM_MATERIAL = config.getString("ITEM.MATERIAL");
        String ITEM_SKULL = config.getString("ITEM.SKULL");
        List<String> ITEM_LORE = config.getStringList("ITEM.LORE");
        List<String> UPGRADE_LORE = config.getStringList("UPGRADE.LORE");

        return new DefenceStruct(fileName, COLOR_NAME, TYPE, BUY_COST, SELL_COST, AMMO_COST, MAX_LEVEL,
                PLACE_SOUND, PLACE_PITCH, BREAK_SOUND, BREAK_PITCH, ACTIVATE_SOUND, ACTIVATE_PITCH, EFFECTS, MESSAGES,
                ATTRIBUTES, EXPERIENCE_DROPS, PROJECTILE, PARTICLE, BLOCK_MATERIAL, BLOCK_SKULL, ITEM_MATERIAL, ITEM_SKULL, ITEM_LORE, UPGRADE_LORE);
    }

    private static List<DefenceEffectStruct> getEffects(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("EFFECTS");
        List<DefenceEffectStruct> effectStructs = new ArrayList<>();

        if (section == null) return effectStructs;
        for (String name : section.getKeys(false)) {
            ConfigurationSection effect = section.getConfigurationSection(name);
            String effectType = effect.getString("EFFECT");
            int defenceLevel = effect.getInt("DEFENCE_LEVEL");
            int effectLevel = effect.getInt("EFFECT_LEVEL");
            int duration = effect.getInt("DURATION");

            effectStructs.add(new DefenceEffectStruct(effectType, defenceLevel, effectLevel, duration));
        }

        return effectStructs;
    }

    private static DefenceAttributeStruct getAttributes(FileConfiguration config) {
        String RANGE = config.getString("ATTRIBUTES.RANGE");
        String COOLDOWN = config.getString("ATTRIBUTES.COOLDOWN");
        String TARGET_MAX = config.getString("ATTRIBUTES.TARGET_MAX");
        String MAX_AMMO = config.getString("ATTRIBUTES.MAX_AMMO");
        String UPGRADE_COST = config.getString("UPGRADE_COST");
        String DAMAGE = config.getString("ATTRIBUTES.DAMAGE");
        String DISTANCE = config.getString("ATTRIBUTES.DISTANCE");
        String HEALING = config.getString("ATTRIBUTES.HEALING");

        return new DefenceAttributeStruct(RANGE, COOLDOWN, TARGET_MAX, MAX_AMMO, UPGRADE_COST, DAMAGE, DISTANCE, HEALING);
    }

    private static Map<String, String> getXPFormulas(FileConfiguration config) {
        ConfigurationSection drops = config.getConfigurationSection("EXPERIENCE_DROPS");
        // Mob Name / Expression
        Map<String, String> formulas = new HashMap<>();
        if (drops == null) return formulas;

        for (String mobType : drops.getKeys(false)) {
            String dropExpression = drops.getString(mobType);

            formulas.put(mobType, dropExpression);
        }

        return formulas;
    }
}
