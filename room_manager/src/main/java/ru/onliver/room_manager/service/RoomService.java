package ru.onliver.room_manager.service;

import java.util.List;

import livekit.LivekitModels.Room;

public interface RoomService {

    Room createRoom(String roomName);

    void deleteRoom(String roomName);

    List<Room> getRooms();
}  
