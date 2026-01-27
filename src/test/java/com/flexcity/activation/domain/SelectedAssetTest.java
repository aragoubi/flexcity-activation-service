package com.flexcity.activation.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectedAssetTest {

    @Test
    void shouldCreateValidSelectedAsset() {
        SelectedAsset selected = new SelectedAsset("A1", 500, 150.0);

        assertEquals("A1", selected.assetCode());
        assertEquals(500, selected.selectedVolumeKw());
        assertEquals(150.0, selected.activationCostEur());
    }

    @Test
    void shouldRejectNullAssetCode() {
        assertThrows(NullPointerException.class,
                () -> new SelectedAsset(null, 500, 150.0));
    }

    @Test
    void shouldRejectZeroVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new SelectedAsset("A1", 0, 150.0));
    }

    @Test
    void shouldRejectNegativeVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new SelectedAsset("A1", -1, 150.0));
    }

    @Test
    void shouldRejectNegativeCost() {
        assertThrows(IllegalArgumentException.class,
                () -> new SelectedAsset("A1", 500, -1.0));
    }

    @Test
    void shouldAllowZeroCost() {
        SelectedAsset selected = new SelectedAsset("A1", 500, 0.0);
        assertEquals(0.0, selected.activationCostEur());
    }
}