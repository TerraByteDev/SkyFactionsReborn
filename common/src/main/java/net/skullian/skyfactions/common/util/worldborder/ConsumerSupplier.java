package net.skullian.skyfactions.common.util.worldborder;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record ConsumerSupplier<T>(Consumer<T> consumer, Supplier<T> supplier) {

    public ConsumerSupplier {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(supplier);
    }

    public static <T> ConsumerSupplier<T> of(Consumer<T> consumer, Supplier<T> supplier) {
        return new ConsumerSupplier<>(consumer, supplier);
    }

    public T get() {
        return supplier.get();
    }

    public void set(T value) {
        consumer.accept(value);
    }

}
