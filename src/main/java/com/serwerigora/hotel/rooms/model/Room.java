package com.serwerigora.hotel.rooms.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("rooms")
public record Room(
        @Id
        @Positive
        Integer number,
        @Positive
        Integer maxPersons,
        @Column(value = "class")
        @NotNull RoomClass roomClass
) {}