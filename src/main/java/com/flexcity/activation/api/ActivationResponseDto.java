package com.flexcity.activation.api;

import java.util.List;

public record ActivationResponseDto(
        List<SelectedAssetDto> selectedAssets
) {
}