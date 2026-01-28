package com.flexcity.activation.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public record Asset(
        String code,
        String name,
        double activationCostEur,
        Set<LocalDate> availabilityDates,
        int maxVolumeKw
) {
    public Asset {
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(availabilityDates, "availabilityDates must not be null");
        if (activationCostEur < 0) {
            throw new IllegalArgumentException("activationCostEur must not be negative");
        }
        if (maxVolumeKw <= 0) {
            throw new IllegalArgumentException("maxVolumeKw must be positive");
        }

        // Ensure immutability by creating an unmodifiable copy
        availabilityDates = Set.copyOf(availabilityDates);
    }

    public boolean canBeActivatedAt(LocalDate date) {
        return availabilityDates.contains(date);
    }
}