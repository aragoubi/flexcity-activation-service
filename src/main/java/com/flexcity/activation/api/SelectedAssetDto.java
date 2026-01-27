package com.flexcity.activation.api;

public record SelectedAssetDto(
        String assetCode,
        int selectedVolumeKw,
        double activationCostEur
) {
}