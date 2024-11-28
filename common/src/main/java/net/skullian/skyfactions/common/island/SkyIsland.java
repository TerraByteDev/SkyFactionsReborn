package net.skullian.skyfactions.common.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.common.util.SkyLocation;
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

    public SkyLocation getCenter(String worldName) {
        if (id == 1) return new SkyLocation(worldName, gridOrigin.get(0), gridOrigin.get(1), gridOrigin.get(2));

        int pos = id - 1;
        int radius = (int) (Math.floor((Math.sqrt(pos) - 1) / 2) + 1);
        int diameter = radius * 2;
        int perimeter = diameter * 4;

        int lastComplete = (perimeter * (radius - 1)) / 2;
        int currentIndex = (pos - lastComplete) % perimeter;

        SkyLocation location;
        switch (currentIndex / diameter) {
            case 0:
                location = new SkyLocation(worldName, (currentIndex - radius), gridOrigin.get(1), -radius);
                break;
            case 1:
                location = new SkyLocation(worldName, radius, gridOrigin.get(1), (currentIndex % diameter) - radius);
                break;
            case 2:
                location = new SkyLocation(worldName, radius - (currentIndex % diameter), gridOrigin.get(1), radius);
                break;
            case 3:
                location = new SkyLocation(worldName, -radius, gridOrigin.get(1), radius - (currentIndex % diameter));
                break;
            default:
                throw new IllegalStateException("Could not find island location with ID: " + id);
        }

        return location.multiply((regionSize + regionPadding));
    }

    public SkyLocation getPosition1(String worldName) {
        if (worldName == null) {
            double size = getSize();
            SkyLocation center = getCenter(null).subtract(new SkyLocation(null, size, gridOrigin.get(1), size));
            center.setY(-64);
            return center;
        }

        double size = getSize();
        SkyLocation center = getCenter(worldName).subtract(new SkyLocation(worldName, size, gridOrigin.get(1), size));
        center.setY(-64);
        return center;
    }

    public SkyLocation getPosition2(String worldName) {
        if (worldName == null) {
            double size = getSize();
            SkyLocation center = getCenter(null).add(new SkyLocation(null, size, gridOrigin.get(1), size));
            center.setY(219);
            return center;
        }

        double size = getSize();
        SkyLocation center = getCenter(worldName).add(new SkyLocation(worldName, size, gridOrigin.get(1), size));
        center.setY(219);
        return center;
    }

    public int getSize() { return regionSize; }
}
