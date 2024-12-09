package edu.miu.horelo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EstoreRequest(
        String phoneNumber,
        String name,
        String email
) {
}
