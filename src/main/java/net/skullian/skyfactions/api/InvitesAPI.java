package net.skullian.skyfactions.api;

import net.skullian.skyfactions.database.struct.InviteData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvitesAPI {

    public static final Map<UUID, List<InviteData>> playerIncomingInvites = new HashMap<>();
}
