package com.serwerigora.hotel.reservations;

import com.serwerigora.hotel.clients.model.ClientsRepository;
import com.serwerigora.hotel.reservations.model.Reservation;
import com.serwerigora.hotel.reservations.model.ReservationsRepository;
import com.serwerigora.hotel.rooms.model.RoomsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



@Service
public class ReservationsService {

    ReservationsRepository reservations;
    ClientsRepository clients;
    RoomsRepository rooms;
    private static final Logger logger =
            LoggerFactory.getLogger(ReservationsService.class);

    ReservationsService(ReservationsRepository reservations, ClientsRepository clients,
                        RoomsRepository rooms) {
        this.reservations = reservations;
        this.clients = clients;
        this.rooms = rooms;
    }

    Reservation createReservation(Reservation requested)
    {
        checkReservationValid(requested);
        return reservations.save(requested);
    }

    Reservation updateReservation(Reservation requested) {
        if (!reservations.existsById(requested.id()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        checkReservationValid(requested);
        return reservations.save(requested);
    }

    private void checkReservationValid(Reservation requested) throws ResponseStatusException
    {
        if (!requested.firstDay().isBefore(requested.lastDay()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last day must be at least 1 day after the first day");
        if (!clients.existsById(requested.clientId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        if (!rooms.existsById(requested.roomNumber()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        checkReservationsNotOverlappingOnTheSameRoom(requested);
    }

    private void checkReservationsNotOverlappingOnTheSameRoom(Reservation requested) throws ResponseStatusException {
        var overlapping = reservations.findOverlappingReservations
                (requested.roomNumber(), requested.firstDay(), requested.lastDay(), requested.id());
        if (requested.id() != null)
            logger.warn("UPDATE - Overlapping count: " + overlapping.size());
        if (!overlapping.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Room occupied on this date. Conflicting reservation ID: " + overlapping.get(0).id());
    }
}
