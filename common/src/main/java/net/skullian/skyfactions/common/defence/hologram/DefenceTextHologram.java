package net.skullian.skyfactions.common.defence.hologram;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.List;
import java.util.concurrent.*;

public abstract class DefenceTextHologram {

    @Getter @Accessors(chain = true)
    private final long updateTaskPeriod = 20L * 3;
    @Getter @Accessors(chain = true)
    private final double nearbyEntityScanningDistance = 40.0;
    @Getter
    private final String id;

    @Getter @Accessors(chain = true)
    private int entityID;

    protected Component text = Component.text("Hologram API");

    @Getter @Setter
    private String owner;

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
    private byte textOpacity = (byte) -1;

    @Getter @Accessors(chain = true)
    private final RenderMode renderMode;

    @Getter @Accessors(chain = true)
    private SkyLocation location;

    @Getter
    private final List<SkyUser> viewers = new CopyOnWriteArrayList<>();

    @Getter
    private boolean dead = false;

    @Getter
    private ScheduledFuture<?> task;

    @Getter
    private final DefenceStruct defence;

    @Getter
    @Setter
    private DefenceData data;

    @Getter
    @Setter Object entity;

    @Getter
    private int durability;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public DefenceTextHologram(String id, RenderMode renderMode, String owner, DefenceStruct defence, DefenceData data) {
        this.renderMode = renderMode;
        validateId(id);
        this.defence = defence;
        this.id = id.toLowerCase();
        this.owner = owner;
        this.data = data;
        startRunnable();
    }

    public abstract void spawn(SkyLocation location);

    public abstract void update();

    public abstract void kill();

    public DefenceTextHologram(String id, String owner, DefenceStruct defence, DefenceData data) {
        this(id, RenderMode.NEARBY, owner, defence, data);
    }

    private void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    public abstract void updateAffectedPlayers();

    private void startRunnable() {
        if (task != null) return;

        this.task = executorService.scheduleAtFixedRate(this::updateAffectedPlayers, 1, updateTaskPeriod, TimeUnit.SECONDS);
    }

    public Component createText(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        DefenceStruct struct = SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(locale, SkyApi.getInstance().getDefenceFactory().getDefences().get(Messages.getDefaulLocale())).get(defence.getIDENTIFIER());
        Component[] components = new Component[3];
        
        components[0] = getData().getAMMO() == 0 && struct.isAPPEND_OUT_OF_STOCK_TO_TOP() ? TextUtility.color(struct.getOUT_OF_STOCK_HOLOGRAM(), locale, player) : (data.getDURABILITY() < 100 && struct.isAPPEND_DURABILITY_AT_TOP()) ? TextUtility.color(struct.getDURABILITY_HOLOGRAM(), locale, player) : Component.text("");
        components[1] = TextUtility.fromList(struct.getHOLOGRAM_LIST(), locale, player);
        components[2] = getData().getAMMO() == 0 && !struct.isAPPEND_OUT_OF_STOCK_TO_TOP() ? TextUtility.color(struct.getOUT_OF_STOCK_HOLOGRAM(), locale, player) : (data.getDURABILITY() < 100 && !struct.isAPPEND_DURABILITY_AT_TOP()) ? TextUtility.color(struct.getDURABILITY_HOLOGRAM(), locale, player) : Component.text("");

        return Component.join(JoinConfiguration.newlines(), components);
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return ((TextComponent) this.text).content();
    }

    public abstract String getTextWithoutColor();

    public DefenceTextHologram setText(String text) {
        this.text = TextUtility.color(text, Messages.getDefaulLocale(), null);
        return this;
    }

    public DefenceTextHologram setText(Component component) {
        this.text = component;
        return this;
    }

    public enum RenderMode {
        NONE,
        VIEWER_LIST,
        ALL,
        NEARBY
    }
}
