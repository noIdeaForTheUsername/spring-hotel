package com.serwerigora.hotel.reservations.model;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationsRepository extends
        ListCrudRepository<Reservation, Long>,
        PagingAndSortingRepository<Reservation, Long>
{
    @Query("""
    SELECT *
    FROM reservations
    WHERE room_number = :roomNumber
      AND first_day < :lastDay
      AND last_day > :firstDay
      AND (:reservationId IS NULL OR id != :reservationId);
    """)
    List<Reservation> findOverlappingReservations(
            Integer roomNumber,
            LocalDate firstDay,
            LocalDate lastDay,
            Long reservationId
    );
}
