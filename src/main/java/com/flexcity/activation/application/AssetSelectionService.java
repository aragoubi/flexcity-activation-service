package com.flexcity.activation.application;

import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.domain.InsufficientCapacityException;
import com.flexcity.activation.domain.SelectedAsset;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AssetSelectionService {

    public List<SelectedAsset> selectAssets(List<Asset> assets, LocalDate date, int requestedVolumeKw) {

        // 1) Keep only assets activatable at 'date'
        List<Asset> eligibleAssets = new ArrayList<>();
        for (Asset a : assets) {
            if (a.canBeActivatedAt(date)) {
                eligibleAssets.add(a);
            }
        }

        // If nothing eligible, impossible
        if (eligibleAssets.isEmpty()) return List.of();


        int sumVolumes = 0;
        for (Asset asset : eligibleAssets) sumVolumes += asset.maxVolumeKw();

        // cannot reach the requested volume
        if (requestedVolumeKw > sumVolumes) {
            throw new InsufficientCapacityException(
                    "Insufficient capacity: requested " + requestedVolumeKw +
                            " kW but only " + sumVolumes + " kW available on " + date
            );
        }

        double[] dp = new double[sumVolumes + 1];
        int[] choice = new int[sumVolumes + 1];
        int[] prev = new int[sumVolumes + 1];

        Arrays.fill(dp, Double.POSITIVE_INFINITY);
        Arrays.fill(choice, -1);
        Arrays.fill(prev, -1);
        dp[0] = 0.0;

        for (int indexAsset = 0; indexAsset < eligibleAssets.size(); indexAsset++) {
            Asset asset = eligibleAssets.get(indexAsset);

            for (int v = sumVolumes; v >= 0; v--) {
                if (!Double.isInfinite(dp[v])) {
                    if (v + asset.maxVolumeKw() <= sumVolumes) {
                        int newV = v + asset.maxVolumeKw();
                        double newCost = dp[v] + asset.activationCostEur();

                        if (newCost < dp[newV]) {
                            dp[newV] = newCost;
                            choice[newV] = indexAsset;
                            prev[newV] = v;
                        }
                    }
                }
            }
        }

        int bestV = -1;
        double bestCost = Double.POSITIVE_INFINITY;

        for(int tv=requestedVolumeKw; tv <= sumVolumes; tv++)
        {
            if(dp[tv] < bestCost) {
                bestCost = dp[tv];
                bestV = tv;
            }
        }
        if (bestV == -1 || Double.isInfinite(bestCost)) return List.of();

        List<SelectedAsset> selected = new ArrayList<>();
        int v = bestV;
        while (v > 0) {
            int idx = choice[v];
            Asset a = eligibleAssets.get(idx);
            selected.add(new SelectedAsset(a.code(), a.maxVolumeKw(), a.activationCostEur()));
            v = prev[v];
        }

        return selected;
    }
}
