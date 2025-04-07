package ru.onliver.room_manager.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Value("${livekit.apiKey}")
    private String apiKey;

    @Value("${livekit.secret}")
    private String secretKey;

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.OPTIONS).build();
    }

    @GetMapping
    public ResponseEntity<?> generateToken(@RequestParam String roomName, @RequestParam String participantName) { 
        if (roomName == null || participantName == null) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "roomName and participantName are required"));
        }
    
        AccessToken token = new AccessToken(apiKey, secretKey); 
        token.setName(participantName); 
        token.setIdentity(participantName);
        token.addGrants(new RoomJoin(true), new RoomName(roomName)); 
    
        return ResponseEntity.ok(Map.of("token", token.toJwt()));
    }
}
