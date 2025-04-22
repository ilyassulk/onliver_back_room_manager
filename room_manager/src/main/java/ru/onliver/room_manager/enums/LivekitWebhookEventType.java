package ru.onliver.room_manager.enums;

public enum LivekitWebhookEventType {
    ROOM_STARTED("room_started"),
    ROOM_FINISHED("room_finished");

    String name;

    LivekitWebhookEventType(String name) {
        this.name = name;
    }
}