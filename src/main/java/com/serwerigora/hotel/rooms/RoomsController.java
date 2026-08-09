package com.serwerigora.hotel.rooms;

import com.serwerigora.hotel.rooms.model.Room;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomsController {

    private RoomsRepository repository;

    RoomsController(RoomsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Room> getRooms() {
        return this.repository.findAll();
    }
}
