package com.flexcity.activation.application;

import com.flexcity.activation.domain.Asset;
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
    void shouldSelectSingleAssetForExactMatch() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 500)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 500);

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).assetCode());
        assertEquals(500, result.get(0).selectedVolumeKw());
        assertEquals(50.0, result.get(0).activationCostEur());
    }

    @Test
    void shouldSelectPartialVolumeFromAsset() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 1000)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 300);

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).assetCode());
        assertEquals(300, result.get(0).selectedVolumeKw());
        assertEquals(50.0, result.get(0).activationCostEur());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientCapacity() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 200),
                new Asset("A2", "Asset 2", 60.0, Set.of(DATE), 300)
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.selectAssets(assets, DATE, 1000)
        );

        assertTrue(exception.getMessage().contains("Insufficient capacity"));
        assertTrue(exception.getMessage().contains("1000"));
        assertTrue(exception.getMessage().contains("500"));
    }

    @Test
    void shouldPreferCheaperAssetsFirst() {
        List<Asset> assets = List.of(
                new Asset("EXPENSIVE", "Expensive Asset", 100.0, Set.of(DATE), 500),
                new Asset("CHEAP", "Cheap Asset", 30.0, Set.of(DATE), 500),
                new Asset("MEDIUM", "Medium Asset", 60.0, Set.of(DATE), 500)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 800);

        assertEquals(2, result.size());
        assertEquals("CHEAP", result.get(0).assetCode());
        assertEquals(500, result.get(0).selectedVolumeKw());
        assertEquals("MEDIUM", result.get(1).assetCode());
        assertEquals(300, result.get(1).selectedVolumeKw());
    }

    @Test
    void shouldFilterByAvailabilityDate() {
        LocalDate targetDate = LocalDate.of(2024, 6, 2);
        List<Asset> assets = List.of(
                new Asset("AVAILABLE", "Available Asset", 50.0, Set.of(targetDate), 500),
                new Asset("UNAVAILABLE", "Unavailable Asset", 30.0, Set.of(DATE), 500)
        );

        List<SelectedAsset> result = service.selectAssets(assets, targetDate, 400);

        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.get(0).assetCode());
    }

    @Test
    void shouldThrowWhenNoAssetsAvailableOnDate() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 50.0, Set.of(DATE), 500)
        );
        LocalDate differentDate = LocalDate.of(2024, 7, 1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.selectAssets(assets, differentDate, 100)
        );

        assertTrue(exception.getMessage().contains("Insufficient capacity"));
        assertTrue(exception.getMessage().contains("0 kW available"));
    }

    @Test
    void shouldSelectMultipleAssetsToMeetVolume() {
        List<Asset> assets = List.of(
                new Asset("A1", "Asset 1", 40.0, Set.of(DATE), 300),
                new Asset("A2", "Asset 2", 50.0, Set.of(DATE), 400),
                new Asset("A3", "Asset 3", 60.0, Set.of(DATE), 500)
        );

        List<SelectedAsset> result = service.selectAssets(assets, DATE, 600);

        assertEquals(2, result.size());
        assertEquals("A1", result.get(0).assetCode());
        assertEquals(300, result.get(0).selectedVolumeKw());
        assertEquals("A2", result.get(1).assetCode());
        assertEquals(300, result.get(1).selectedVolumeKw());
    }
}