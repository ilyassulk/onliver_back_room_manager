package ru.onliver.room_manager.service;

import livekit.LivekitModels.Room;

public interface LiveKitRoomService {
    void roomStartedHandler(Room room);
    void roomFinishedHandler(Room room);
}
