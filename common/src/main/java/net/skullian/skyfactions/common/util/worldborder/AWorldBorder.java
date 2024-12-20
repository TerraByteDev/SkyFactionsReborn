package net.skullian.skyfactions.common.util.worldborder;

import net.skullian.skyfactions.common.user.SkyUser;

import java.util.function.Supplier;

public abstract class AWorldBorder implements WorldBorderInterface {
    private ConsumerSupplier<BorderPos> center;
    private Supplier<BorderPos> minimum;
    private Supplier<BorderPos> maximum;
    private ConsumerSupplier<Double> size;

    public AWorldBorder(ConsumerSupplier<BorderPos> center, Supplier<BorderPos> minimum, Supplier<BorderPos> maximum, ConsumerSupplier<Double> size) {
        this.center = center;
        this.minimum = minimum;
        this.maximum = maximum;
        this.size = size;
    }

    public AWorldBorder(SkyUser player) {}

    public AWorldBorder(String world) {}

    @Override
    public void centre(BorderPos location) {
        this.center.set(location);
    }

    @Override
    public BorderPos centre() {
        return this.center.get();
    }

    @Override
    public BorderPos minimumPos() {
        return this.minimum.get();
    }

    @Override
    public BorderPos maximumPos() {
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
