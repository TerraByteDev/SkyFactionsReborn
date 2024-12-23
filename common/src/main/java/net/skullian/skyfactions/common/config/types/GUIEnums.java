package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum GUIEnums {

    RUNES_SUBMIT_GUI("runes_ui"),
    CREATE_ISLAND_GUI("confirmations/create_island"),
    RAID_START_GUI("confirmations/start_raid"),
    FACTION_LEAVE_GUI("confirmations/faction_leave"),

    OBELISK_PLAYER_GUI("obelisk/player_obelisk"),
    OBELISK_FACTION_GUI("obelisk/faction_obelisk"),
    OBELISK_FACTION_ELECTION_GUI("obelisk/faction_election"),
    OBELISK_MEMBER_MANAGEMENT_GUI("obelisk/member/member_management"),
    OBELISK_MANAGE_MEMBER_GUI("obelisk/member/manage_member"),
    OBELISK_MANAGE_MEMBER_RANK_GUI("obelisk/member/manage_member_rank"),
    OBELISK_AUDIT_LOG_GUI("obelisk/audit_log"),
    OBELISK_INVITE_SELECTION_GUI("obelisk/invites/invite_selection"),
    OBELISK_INVITE_INCOMING_GUI("obelisk/invites/incoming_requests"),
    OBELISK_INVITE_OUTGOING_GUI("obelisk/invites/outgoing_invites"),
    OBELISK_PLAYER_INVITE_MANAGE_GUI("obelisk/invites/player_invite_manage"),
    OBELISK_PLAYER_INCOMING_INVITES_GUI("obelisk/invites/player_faction_invites"),
    OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI("obelisk/invites/player_invite_selection"),
    OBELISK_PLAYER_JOIN_REQUEST_MANAGE_GUI("obelisk/invites/player_join_request"),
    OBELISK_JOIN_REQUEST_MANAGE_GUI("obelisk/invites/join_request_manage"),
    OBELISK_PLAYER_NOTIFICATIONS_GUI("obelisk/player_notifications"),
    OBELISK_DEFENCE_PURCHASE_OVERVIEW_GUI("obelisk/defences/defence_purchase_overview"),
    OBELISK_PURCHASE_DEFENCE_GUI("obelisk/defences/purchase_defence"),

    DEFENCE_MANAGEMENT_UI("defence/management_ui"),

    PAGINATION_MODEL("pagination");

    private final String path;
    public static final Map<String, Map<String, YamlDocument>> configs = new HashMap<>();

    GUIEnums(String path) {
        this.path = path;
    }
}
