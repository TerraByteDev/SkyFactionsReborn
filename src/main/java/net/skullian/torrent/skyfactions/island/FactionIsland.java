package net.skullian.torrent.skyfactions.island;

import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Settings;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@Getter
public class FactionIsland {

    private int id;
    private int last_raided;

    public FactionIsland(int id, int last_raided) { this.id = id; this.last_raided = last_raided; }

    public Location getCenter(World world) {
        List<Integer> origin = Settings.GEN_FACTION_GRID_ORIGIN.getIntegerList();
        if (id == 1) return new org.bukkit.Location(world, origin.get(0), origin.get(1), origin.get(2));

        int pos = id - 1;
        int radius = (int) (Math.floor((Math.sqrt(pos) - 1) / 2) + 1);
        int diameter = radius * 2;
        int perimeter = diameter * 4;

        int lastCompletePosition = (perimeter * (radius - 1)) / 2;

        int currentIndexInPerimeter = (pos - lastCompletePosition) % perimeter;

        org.bukkit.Location location;

        switch (currentIndexInPerimeter / diameter) {
            case 0:
                location = new org.bukkit.Location(world, (currentIndexInPerimeter - radius), 0, -radius);
                break;
            case 1:
                location = new org.bukkit.Location(world, radius, 0, (currentIndexInPerimeter % diameter) - radius);
                break;
            case 2:
                location = new org.bukkit.Location(world, radius - (currentIndexInPerimeter % diameter), 0, radius);
                break;
            case 3:
                location = new org.bukkit.Location(world, -radius, 0, radius - (currentIndexInPerimeter % diameter));
                break;
            default:
                throw new IllegalStateException("Could not find island location with ID: " + id);
        }

        org.bukkit.Location newLocation = location.multiply((Settings.GEN_FACTION_REGION_SIZE.getInt() + Settings.GEN_FACTION_REGION_PADDING.getInt()));

        return newLocation;
    }

    public Location getPosition1(World world) {
        if (world == null) {
            double size = getSize() / 2.00;
            Location center = getCenter(null).subtract(new Location(null, size, 0, size));
            center.setY(-64);
            return center;
        }

        double size = getSize() / 2.00;
        Location center = getCenter(world).subtract(new Location(world, size, 0, size));
        center.setY(-64);
        return center;
    }

    public Location getPosition2(World world) {
        if (world == null) {
            double size = getSize() / 2.00;
            Location center = getCenter(null).add(new Location(null, size, 0, size));
            center.setY(219);
            return center;
        }

        double size = getSize() / 2.00;
        Location center = getCenter(world).add(new Location(world, size, 0, size));
        center.setY(219);
        return center;
    }

    public int getSize() {
        return Settings.GEN_FACTION_REGION_SIZE.getInt();
    }
}
