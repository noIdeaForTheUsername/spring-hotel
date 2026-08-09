package com.serwerigora.hotel.rooms;

import com.serwerigora.hotel.rooms.dto.UpdateRoomRequest;
import com.serwerigora.hotel.rooms.model.Room;
import com.serwerigora.hotel.rooms.model.RoomsRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomsController {

    private RoomsRepository repository;
    private static final Logger logger =
            LoggerFactory.getLogger(RoomsController.class);

    RoomsController(RoomsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<Room> getRooms() {
        return this.repository.findAll();
    }

    @GetMapping("/{number}")
    Room getRoom(@PathVariable Integer number) {
        var optional = this.repository.findById(number);
        if (optional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optional.get();
    }

    @PostMapping
    ResponseEntity<Void> createRoom(@Valid @RequestBody Room room) {
        try {
            this.repository.insert(room.number(), room.maxPersons(), room.roomClass());
            return ResponseEntity.created(URI.create("/rooms/" + room.number())).build();
        }
        catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room of this number already exists");
        }
    }

    @PutMapping("/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateRoom(@PathVariable Integer number, @Valid @RequestBody UpdateRoomRequest room) {
        boolean found = this.repository.update(number, room.maxPersons(), room.roomClass()) > 0;
        if (!found)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRoom(@PathVariable Integer number) {
        if (!this.repository.existsById(number))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        this.repository.deleteById(number);
    }
}
