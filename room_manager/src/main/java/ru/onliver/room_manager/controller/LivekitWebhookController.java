package ru.onliver.room_manager.controller;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitModels.Room;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.onliver.room_manager.enums.LivekitWebhookEventType;
import livekit.LivekitWebhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.onliver.room_manager.service.LiveKitRoomService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/livekit")
public class LivekitWebhookController {

    private final WebhookReceiver webhookReceiver;
    private final LiveKitRoomService liveKitRoomService;

    @PostMapping(
        path = "/webhook",
        consumes = "application/webhook+json",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> handleWebhook(
            @RequestBody String body,
            @RequestHeader("Authorization") String authHeader
    ) {
        LivekitWebhook.WebhookEvent event;
        try {
            event = webhookReceiver.receive(body, authHeader);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("{\"error\":\"Invalid webhook signature\"}");
        }

        switch (LivekitWebhookEventType.fromString(event.getEvent())) {
            case LivekitWebhookEventType.ROOM_STARTED:
                liveKitRoomService.roomStartedHandler(event.getRoom());
                break;
            case LivekitWebhookEventType.ROOM_FINISHED:
                liveKitRoomService.roomFinishedHandler(event.getRoom());
                break;
            default:
                break;
        }

        // Успешный ответ LiveKit: HTTP 200 без тела
        return ResponseEntity.ok().build();
    }
}