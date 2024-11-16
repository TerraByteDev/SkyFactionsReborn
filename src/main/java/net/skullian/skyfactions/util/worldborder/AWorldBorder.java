package net.skullian.skyfactions.util.worldborder;

import org.bukkit.Location;

import java.util.function.Supplier;

public abstract class AWorldBorder implements WorldBorderInterface {

    private final ConsumerSupplier<WorldBorder.BorderPos> center;
    private final Supplier<WorldBorder.BorderPos> minimum;
    private final Supplier<WorldBorder.BorderPos> maximum;

    private final ConsumerSupplier<Double> size;

    public AWorldBorder(ConsumerSupplier<WorldBorder.BorderPos> center, Supplier<WorldBorder.BorderPos> minimum, Supplier<WorldBorder.BorderPos> maximum, ConsumerSupplier<Double> size) {
        this.center = center;
        this.minimum = minimum;
        this.maximum = maximum;
        this.size = size;
    }

    @Override
    public void centre(WorldBorder.BorderPos location) {
        this.center.set(location);
    }

    @Override
    public WorldBorder.BorderPos centre() {
        return this.center.get();
    }

    @Override
    public WorldBorder.BorderPos minimumPos() {
        return this.minimum.get();
    }

    @Override
    public WorldBorder.BorderPos maximumPos() {
        return this.maximum.get();
    }

    @Override
    public double worldBorderSize() {
        return this.size.get();
    }

    @Override
    public void worldBorderSize(double radius) {
        this.size.set(radius);
    }

}
