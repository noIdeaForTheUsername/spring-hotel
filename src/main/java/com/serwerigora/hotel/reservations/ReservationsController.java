package com.serwerigora.hotel.reservations;

import com.serwerigora.hotel.reservations.model.Reservation;
import com.serwerigora.hotel.reservations.model.ReservationsRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationsController {

    ReservationsRepository repository;
    ReservationsService service;

    ReservationsController(ReservationsRepository repository, ReservationsService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/{id}")
    Reservation getReservation(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    @GetMapping
    List<Reservation> getReservations(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @PostMapping
    ResponseEntity<Void> createReservation(@Valid @RequestBody Reservation body) {
        Reservation saved = service.createReservation(body);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id())).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation body) {
        var toUpdate = new Reservation(id, body.clientId(), body.roomNumber(), body.firstDay(), body.lastDay());
        service.updateReservation(toUpdate);
    }
}
