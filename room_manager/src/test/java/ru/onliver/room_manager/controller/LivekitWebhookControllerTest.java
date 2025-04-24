package ru.onliver.room_manager.controller;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitModels.Room;
import livekit.LivekitWebhook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.onliver.room_manager.service.LiveKitRoomService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivekitWebhookController.class)
public class LivekitWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookReceiver webhookReceiver;

    @MockitoBean
    private LiveKitRoomService liveKitRoomService;

    @Test
    void handleWebhook_roomStarted() throws Exception {
        LivekitWebhook.WebhookEvent event = mock(LivekitWebhook.WebhookEvent.class);
        when(webhookReceiver.receive(anyString(), anyString())).thenReturn(event);
        when(event.getEvent()).thenReturn("room_started");
        Room room = mock(Room.class);
        when(event.getRoom()).thenReturn(room);

        mockMvc.perform(post("/api/livekit/webhook")
                .contentType("application/webhook+json")
                .header("Authorization", "auth")
                .content("{}"))
                .andExpect(status().isOk());

        verify(liveKitRoomService).roomStartedHandler(room);
    }

    @Test
    void handleWebhook_roomFinished() throws Exception {
        LivekitWebhook.WebhookEvent event = mock(LivekitWebhook.WebhookEvent.class);
        when(webhookReceiver.receive(anyString(), anyString())).thenReturn(event);
        when(event.getEvent()).thenReturn("room_finished");
        Room room = mock(Room.class);
        when(event.getRoom()).thenReturn(room);

        mockMvc.perform(post("/api/livekit/webhook")
                .contentType("application/webhook+json")
                .header("Authorization", "auth")
                .content("{}"))
                .andExpect(status().isOk());

        verify(liveKitRoomService).roomFinishedHandler(room);
    }

    @Test
    void handleWebhook_invalidSignature() throws Exception {
        when(webhookReceiver.receive(anyString(), anyString())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/livekit/webhook")
                .contentType("application/webhook+json")
                .header("Authorization", "auth")
                .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Invalid webhook signature\"}"));
    }
} 