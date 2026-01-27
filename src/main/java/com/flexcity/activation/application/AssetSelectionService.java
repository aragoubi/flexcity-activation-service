package com.flexcity.activation.application;

import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.domain.SelectedAsset;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AssetSelectionService {

    public List<SelectedAsset> selectAssets(List<Asset> assets, LocalDate date, int requestedVolumeKw) {

        //  Filter assets that are available on the requested date and sort them by activation cost
        List<Asset> availableAssets = assets.stream()
                .filter(asset -> asset.availabilityDates().contains(date))
                .sorted(Comparator.comparingDouble(Asset::activationCostEur))
                .toList();

        // Compute the total available capacity for the given date
        int totalAvailableVolume = availableAssets.stream()
                .mapToInt(Asset::maxVolumeKw)
                .sum();

        // Ensure that the total available capacity is sufficient
        if (totalAvailableVolume < requestedVolumeKw) {
            throw new IllegalStateException(
                    "Insufficient capacity: requested " + requestedVolumeKw +
                    " kW but only " + totalAvailableVolume + " kW available on " + date);
        }

        // Select assets to activate until the requested volume is reached
        List<SelectedAsset> selectedAssets = new ArrayList<>();
        int remainingVolume = requestedVolumeKw;

        for (Asset asset : availableAssets) {
            if (remainingVolume <= 0) {
                break;
            }

            int selectedVolume = Math.min(remainingVolume, asset.maxVolumeKw());
            selectedAssets.add(new SelectedAsset(
                    asset.code(),
                    selectedVolume,
                    asset.activationCostEur()
            ));
            remainingVolume -= selectedVolume;
        }

        return selectedAssets;
    }
}
