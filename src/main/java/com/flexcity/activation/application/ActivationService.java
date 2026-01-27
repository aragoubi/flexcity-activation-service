package com.flexcity.activation.application;

import com.flexcity.activation.domain.ActivationRequest;
import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.domain.SelectedAsset;
import com.flexcity.activation.ports.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivationService {

    private final AssetRepository assetRepository;
    private final AssetSelectionService assetSelectionService;

    public ActivationService(AssetRepository assetRepository, AssetSelectionService assetSelectionService) {
        this.assetRepository = assetRepository;
        this.assetSelectionService = assetSelectionService;
    }

    public List<SelectedAsset> handleActivation(ActivationRequest request) {
        List<Asset> allAssets = assetRepository.findAll();
        return assetSelectionService.selectAssets(
                allAssets,
                request.date(),
                request.requestedVolumeKw()
        );
    }
}