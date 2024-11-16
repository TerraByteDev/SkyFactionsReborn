package net.skullian.skyfactions.util.worldborder.persistence;

import net.skullian.skyfactions.SkyFactionsReborn;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BorderDataTags implements PersistentDataType<PersistentDataContainer, WBData> {

    private final NamespacedKey xKey;
    private final NamespacedKey zKey;
    private final NamespacedKey sizeKey;

    public BorderDataTags() {
        SkyFactionsReborn plugin = SkyFactionsReborn.getInstance();
        this.xKey = new NamespacedKey(plugin, "center_x");
        this.zKey = new NamespacedKey(plugin, "center_z");
        this.sizeKey = new NamespacedKey(plugin, "size");
    }


    @NotNull
    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @NotNull
    @Override
    public Class<WBData> getComplexType() {
        return WBData.class;
    }

    @NotNull
    @Override
    public PersistentDataContainer toPrimitive(@NotNull WBData complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        container.set(sizeKey, PersistentDataType.DOUBLE, complex.getSize());
        container.set(xKey, PersistentDataType.DOUBLE, complex.getX());
        container.set(zKey, PersistentDataType.DOUBLE, complex.getZ());

        return container;
    }

    @NotNull
    @Override
    public WBData fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        WBData data = new WBData();

        fetch(primitive, sizeKey, PersistentDataType.DOUBLE).ifPresent(data::setSize);
        fetch(primitive, xKey, PersistentDataType.DOUBLE).ifPresent(data::setX);
        fetch(primitive, zKey, PersistentDataType.DOUBLE).ifPresent(data::setZ);

        return data;
    }

    private <T, Z> Optional<Z> fetch(PersistentDataContainer persistentDataContainer, NamespacedKey namespacedKey, PersistentDataType<T, Z> type) {
        if (persistentDataContainer.has(namespacedKey, type)) {
            return Optional.ofNullable(persistentDataContainer.get(namespacedKey, type));
        }
        return Optional.empty();
    }
}
