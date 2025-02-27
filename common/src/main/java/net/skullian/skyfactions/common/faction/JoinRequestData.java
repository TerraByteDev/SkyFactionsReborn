package net.skullian.skyfactions.common.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinRequestData {

    private String factionName;
    private boolean accepted;
    private long timestamp;

}
