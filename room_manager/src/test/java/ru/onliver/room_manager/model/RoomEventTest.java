package ru.onliver.room_manager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomEventTest {

    @Test
    void buildRoomStartedEvent() {
        RoomEvent event = RoomEvent.buildRoomStartedEvent("foo");
        assertEquals("room_started", event.getEventType());
        assertEquals("foo", event.getRoomName());
    }

    @Test
    void buildRoomFinishedEvent() {
        RoomEvent event = RoomEvent.buildRoomFinishedEvent("bar");
        assertEquals("room_finished", event.getEventType());
        assertEquals("bar", event.getRoomName());
    }
} 