package net.skullian.skyfactions.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class SkyIsland {

    private int id;
    private int regionSize;
    private int regionPadding;
    private List<Integer> gridOrigin;

    public Location getCenter(World world) {
        if (id == 1) return new Location(world, gridOrigin.get(0), gridOrigin.get(1), gridOrigin.get(2));

        int pos = id - 1;
        int radius = (int) (Math.floor((Math.sqrt(pos) - 1) / 2) + 1);
        int diameter = radius * 2;
        int perimeter = diameter * 4;

        int lastComplete = (perimeter * (radius - 1)) / 2;
        int currentIndex = (pos - lastComplete) % perimeter;

        Location location;
        switch (currentIndex / diameter) {
            case 0:
                location = new Location(world, (currentIndex - radius), 0, -radius);
                break;
            case 1:
                location = new Location(world, radius, 0, (currentIndex % diameter) - radius);
                break;
            case 2:
                location = new Location(world, radius - (currentIndex % diameter), 0, radius);
                break;
            case 3:
                location = new Location(world, -radius, 0, radius - (currentIndex % diameter));
                break;
            default:
                throw new IllegalStateException("Could not find island location with ID: " + id);
        }

        return location.multiply((regionSize + regionPadding));
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

    public int getSize() { return regionSize; }
}
