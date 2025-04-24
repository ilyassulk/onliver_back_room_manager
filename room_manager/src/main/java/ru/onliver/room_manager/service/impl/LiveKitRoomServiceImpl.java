package ru.onliver.room_manager.service.impl;

import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ru.onliver.room_manager.model.Room;
import ru.onliver.room_manager.model.RoomEvent;
import ru.onliver.room_manager.repository.RoomRepository;
import ru.onliver.room_manager.service.LiveKitRoomService;
import ru.onliver.room_manager.util.KafkaProducer;

import java.io.IOException;
import java.util.List;

@Service
@Primary
@AllArgsConstructor
public class LiveKitRoomServiceImpl implements LiveKitRoomService {
    public final RoomRepository roomRepository;
    public final KafkaProducer kafkaProducer;
    private final String topic = "room-events";
    private final RoomServiceClient roomServiceClient;

    public LivekitModels.Room createRoom(String roomName) {
        try {
            Response<LivekitModels.Room> response = roomServiceClient
                    .createRoom(roomName)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Error room creating: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error room creating: ", e);
        }
    }

    public void deleteRoom(String roomName) {
        try {
            Response<Void> response = roomServiceClient
                    .deleteRoom(roomName)
                    .execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error deleting creating: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting creating: ", e);
        }
    }

    @Override
    public List<LivekitModels.Room> getRooms() {
        try {
            Response<List<LivekitModels.Room>> response = roomServiceClient.listRooms()
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Error getting creating:  " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting creating: ", e);
        }
    }

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
