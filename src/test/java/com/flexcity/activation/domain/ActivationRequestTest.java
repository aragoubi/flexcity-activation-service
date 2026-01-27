package com.flexcity.activation.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ActivationRequestTest {

    @Test
    void shouldCreateValidRequest() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        ActivationRequest request = new ActivationRequest(date, 1000);

        assertEquals(date, request.date());
        assertEquals(1000, request.requestedVolumeKw());
    }

    @Test
    void shouldRejectNullDate() {
        assertThrows(NullPointerException.class,
                () -> new ActivationRequest(null, 1000));
    }

    @Test
    void shouldRejectZeroVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new ActivationRequest(LocalDate.now(), 0));
    }

    @Test
    void shouldRejectNegativeVolume() {
        assertThrows(IllegalArgumentException.class,
                () -> new ActivationRequest(LocalDate.now(), -1));
    }
}