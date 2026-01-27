package com.flexcity.activation.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ActivationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSelectedAssets() throws Exception {
        String requestBody = """
                {
                    "date": "2024-06-01",
                    "requestedVolumeKw": 1000
                }
                """;

        mockMvc.perform(post("/api/activations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedAssets").isArray())
                .andExpect(jsonPath("$.selectedAssets[0].assetCode").exists())
                .andExpect(jsonPath("$.selectedAssets[0].selectedVolumeKw").exists())
                .andExpect(jsonPath("$.selectedAssets[0].activationCostEur").exists());
    }

    @Test
    void shouldReturn400ForMissingDate() throws Exception {
        String requestBody = """
                {
                    "requestedVolumeKw": 1000
                }
                """;

        mockMvc.perform(post("/api/activations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForNegativeVolume() throws Exception {
        String requestBody = """
                {
                    "date": "2024-06-01",
                    "requestedVolumeKw": -100
                }
                """;

        mockMvc.perform(post("/api/activations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn422ForInsufficientCapacity() throws Exception {
        String requestBody = """
                {
                    "date": "2024-06-01",
                    "requestedVolumeKw": 999999
                }
                """;

        mockMvc.perform(post("/api/activations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }
}