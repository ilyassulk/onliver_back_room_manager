package ru.onliver.room_manager.controller;

import org.springframework.web.bind.annotation.RestController;

import livekit.LivekitModels.Room;
import ru.onliver.room_manager.service.LiveKitRoomService;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final LiveKitRoomService roomService;

    public RoomController(LiveKitRoomService roomService) {
        this.roomService = roomService;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS).build();
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestParam("name") String roomName) {
        try {
            Room room = roomService.createRoom(roomName);
            return ResponseEntity.ok(room.getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Couldn't create a room: " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS, path = "/{roomName}")
    public ResponseEntity<?> handleOptions(@PathVariable String roomName) {
        return ResponseEntity.ok().allow(HttpMethod.DELETE, HttpMethod.OPTIONS).build();
    }

    @DeleteMapping("/{roomName}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomName) {
        try {
            roomService.deleteRoom(roomName);
            return ResponseEntity.ok("The room was successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Couldn't delete room: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getRooms() {
        List<Room> rooms = roomService.getRooms();
        return ResponseEntity.ok(rooms.stream().map(Room::getName).collect(Collectors.toList()));
    }
    // for deploy
}