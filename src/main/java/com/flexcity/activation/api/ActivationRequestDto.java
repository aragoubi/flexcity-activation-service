package com.flexcity.activation.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ActivationRequestDto(
        @NotNull(message = "date is required")
        LocalDate date,

        @NotNull(message = "requestedVolumeKw is required")
        @Positive(message = "requestedVolumeKw must be positive")
        Integer requestedVolumeKw
) {
}