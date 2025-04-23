package ru.onliver.room_manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.onliver.room_manager.model.Room;

public interface RoomRepository extends MongoRepository<Room, String> {
    void deleteByName(String name);
}
