package net.skullian.skyfactions.util.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TextHologram {

    @Getter @Accessors(chain = true)
    private long updateTaskPeriod = 20L * 3;
    @Getter @Accessors(chain = true)
    private double nearbyEntityScanningDistance = 40.0;
    @Getter
    private final String id;

    @Getter @Accessors(chain = true)
    private int entityID;

    protected Component text = Component.text("Hologram API");
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    @Getter @Setter
    private String owner;

    @Setter @Getter @Accessors(chain = true)
    private Display.Billboard billboard = Display.Billboard.CENTER;
    @Setter @Getter @Accessors(chain = true)
    private int interpolationDurationRotation = 10;
    @Setter @Getter @Accessors(chain = true)
    private int interpolationDurationTransformation = 10;
    @Setter @Getter @Accessors(chain = true)
    private double viewRange = 1.0;
    @Setter @Getter @Accessors(chain = true)
    private boolean shadow = true;
    @Setter @Getter @Accessors(chain = true)
    private int maxLineWidth = 200;
    @Setter @Getter @Accessors(chain = true)
    private int backgroundColor;
    @Setter @Getter @Accessors(chain = true)
    private boolean seeThroughBlocks = false;
    @Setter @Getter @Accessors(chain = true)
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    @Setter @Getter @Accessors(chain = true)
    private byte textOpacity = (byte) -1;

    @Getter @Accessors(chain = true)
    private final RenderMode renderMode;

    @Getter @Accessors(chain = true)
    private Location location;

    @Getter
    private final List<Player> viewers = new CopyOnWriteArrayList<>();

    @Getter
    private boolean dead = false;

    @Getter
    private BukkitTask task;

    public TextHologram(String id, RenderMode renderMode, String owner) {
        this.renderMode = renderMode;
        validateId(id);
        this.id = id.toLowerCase();
        this.owner = owner;
        startRunnable();
    }

    public TextHologram(String id, String owner) {
        this(id, RenderMode.NEARBY, owner);
    }

    private void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    private void startRunnable() {
        if (task != null) return;
        task = Bukkit.getServer().getScheduler().runTaskTimer(SkyFactionsReborn.getInstance(), this::updateAffectedPlayers, 20L, updateTaskPeriod);
    }

    /**
     * Use HologramManager#spawn(TextHologram.class, Location.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void spawn(Location location) {
        this.location = location;
        entityID = ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE);
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
        SkyFactionsReborn.getInstance().getServer().getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            updateAffectedPlayers();
            sendPacket(packet);
            this.dead = false;
        });
        update();
    }

    public TextHologram update() {
        SkyFactionsReborn.getInstance().getServer().getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            updateAffectedPlayers();
            TextDisplayMeta meta = createMeta();
            sendPacket(meta.createPacket());
        });
        return this;
    }

    private TextDisplayMeta createMeta() {
        TextDisplayMeta meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, EntityTypes.TEXT_DISPLAY);
        meta.setText(getTextAsComponent());
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(toVector3f(this.translation));
        meta.setScale(toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setLineWidth(this.maxLineWidth);
        meta.setViewRange((float) this.viewRange);
        meta.setBackgroundColor(this.backgroundColor);
        meta.setTextOpacity(this.textOpacity);
        meta.setShadow(this.shadow);
        meta.setSeeThrough(this.seeThroughBlocks);
        setAlignment(meta);
        return meta;
    }

    private TextHologram setAlignment(TextDisplayMeta meta) {
        switch (this.alignment) {
            case LEFT -> meta.setAlignLeft(true);
            case RIGHT -> meta.setAlignRight(true);
        }
        return this;
    }

    private com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

    /**
     * Use HologramManager#remove(TextHologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
    }

    public TextHologram teleport(Location location) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return this;
    }

    public TextHologram addAllViewers(List<Player> viewerList) {
        this.viewers.addAll(viewerList);
        return this;
    }

    public TextHologram addViewer(Player player) {
        this.viewers.add(player);
        return this;
    }

    public TextHologram removeViewer(Player player) {
        this.viewers.remove(player);
        return this;
    }

    public TextHologram removeAllViewers() {
        this.viewers.clear();
        return this;
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public TextHologram setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return this;
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public TextHologram setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return this;
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return ((TextComponent) this.text).content();
    }

    public String getTextWithoutColor() {
        return ChatColor.stripColor(getText());
    }

    public TextHologram setText(String text) {
        this.text = Component.text(TextUtility.color(text, null));
        return this;
    }

    public TextHologram setText(Component component) {
        this.text = component;
        return this;
    }

    private void updateAffectedPlayers() {
        viewers.stream()
                .filter(player -> player.isOnline() && (player.getWorld() != this.location.getWorld() || player.getLocation().distance(this.location) > (isFaction() ? Settings.GEN_FACTION_REGION_SIZE.getInt() : Settings.GEN_PLAYER_REGION_SIZE.getInt())))
                .forEach(player -> {
                    WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                });

        if (this.renderMode == RenderMode.VIEWER_LIST) return;

        if (this.renderMode == RenderMode.ALL) {
            this.addAllViewers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        } else if (this.renderMode == RenderMode.NEARBY) {
            this.location.getWorld().getNearbyEntities(this.location, nearbyEntityScanningDistance, nearbyEntityScanningDistance, nearbyEntityScanningDistance)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(entity -> this.viewers.add((Player) entity));
        }
    }

    private void sendPacket(PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE) return;
        viewers.forEach(player -> PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet));
    }

    private boolean isFaction() {
        try {
            UUID.fromString(this.owner);
            return false; // is player uuid
        } catch (Exception ignored) {
            return true; // is false
        }
    }


    public enum RenderMode {
        NONE,
        VIEWER_LIST,
        ALL,
        NEARBY
    }
}