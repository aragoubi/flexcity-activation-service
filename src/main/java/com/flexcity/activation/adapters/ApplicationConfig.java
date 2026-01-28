package com.flexcity.activation.adapters;

import com.flexcity.activation.application.ActivationService;
import com.flexcity.activation.application.AssetSelectionService;
import com.flexcity.activation.ports.AssetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public AssetSelectionService assetSelectionService() {
        return new AssetSelectionService();
    }

    @Bean
    public ActivationService activationService(AssetRepository assetRepository,
                                                AssetSelectionService assetSelectionService) {
        return new ActivationService(assetRepository, assetSelectionService);
    }
}
