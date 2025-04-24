package ru.onliver.room_manager.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import ru.onliver.room_manager.model.RoomEvent;
import ru.onliver.room_manager.repository.RoomRepository;
import ru.onliver.room_manager.util.KafkaProducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class LiveKitRoomServiceImplTest {

    private RoomRepository roomRepository;
    private KafkaProducer kafkaProducer;
    private RoomServiceClient roomServiceClient;
    private LiveKitRoomServiceImpl service;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        kafkaProducer = mock(KafkaProducer.class);
        roomServiceClient = mock(RoomServiceClient.class);
        service = new LiveKitRoomServiceImpl(roomRepository, kafkaProducer, roomServiceClient);
    }

    @Test
    void createRoom_success() throws Exception {
        Call<LivekitModels.Room> call = mock(Call.class);
        LivekitModels.Room liveRoom = mock(LivekitModels.Room.class);
        when(roomServiceClient.createRoom("r1")).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(liveRoom));

        LivekitModels.Room result = service.createRoom("r1");
        assertSame(liveRoom, result);
    }

    @Test
    void createRoom_errorResponse() throws Exception {
        Call<LivekitModels.Room> call = mock(Call.class);
        when(roomServiceClient.createRoom("r2")).thenReturn(call);
        ResponseBody errorBody = ResponseBody.create(MediaType.parse("text/plain"), "err body");
        when(call.execute()).thenReturn(Response.error(400, errorBody));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createRoom("r2"));
        assertTrue(ex.getMessage().contains("Error room creating"));
        assertTrue(ex.getMessage().contains("err body"));
    }

    @Test
    void createRoom_ioException() throws Exception {
        Call<LivekitModels.Room> call = mock(Call.class);
        when(roomServiceClient.createRoom("r3")).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("io error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createRoom("r3"));
        assertTrue(ex.getMessage().contains("Error room creating"));
    }

    @Test
    void deleteRoom_success() throws Exception {
        Call<Void> call = mock(Call.class);
        when(roomServiceClient.deleteRoom("r")).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(null));

        assertDoesNotThrow(() -> service.deleteRoom("r"));
    }

    @Test
    void deleteRoom_errorResponse() throws Exception {
        Call<Void> call = mock(Call.class);
        when(roomServiceClient.deleteRoom("r2")).thenReturn(call);
        ResponseBody errorBody = ResponseBody.create(MediaType.parse("text/plain"), "del err");
        when(call.execute()).thenReturn(Response.error(500, errorBody));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteRoom("r2"));
        assertTrue(ex.getMessage().contains("Error deleting creating"));
        assertTrue(ex.getMessage().contains("del err"));
    }

    @Test
    void deleteRoom_ioException() throws Exception {
        Call<Void> call = mock(Call.class);
        when(roomServiceClient.deleteRoom("r3")).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("io del error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteRoom("r3"));
        assertTrue(ex.getMessage().contains("Error deleting creating"));
    }

    @Test
    void getRooms_success() throws Exception {
        Call<List<LivekitModels.Room>> call = mock(Call.class);
        List<LivekitModels.Room> body = Arrays.asList(mock(LivekitModels.Room.class), mock(LivekitModels.Room.class));
        when(roomServiceClient.listRooms()).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(body));

        List<LivekitModels.Room> result = service.getRooms();
        assertEquals(body, result);
    }

    @Test
    void getRooms_errorResponse() throws Exception {
        Call<List<LivekitModels.Room>> call = mock(Call.class);
        when(roomServiceClient.listRooms()).thenReturn(call);
        ResponseBody errorBody = ResponseBody.create(MediaType.parse("text/plain"), "list err");
        when(call.execute()).thenReturn(Response.error(400, errorBody));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getRooms());
        assertTrue(ex.getMessage().contains("Error getting creating"));
        assertTrue(ex.getMessage().contains("list err"));
    }

    @Test
    void getRooms_ioException() throws Exception {
        Call<List<LivekitModels.Room>> call = mock(Call.class);
        when(roomServiceClient.listRooms()).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("io list error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getRooms());
        assertTrue(ex.getMessage().contains("Error getting creating"));
    }

    @Test
    void roomStartedHandler_invokesRepositoryAndKafka() {
        LivekitModels.Room liveRoom = mock(LivekitModels.Room.class);
        when(liveRoom.getName()).thenReturn("r1");

        service.roomStartedHandler(liveRoom);

        verify(roomRepository).save(any(ru.onliver.room_manager.model.Room.class));
        verify(kafkaProducer).send("room-events", RoomEvent.buildRoomStartedEvent("r1"));
    }

    @Test
    void roomFinishedHandler_invokesRepositoryAndKafka() {
        LivekitModels.Room liveRoom = mock(LivekitModels.Room.class);
        when(liveRoom.getName()).thenReturn("r2");

        service.roomFinishedHandler(liveRoom);

        verify(roomRepository).deleteByName("r2");
        verify(kafkaProducer).send("room-events", RoomEvent.buildRoomFinishedEvent("r2"));
    }
} 