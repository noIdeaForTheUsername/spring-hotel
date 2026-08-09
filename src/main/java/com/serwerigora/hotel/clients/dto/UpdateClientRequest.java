package com.serwerigora.hotel.clients.dto;

import com.serwerigora.hotel.shared.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record UpdateClientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @PhoneNumber String phoneNumber
) {
}
