package net.skullian.torrent.skyfactions.island;

import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@Getter
public class SkyIsland {

    private int id;

    public SkyIsland(int id) {
        this.id = id;
    }

    public Location getCenter(World world) {
        List<Integer> origin = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getIntegerList("Generation.PLAYER_ISLANDS.GRID_ORIGIN");
            if (id == 1) return new Location(world, origin.get(0), origin.get(1), origin.get(2));

            int pos = id - 1;
            int radius = (int) (Math.floor((Math.sqrt(pos) - 1) / 2) + 1);
            int diameter = radius * 2;
            int perimeter = diameter * 4;

            int lastCompletePosition = (perimeter * (radius - 1)) / 2;

            int currentIndexInPerimeter = (pos - lastCompletePosition) % perimeter;

            Location location;

            switch (currentIndexInPerimeter / diameter) {
                case 0:
                    location = new Location(world, (currentIndexInPerimeter - radius), 0, -radius);
                    break;
                case 1:
                    location = new Location(world, radius, 0, (currentIndexInPerimeter % diameter) - radius);
                    break;
                case 2:
                    location = new Location(world, radius - (currentIndexInPerimeter % diameter), 0, radius);
                    break;
                case 3:
                    location = new Location(world, -radius, 0, radius - (currentIndexInPerimeter % diameter));
                    break;
                default:
                    throw new IllegalStateException("Could not find island location with ID: " + id);
            }

            Location newLocation = location.multiply((SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Generation.PLAYER_ISLANDS.REGION_SIZE") + SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Generation.PLAYER_ISLANDS.REGION_PADDING")));

            return newLocation;
    }

    public Location getPosition1(World world) {
        if (world == null) {
            double size = getSize() / 2.00;
            return getCenter(null).subtract(new Location(null, size, -64, size));
        }

        double size = getSize() / 2.00;
        return getCenter(world).subtract(new Location(world, size, -64, size));
    }

    public Location getPosition2(World world) {
        if (world == null) {
            double size = getSize() / 2.00;
            return getCenter(null).add(new Location(null, size, 219, size));
        }

        double size = getSize() / 2.00;
        return getCenter(world).add(new Location(world, size, 219, size));
    }

    public int getSize() {
        return SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Generation.REGION_SIZE");
    }
}
