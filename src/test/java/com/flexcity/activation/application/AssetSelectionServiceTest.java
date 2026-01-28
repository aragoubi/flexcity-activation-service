package com.flexcity.activation.application;

import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.domain.InsufficientCapacityException;
import com.flexcity.activation.domain.SelectedAsset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssetSelectionServiceTest {

    private AssetSelectionService service;
    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @BeforeEach
    void setUp() {
        service = new AssetSelectionService();
    }

    @Test
    void shouldSelectCheapestCombinationToMeetRequestedVolume() {
        List<Asset> assets = List.of(
                new Asset("EXPENSIVE", "Expensive Asset", 100.0, Set.of(DATE), 500),
                new Asset("CHEAP_1", "Cheap Asset 1", 20.0, Set.of(DATE), 300),
                new Asset("CHEAP_2", "Cheap Asset 2", 30.0, Set.of(DATE), 400)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 600);

        int totalVolume = result.stream().mapToInt(SelectedAsset::selectedVolumeKw).sum();
        assertTrue(totalVolume >= 600);

        double totalCost = result.stream().mapToDouble(SelectedAsset::activationCostEur).sum();
        assertEquals(50.0, totalCost);

        List<String> codes = result.stream().map(SelectedAsset::assetCode).toList();
        assertTrue(codes.contains("CHEAP_1"));
        assertTrue(codes.contains("CHEAP_2"));
        assertFalse(codes.contains("EXPENSIVE"));
    }

    @Test
    void shouldIgnoreAssetsNotAvailableOnGivenDate() {
        LocalDate targetDate = LocalDate.of(2024, 6, 2);
        List<Asset> assets = List.of(
                new Asset("AVAILABLE", "Available Asset", 50.0, Set.of(targetDate), 500),
                new Asset("UNAVAILABLE", "Unavailable Asset", 10.0, Set.of(DATE), 1000)
        );

        List<SelectedAsset> result = service.selectAssets(assets, targetDate, 400);

        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.get(0).assetCode());
    }

    @Test
    void shouldThrowWhenRequestedVolumeCannotBeReached() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 200),
                new Asset("A2", "Asset 2", 60.0, Set.of(DATE), 300)
        );

        InsufficientCapacityException exception = assertThrows(
                InsufficientCapacityException.class,
                () -> service.selectAssets(assets, DATE, 1000)
        );

        assertTrue(exception.getMessage().contains("1000"));
        assertTrue(exception.getMessage().contains("500"));
    }

    @Test
    void shouldReturnEmptyListWhenNoAssetIsEligibleForGivenDate() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 500),
                new Asset("A2", "Asset 2", 60.0, Set.of(DATE), 500)
        );
        LocalDate differentDate = LocalDate.of(2024, 7, 1);

        List<SelectedAsset> result = service.selectAssets(assets, differentDate, 100);

        assertTrue(result.isEmpty());
    }


    @Test
    void shouldSelectSingleAssetWhenItAloneMeetsRequestedVolume() {
        List<Asset> assets = List.of(
                new Asset("LARGE", "Large Asset", 80.0, Set.of(DATE), 1000),
                new Asset("SMALL", "Small Asset", 50.0, Set.of(DATE), 200)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 800);

        assertEquals(1, result.size());
        assertEquals("LARGE", result.get(0).assetCode());
        assertEquals(80.0, result.get(0).activationCostEur());
    }

    @Test
    void shouldPreferCheaperCombinationOverSingleExpensiveAsset() {
        List<Asset> assets = List.of(
                new Asset("EXPENSIVE_LARGE", "Expensive Large", 200.0, Set.of(DATE), 1000),
                new Asset("CHEAP_1", "Cheap 1", 40.0, Set.of(DATE), 400),
                new Asset("CHEAP_2", "Cheap 2", 40.0, Set.of(DATE), 400),
                new Asset("CHEAP_3", "Cheap 3", 40.0, Set.of(DATE), 400)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 1000);

        double totalCost = result.stream().mapToDouble(SelectedAsset::activationCostEur).sum();
        assertTrue(totalCost < 200.0);

        int totalVolume = result.stream().mapToInt(SelectedAsset::selectedVolumeKw).sum();
        assertTrue(totalVolume >= 1000);
    }
}