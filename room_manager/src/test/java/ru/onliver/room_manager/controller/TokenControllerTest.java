package ru.onliver.room_manager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.onliver.room_manager.config.LiveKitConfig;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenController.class)
public class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LiveKitConfig liveKitConfig;

    @Test
    void generateToken_success() throws Exception {
        when(liveKitConfig.getApiKey()).thenReturn("key");
        when(liveKitConfig.getApiSecret()).thenReturn("secret");

        mockMvc.perform(get("/token")
                .param("roomName", "room1")
                .param("participantName", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void generateToken_missingParam() throws Exception {
        mockMvc.perform(get("/token")
                .param("roomName", "room1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optionsAllowedMethods() throws Exception {
        when(liveKitConfig.getApiKey()).thenReturn("key");
        when(liveKitConfig.getApiSecret()).thenReturn("secret");
        mockMvc.perform(options("/token")
                .param("roomName", "room1")
                .param("participantName", "alice"))
                .andExpect(status().isOk())
                .andExpect(header().string("Allow", "GET,OPTIONS"));
    }
} 