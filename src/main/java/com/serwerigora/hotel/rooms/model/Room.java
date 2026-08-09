package com.serwerigora.hotel.rooms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("rooms")
public record Room(
        @Id
        int number,
        int maxPersons,
        @Column(value = "class")
        RoomClass roomClass
) {}