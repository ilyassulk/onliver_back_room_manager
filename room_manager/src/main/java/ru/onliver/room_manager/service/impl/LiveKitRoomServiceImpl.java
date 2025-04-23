package ru.onliver.room_manager.service.impl;

import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.onliver.room_manager.model.Room;
import ru.onliver.room_manager.model.RoomEvent;
import ru.onliver.room_manager.repository.RoomRepository;
import ru.onliver.room_manager.service.LiveKitRoomService;
import ru.onliver.room_manager.util.KafkaProducer;

@Service
@Primary
@AllArgsConstructor
public class LiveKitRoomServiceImpl implements LiveKitRoomService {
    public final RoomRepository roomRepository;
    public final KafkaProducer kafkaProducer;
    private final String topic = "room-events";

    @Override
    public void roomStartedHandler(LivekitModels.Room room) {
        Room roomDB = new Room(room.getName());
        roomRepository.save(roomDB);
        kafkaProducer.send(topic, RoomEvent.buildRoomStartedEvent(room.getName()));
    }

    @Override
    public void roomFinishedHandler(LivekitModels.Room room) {
        roomRepository.deleteByName(room.getName());
        kafkaProducer.send(topic, RoomEvent.buildRoomFinishedEvent(room.getName()));
    }
}
