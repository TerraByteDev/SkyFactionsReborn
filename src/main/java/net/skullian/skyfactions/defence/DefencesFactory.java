package net.skullian.skyfactions.defence;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.api.SkullAPI;
import net.skullian.skyfactions.config.ConfigHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.struct.DefenceAttributeStruct;
import net.skullian.skyfactions.defence.struct.DefenceEffectStruct;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
    public static final Map<String, Class<? extends Defence>> defenceTypes = new HashMap<>();

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

                    ConfigHandler handler = new ConfigHandler(SkyFactionsReborn.getInstance(), "defences/" + defenceName);
                    handler.saveDefaultConfig();
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
            File dir = new File(SkyFactionsReborn.getInstance().getDataFolder() + "/defences");
            for (File defenceFile : Objects.requireNonNull(dir.listFiles())) {
                String fullPathName = defenceFile.getName();
                String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                SLogger.info("Registering Defence: \u001B[32m{}", defenceName);

                YamlConfiguration config = new YamlConfiguration();
                config.load(defenceFile);
                DefenceStruct struct = createStruct(config, defenceName);
                defences.put(struct.getIDENTIFIER(), struct);
            }
        } catch (InvalidConfigurationException | IOException error) {
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
        String IDENTIFIER = config.getString("IDENTIFIER");

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

        String PLACEMENT_BLOCKED_MESSAGE = config.getString("PLACEMENT.PLACEMENT_BLOCKED_MESSAGE");
        boolean IS_WHITELIST = config.getBoolean("PLACEMENT.WHITELIST");
        List<Material> BLOCKS_LIST = getPlacementBlocks(config, fileName);

        List<String> HOLOGRAM_STACK = config.getStringList("HOLOGRAMS.LINES");
        String OUT_OF_STOCK_LINE = config.getString("HOLOGRAMS.OUT_OF_STOCK_LINE");
        boolean APPEND_TO_TOP = config.getBoolean("HOLOGRAMS.STOCK_AT_TOP");

        return new DefenceStruct(fileName, COLOR_NAME, TYPE, IDENTIFIER, BUY_COST, SELL_COST, AMMO_COST, MAX_LEVEL,
                PLACE_SOUND, PLACE_PITCH, BREAK_SOUND, BREAK_PITCH, ACTIVATE_SOUND, ACTIVATE_PITCH, EFFECTS, MESSAGES,
                ATTRIBUTES, EXPERIENCE_DROPS, PROJECTILE, PARTICLE, BLOCK_MATERIAL, BLOCK_SKULL, ITEM_MATERIAL, ITEM_SKULL, ITEM_LORE, UPGRADE_LORE,
                PLACEMENT_BLOCKED_MESSAGE, IS_WHITELIST, BLOCKS_LIST, HOLOGRAM_STACK, OUT_OF_STOCK_LINE, APPEND_TO_TOP);
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
        ItemStack stack = SkullAPI.convertToSkull(new ItemStack(Material.getMaterial(defence.getITEM_MATERIAL())), defence.getITEM_SKULL());
        NamespacedKey nameKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtility.color(defence.getNAME()));
        meta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, defence.getFILE_NAME());

        meta.setLore(getFormattedLore(defence, defence.getITEM_LORE()));
        stack.setItemMeta(meta);


        if (faction != null) {
            // assume faction type
            faction.subtractRunes(defence.getBUY_COST()).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "purchase a defence", "SQL_GEMS_MODIFY", ex);
                    return;
                }

                player.getInventory().addItem(stack);
                SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
                Messages.DEFENCE_PURCHASE_SUCCESS.send(player, "%defence_name%", TextUtility.color(defence.getNAME()));

                faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_PURCHASE, "%player_name%", player.getName(), "%defence_name%", TextUtility.color(defence.getNAME()));
            });
        } else {
            RunesAPI.removeRunes(player.getUniqueId(), defence.getBUY_COST()).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "purchase a defence", "SQL_GEMS_MODIFY", ex);
                    return;
                }

                player.getInventory().addItem(stack);
                SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
                Messages.DEFENCE_PURCHASE_SUCCESS.send(player, "%defence_name%", TextUtility.color(defence.getNAME()));
            });
        }
    }

    private static List<String> getFormattedLore(DefenceStruct struct, List<String> lore) {
        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefencesFactory.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefencesFactory.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefencesFactory.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        List<String> newLore = new ArrayList<>();

        for (String str : lore) {
            newLore.add(TextUtility.color(str
                    .replace("%max_level%", maxLevel)
                    .replace("%range%", range)
                    .replace("%ammo%", ammo)
                    .replace("%target_max%", targetMax)
                    .replace("%damage%", damage)
                    .replace("%cooldown%", cooldown)
                    .replace("%healing%", healing)
                    .replace("%distance%", distance)
                    .replace("%cost%", String.valueOf(struct.getBUY_COST()))));
        }

        return newLore;
    }

    private static List<Material> getPlacementBlocks(FileConfiguration config, String fName) {
        List<Material> matchingMaterials = new ArrayList<>();
        List<String> list = config.getStringList("PLACEMENT.BLOCKS");

        for (String block : list) {
            boolean isWildCard = block.startsWith("*");
            String cleaned = isWildCard ? block.substring(1) : block;

            Material match = Material.matchMaterial(cleaned);
            if (match != null) {
                matchingMaterials.add(match);
            } else {
                for (Material material : Material.values()) {
                    if (material.name().toLowerCase().contains(cleaned.toLowerCase())) {
                        match = material;
                    }
                }

                if (match == null) {
                    SLogger.warn("Could not find any matching materials by the name of: {} in Defence {}", cleaned, fName);
                } else matchingMaterials.add(match);
            }
        }

        return matchingMaterials;
    }
}
