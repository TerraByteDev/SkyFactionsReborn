package net.skullian.skyfactions.common.obelisk;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.util.SkyLocation;

@Getter
@Setter
public abstract class ObeliskBlockEntity {
    private final String BROKEN_MATERIAL = "AIR";
    private final String HITBOX_MATERIAL = "BARRIER";

    private SkyLocation location;
    private ObeliskItem blockItem;

    public abstract void placeBlock(SkyLocation location, ObeliskItem blockItem);

    public abstract void breakBlock();

    public SkyLocation getLocationFromBlock(SkyLocation location) {
        SkyLocation entityLocation = location.clone();
        entityLocation.setX(location.getX() + 0.5);
        entityLocation.setY(location.getY() + 0.5);
        entityLocation.setZ(location.getZ() + 0.5);

        return entityLocation;
    }
}
