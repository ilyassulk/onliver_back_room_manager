package ru.onliver.room_manager.service.impl;

import org.springframework.stereotype.Service;
import ru.onliver.room_manager.service.RoomService;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels.Room;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

@Service
@Primary
public class LiveKitRoomService implements RoomService {

    private final RoomServiceClient roomServiceClient;

    public LiveKitRoomService(@Value("${livekit.host}") String host,
                              @Value("${livekit.apiKey}") String apiKey,
                              @Value("${livekit.secret}") String secret) {
        // Используем createClient для создания клиента.
        this.roomServiceClient = RoomServiceClient.createClient(host, apiKey, secret, () -> new OkHttpClient());
    }

    /**
     * Создаёт комнату с указанным именем.
     *
     * @param roomName имя комнаты
     * @return объект комнаты, полученный от LiveKit
     */
    public Room createRoom(String roomName) {
        try {
            // Здесь мы передаём только обязательный параметр roomName,
            // остальные можно оставить по умолчанию, если они не требуются.
            Response<Room> response = roomServiceClient
                    .createRoom(roomName)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Ошибка создания комнаты: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании комнаты", e);
        }
    }

    /**
     * Удаляет комнату по имени.
     *
     * @param roomName имя комнаты для удаления
     */
    public void deleteRoom(String roomName) {
        try {
            Response<Void> response = roomServiceClient
                    .deleteRoom(roomName)
                    .execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Ошибка удаления комнаты: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении комнаты", e);
        }
    }

    @Override
    public List<Room> getRooms() {
        try {
            Response<List<Room>> response = roomServiceClient.listRooms()
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Ошибка получения комнат: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при получении комнат", e);
        }
    }
}