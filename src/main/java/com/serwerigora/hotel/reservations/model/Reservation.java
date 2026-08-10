package com.serwerigora.hotel.reservations.model;

import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("reservations")
public record Reservation(
        @Id Long id,
        @Positive Integer clientId,
        @Positive Integer roomNumber,
        LocalDate firstDay,
        LocalDate lastDay
) {
}
