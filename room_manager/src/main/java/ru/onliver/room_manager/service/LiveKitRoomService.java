package ru.onliver.room_manager.service;

import livekit.LivekitModels.Room;

import java.util.List;

public interface LiveKitRoomService {
    void roomStartedHandler(Room room);
    void roomFinishedHandler(Room room);
    Room createRoom(String roomName);
    void deleteRoom(String roomName);
    List<Room> getRooms();
}
