package net.skullian.skyfactions.util.worldborder;

import java.util.function.Supplier;

public abstract class AWorldBorder implements WorldBorderInterface {
    private final ConsumerSupplier<BorderPos> center;
    private final Supplier<BorderPos> minimum;
    private final Supplier<BorderPos> maximum;
    private final ConsumerSupplier<Double> size;

    public AWorldBorder(ConsumerSupplier<BorderPos> center, Supplier<BorderPos> minimum, Supplier<BorderPos> maximum, ConsumerSupplier<Double> size) {
        this.center = center;
        this.minimum = minimum;
        this.maximum = maximum;
        this.size = size;
    }

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
