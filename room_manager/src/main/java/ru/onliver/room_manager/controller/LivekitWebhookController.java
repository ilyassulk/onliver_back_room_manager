package ru.onliver.room_manager.controller;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitModels.Room;
import ru.onliver.room_manager.enums.LivekitWebhookEventType;
import livekit.LivekitWebhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livekit")
public class LivekitWebhookController {



    private final WebhookReceiver webhookReceiver;

    public LivekitWebhookController(
            @Value("${livekit.apiKey}") String apiKey,
            @Value("${livekit.apiSecret}") String apiSecret
    ) {
        this.webhookReceiver = new WebhookReceiver(apiKey, apiSecret);
    }

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
            // Валидация подписи и декодирование JSON
            event = webhookReceiver.receive(body, authHeader);
        } catch (Exception ex) {
            // Неверная подпись или некорректный JSON
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("{\"error\":\"Invalid webhook signature\"}");
        }

        // Обработка по типу события
        switch (event.getEvent()) {
            case "room_started":
                handleRoomStarted(event.getRoom());
                break;
            case "room_finished":
                handleRoomFinished(event.getRoom());
                break;
            default:
                // Неизвестное событие
                break;
        }

        // Успешный ответ LiveKit: HTTP 200 без тела
        return ResponseEntity.ok().build();
    }

    private void handleRoomStarted(Room room) {
        // Например: логируем или запускаем какие‑то фоновые задачи
        System.out.println("Room started: " + room.getName() + " (ID: " + room.getSid() + ")");
    }

    private void handleRoomFinished(Room room) {
        System.out.println("Room finished: " + room.getName() + " (ID: " + room.getSid() + ")");
    }
}