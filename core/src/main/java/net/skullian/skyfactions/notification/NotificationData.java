package net.skullian.skyfactions.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NotificationData {

    private UUID uuid;
    private String type;
    private Object[] replacements;
    private long timestamp;

}
