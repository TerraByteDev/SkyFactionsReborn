package net.skullian.skyfactions.paper.defence;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.skullian.skyfactions.common.defence.DefenceFactory;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefencesFactory extends DefenceFactory {

    @Override
    public List<String> getPlacementBlocks(YamlDocument config) {
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