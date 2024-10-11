package net.skullian.skyfactions.faction;

import lombok.Getter;

public enum RankType {

    OWNER("owner"),
    ADMIN("admin"),
    MODERATOR("moderator"),
    FIGHTER("fighter"),
    MEMBER("member");

    @Getter
    private final String rankValue;
    RankType(String path) { this.rankValue = path; }
}
