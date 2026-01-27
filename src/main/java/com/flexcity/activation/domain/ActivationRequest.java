package com.flexcity.activation.domain;

import java.time.LocalDate;
import java.util.Objects;

public record ActivationRequest(
        LocalDate date,
        int requestedVolumeKw
) {
    public ActivationRequest {
        Objects.requireNonNull(date, "date must not be null");
        if (requestedVolumeKw <= 0) {
            throw new IllegalArgumentException("requestedVolumeKw must be positive");
        }
    }
}