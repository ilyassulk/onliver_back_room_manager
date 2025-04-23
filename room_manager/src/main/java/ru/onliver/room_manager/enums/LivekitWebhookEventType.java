package ru.onliver.room_manager.enums;

import java.util.Arrays;

public enum LivekitWebhookEventType {
    ROOM_STARTED("room_started"),
    ROOM_FINISHED("room_finished");

    String name;

    LivekitWebhookEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LivekitWebhookEventType fromString(String name) {
        return Arrays.stream(LivekitWebhookEventType.values())
            .filter(eventType -> eventType.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid event type: " + name));
    }
}