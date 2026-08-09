package com.serwerigora.hotel.rooms.dto;

import com.serwerigora.hotel.rooms.model.RoomClass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRoomRequest(
        @Positive int maxPersons,
        @NotNull RoomClass roomClass) {
}
