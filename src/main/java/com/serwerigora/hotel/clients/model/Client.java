package com.serwerigora.hotel.clients.model;

import com.serwerigora.hotel.shared.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "clients")
public record Client(
        @Id Integer id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @PhoneNumber String phoneNumber
) { }
