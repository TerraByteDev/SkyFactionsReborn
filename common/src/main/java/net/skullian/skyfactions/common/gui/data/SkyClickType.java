package net.skullian.skyfactions.common.gui.data;

/**
 * What the client did to trigger this action (not the result).
 */
public enum SkyClickType {

    LEFT,
    SHIFT_LEFT,
    RIGHT,
    SHIFT_RIGHT,
    WINDOW_BORDER_LEFT,
    WINDOW_BORDER_RIGHT,
    MIDDLE,
    NUMBER_KEY,
    DOUBLE_CLICK,
    DROP,
    CONTROL_DROP,
    CREATIVE,
    SWAP_OFFHAND,
    UNKNOWN;

    public boolean isKeyboardClick() {
        return (this == SkyClickType.NUMBER_KEY) || (this == SkyClickType.DROP) || (this == SkyClickType.CONTROL_DROP) || (this == SkyClickType.SWAP_OFFHAND);
    }

    public boolean isMouseClick() {
        return (this == SkyClickType.DOUBLE_CLICK) || (this == LEFT) || (this == SkyClickType.RIGHT) || (this == SkyClickType.MIDDLE)
                || (this == SkyClickType.WINDOW_BORDER_LEFT) || (this == SkyClickType.SHIFT_LEFT) || (this == SkyClickType.SHIFT_RIGHT) || (this == SkyClickType.WINDOW_BORDER_RIGHT);
    }

    public boolean isCreativeAction() {
        // Why use middle click?
        return (this == SkyClickType.MIDDLE) || (this == SkyClickType.CREATIVE);
    }

    public boolean isRightClick() {
        return (this == SkyClickType.RIGHT) || (this == SkyClickType.SHIFT_RIGHT);
    }

    public boolean isLeftClick() {
        return (this == SkyClickType.LEFT) || (this == SkyClickType.SHIFT_LEFT) || (this == SkyClickType.DOUBLE_CLICK) || (this == SkyClickType.CREATIVE);
    }

    public boolean isShiftClick() {
        return (this == SkyClickType.SHIFT_LEFT) || (this == SkyClickType.SHIFT_RIGHT);
    }
}
