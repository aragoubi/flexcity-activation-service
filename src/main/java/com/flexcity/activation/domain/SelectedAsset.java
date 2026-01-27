package com.flexcity.activation.domain;

import java.util.Objects;

public record SelectedAsset(
        String assetCode,
        int selectedVolumeKw,
        double activationCostEur
) {
    public SelectedAsset {
        Objects.requireNonNull(assetCode, "assetCode must not be null");
        if (selectedVolumeKw <= 0) {
            throw new IllegalArgumentException("selectedVolumeKw must be positive");
        }
        if (activationCostEur < 0) {
            throw new IllegalArgumentException("activationCostEur must not be negative");
        }
    }
}