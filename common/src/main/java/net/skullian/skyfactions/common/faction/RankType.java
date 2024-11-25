package net.skullian.skyfactions.common.faction;

import lombok.Getter;

public enum RankType {

    OWNER("owner", 1),
    ADMIN("admin", 2),
    MODERATOR("moderator", 3),
    FIGHTER("fighter", 4),
    MEMBER("member", 5);

    @Getter
    private final String rankValue;
    @Getter
    private final int order;
    RankType(String path, int order) {
        this.rankValue = path;
        this.order = order;
    }
}
