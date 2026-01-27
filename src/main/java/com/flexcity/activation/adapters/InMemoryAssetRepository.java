package com.flexcity.activation.adapters;

import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.ports.AssetRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public class InMemoryAssetRepository implements AssetRepository {

    private static final List<Asset> ASSETS = List.of(
            new Asset("SOLAR_01", "Solar Park Alpha", 45.0,
                    Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2), LocalDate.of(2024, 6, 3)),
                    1200),
            new Asset("WIND_01", "Wind Farm Beta", 62.5,
                    Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2)),
                    800),
            new Asset("BATTERY_01", "Battery Storage Gamma", 120.0,
                    Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2), LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 4)),
                    500),
            new Asset("HYDRO_01", "Hydro Plant Delta", 38.0,
                    Set.of(LocalDate.of(2024, 6, 2), LocalDate.of(2024, 6, 3)),
                    2000),
            new Asset("SOLAR_02", "Solar Park Epsilon", 52.0,
                    Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 4)),
                    950),
            new Asset("WIND_02", "Wind Farm Zeta", 58.0,
                          Set.of(LocalDate.of(2024, 6, 2), LocalDate.of(2024, 6, 3)),
            1100),
            new Asset("BATTERY_02", "Battery Storage Eta", 95.0,
                      Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 3)),
            300),
            new Asset("GAS_01", "Gas Turbine Theta", 150.0,
                      Set.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2),
                   LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 4)),
                           2500)
    );

    @Override
    public List<Asset> findAll() {
        return ASSETS;
    }
}