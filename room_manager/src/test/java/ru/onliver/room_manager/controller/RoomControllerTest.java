package ru.onliver.room_manager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.onliver.room_manager.service.LiveKitRoomService;
import livekit.LivekitModels;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LiveKitRoomService roomService;

    @Test
    void createRoom_success() throws Exception {
        LivekitModels.Room liveRoom = mock(LivekitModels.Room.class);
        when(liveRoom.getName()).thenReturn("foo");
        when(roomService.createRoom("foo")).thenReturn(liveRoom);

        mockMvc.perform(post("/room").param("name", "foo"))
                .andExpect(status().isOk())
                .andExpect(content().string("foo"));
    }

    @Test
    void createRoom_failure() throws Exception {
        doThrow(new RuntimeException("err")).when(roomService).createRoom("foo");

        mockMvc.perform(post("/room").param("name", "foo"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Couldn't create a room: err"));
    }

    @Test
    void deleteRoom_success() throws Exception {
        mockMvc.perform(delete("/room/foo"))
                .andExpect(status().isOk())
                .andExpect(content().string("The room was successfully deleted"));
    }

    @Test
    void deleteRoom_failure() throws Exception {
        doThrow(new RuntimeException("delErr")).when(roomService).deleteRoom("foo");

        mockMvc.perform(delete("/room/foo"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Couldn't delete room: delErr"));
    }

    @Test
    void getRooms() throws Exception {
        LivekitModels.Room r1 = mock(LivekitModels.Room.class);
        when(r1.getName()).thenReturn("r1");
        LivekitModels.Room r2 = mock(LivekitModels.Room.class);
        when(r2.getName()).thenReturn("r2");
        when(roomService.getRooms()).thenReturn(java.util.List.of(r1, r2));

        mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("r1"))
                .andExpect(jsonPath("$[1]").value("r2"));
    }

    @Test
    void optionsRoot() throws Exception {
        mockMvc.perform(options("/room"))
                .andExpect(status().isOk())
                .andExpect(header().string("Allow", "GET,POST,OPTIONS"));
    }

    @Test
    void optionsWithPath() throws Exception {
        mockMvc.perform(options("/room/foo"))
                .andExpect(status().isOk())
                .andExpect(header().string("Allow", "DELETE,OPTIONS"));
    }
} 