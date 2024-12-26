package net.skullian.skyfactions.paper.util.worldborder;

import net.skullian.skyfactions.common.util.worldborder.persistence.WBData;
import net.skullian.skyfactions.paper.PaperSharedConstants;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BorderDataTags implements PersistentDataType<PersistentDataContainer, WBData> {

    private final NamespacedKey xKey;
    private final NamespacedKey zKey;
    private final NamespacedKey sizeKey;

    public BorderDataTags(JavaPlugin plugin) {
        this.xKey = PaperSharedConstants.WORLD_BORDER_X_KEY;
        this.zKey = PaperSharedConstants.WORLD_BORDER_Z_KEY;
        this.sizeKey = PaperSharedConstants.WORLD_BORDER_SIZE_KEY;
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

        container.set(sizeKey, DOUBLE, complex.getSize());
        container.set(xKey, DOUBLE, complex.getX());
        container.set(zKey, DOUBLE, complex.getZ());

        return container;
    }

    @NotNull
    @Override
    public WBData fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        WBData data = new WBData();

        fetch(primitive, sizeKey, DOUBLE).ifPresent(data::setSize);
        fetch(primitive, xKey, DOUBLE).ifPresent(data::setX);
        fetch(primitive, zKey, DOUBLE).ifPresent(data::setZ);

        return data;
    }

    private <T, Z> Optional<Z> fetch(PersistentDataContainer persistentDataContainer, NamespacedKey namespacedKey, PersistentDataType<T, Z> type) {
        if (persistentDataContainer.has(namespacedKey, type)) {
            return Optional.ofNullable(persistentDataContainer.get(namespacedKey, type));
        }
        return Optional.empty();
    }
}
