package com.flexcity.activation.api;

import com.flexcity.activation.application.ActivationService;
import com.flexcity.activation.domain.ActivationRequest;
import com.flexcity.activation.domain.SelectedAsset;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activations")
public class ActivationController {

    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @PostMapping
    public ResponseEntity<ActivationResponseDto> createActivation(
            @Valid @RequestBody ActivationRequestDto requestDto) {

        ActivationRequest domainRequest = new ActivationRequest(
                requestDto.date(),
                requestDto.requestedVolumeKw()
        );

        List<SelectedAsset> selectedAssets = activationService.handleActivation(domainRequest);

        List<SelectedAssetDto> assetDtos = selectedAssets.stream()
                .map(asset -> new SelectedAssetDto(
                        asset.assetCode(),
                        asset.selectedVolumeKw(),
                        asset.activationCostEur()
                ))
                .toList();

        return ResponseEntity.ok(new ActivationResponseDto(assetDtos));
    }
}