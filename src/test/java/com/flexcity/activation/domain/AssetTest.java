package com.flexcity.activation.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void shouldCreateValidAsset() {
        Asset asset = new Asset("A1", "Asset One", 100.0, Set.of(LocalDate.now()), 500);

        assertEquals("A1", asset.code());
        assertEquals("Asset One", asset.name());
        assertEquals(100.0, asset.activationCostEur());
        assertEquals(500, asset.maxVolumeKw());
    }

    @Test
    void shouldRejectNullCode() {
        assertThrows(NullPointerException.class,
                () -> new Asset(null, "name", 100.0, Set.of(), 500));
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(NullPointerException.class,
                () -> new Asset("A1", null, 100.0, Set.of(), 500));
    }

    @Test
    void shouldRejectNullAvailabilityDates() {
        assertThrows(NullPointerException.class,
                () -> new Asset("A1", "name", 100.0, null, 500));
    }

    @Test
    void shouldRejectNegativeCost() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("A1", "name", -1.0, Set.of(), 500));
    }

    @Test
    void shouldRejectZeroMaxVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("A1", "name", 100.0, Set.of(), 0));
    }

    @Test
    void shouldRejectNegativeMaxVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("A1", "name", 100.0, Set.of(), -1));
    }

    @Test
    void shouldReturnImmutableAvailabilityDates() {
        Asset asset = new Asset("A1", "name", 100.0, Set.of(LocalDate.now()), 500);

        assertThrows(UnsupportedOperationException.class,
                () -> asset.availabilityDates().add(LocalDate.now().plusDays(1)));
    }
}