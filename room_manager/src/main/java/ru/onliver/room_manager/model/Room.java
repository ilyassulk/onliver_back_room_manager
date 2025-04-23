package ru.onliver.room_manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "room")
public class Room {
    @Id
    Long id;
    String name;

    public Room(String name) {
        this.name = name;
    }
}
