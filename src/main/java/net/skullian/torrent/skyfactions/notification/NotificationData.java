package net.skullian.torrent.skyfactions.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NotificationData {

    private UUID uuid;
    private String title;
    private String description;
    private long timestamp;

}
