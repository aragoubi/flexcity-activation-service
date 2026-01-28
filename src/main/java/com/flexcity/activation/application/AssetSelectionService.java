package com.flexcity.activation.application;

import com.flexcity.activation.domain.Asset;
import com.flexcity.activation.domain.InsufficientCapacityException;
import com.flexcity.activation.domain.SelectedAsset;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for selecting the optimal combination of assets
 * to meet a requested activation volume at minimum cost.
 *
 * Uses a dynamic programming approach to find the cheapest subset
 *  of assets whose total volume >= requested volume.
 */
public class AssetSelectionService {

    /**
     * Selects assets to activate for a given date and requested volume.
     *
     * @param assets           all available assets in the system
     * @param date             the activation date (assets must be available on this date)
     * @param requestedVolumeKw the minimum volume required (in kW)
     * @return list of selected assets with their allocated volumes and costs
     * @throws InsufficientCapacityException if total available capacity is less than requested
     */
    public List<SelectedAsset> selectAssets(List<Asset> assets, LocalDate date, int requestedVolumeKw) {

        // Step 1: Filter assets available on the requested date
        List<Asset> eligibleAssets = new ArrayList<>();
        for (Asset a : assets) {
            if (a.canBeActivatedAt(date)) {
                eligibleAssets.add(a);
            }
        }

        // No assets available on this date
        if (eligibleAssets.isEmpty()) {
            return List.of();
        }

        // Step 2: Calculate total available capacity
        int sumVolumes = 0;
        for (Asset asset : eligibleAssets) {
            sumVolumes += asset.maxVolumeKw();
        }

        // Validate that we can meet the requested volume
        if (requestedVolumeKw > sumVolumes) {
            throw new InsufficientCapacityException(
                    "Insufficient capacity: requested " + requestedVolumeKw +
                            " kW but only " + sumVolumes + " kW available on " + date
            );
        }

        // dp[v] = minimum cost to achieve exactly volume v
        // choice[v] = index of the asset used to reach volume v
        // prev[v] = previous volume before adding the asset (for backtracking)
        double[] dp = new double[sumVolumes + 1];
        int[] choice = new int[sumVolumes + 1];
        int[] prev = new int[sumVolumes + 1];

        // infinite cost for all volumes except 0
        Arrays.fill(dp, Double.POSITIVE_INFINITY);
        Arrays.fill(choice, -1);
        Arrays.fill(prev, -1);
        dp[0] = 0.0; // Zero cost to achieve zero volume


        // For each asset, update achievable volumes (iterate backwards to avoid reusing same asset)
        for (int indexAsset = 0; indexAsset < eligibleAssets.size(); indexAsset++) {
            Asset asset = eligibleAssets.get(indexAsset);

            for (int v = sumVolumes; v >= 0; v--) {
                if (!Double.isInfinite(dp[v])) {
                    int newV = v + asset.maxVolumeKw();
                    if (newV <= sumVolumes) {
                        double newCost = dp[v] + asset.activationCostEur();

                        // Update if this combination is cheaper
                        if (newCost < dp[newV]) {
                            dp[newV] = newCost;
                            choice[newV] = indexAsset;
                            prev[newV] = v;
                        }
                    }
                }
            }
        }

        // Find the minimum cost among all volumes >= requestedVolumeKw
        int bestV = -1;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int tv = requestedVolumeKw; tv <= sumVolumes; tv++) {
            if (dp[tv] < bestCost) {
                bestCost = dp[tv];
                bestV = tv;
            }
        }

        // No valid combination found
        if (bestV == -1 || Double.isInfinite(bestCost)) {
            return List.of();
        }

        // Backtrack to reconstruct the selected assets
        List<SelectedAsset> selected = new ArrayList<>();
        int v = bestV;
        while (v > 0) {
            int idx = choice[v];
            Asset a = eligibleAssets.get(idx);
            selected.add(new SelectedAsset(a.code(), a.maxVolumeKw(), a.activationCostEur()));
            v = prev[v]; // Move to previous volume state
        }

        return selected;
    }
}
