package com.flexcity.activation.ports;

import com.flexcity.activation.domain.Asset;

import java.util.List;

public interface AssetRepository {
    List<Asset> findAll();
}