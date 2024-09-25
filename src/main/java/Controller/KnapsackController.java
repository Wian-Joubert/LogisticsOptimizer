package Controller;

import Model.KSModel;
import Model.Product;
import Model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class KnapsackController {
    public KSModel calculateKnapsack(ArrayList<Product> products, Vehicle vehicle) {
        int n = products.size();
        double maxWeight = vehicle.getMaxWeight();
        BigDecimal maxVolume = vehicle.getConVolume();

        // DP table to store maximum value achievable with given weight and volume
        double[][] dp = new double[n + 1][(int) maxWeight + 1];

        for (int i = 1; i <= n; i++) {
            Product product = products.get(i - 1);
            double weight = product.getWeight();
            double volume = product.getVolume();
            double value = product.getValue();

            for (int w = (int) maxWeight; w >= weight; w--) {
                for (int v = maxVolume.intValue(); v >= volume; v--) {
                    if (w >= weight && v >= volume) {
                        dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - (int) weight] + value);
                    }
                }
            }
        }

        // Backtrack to find the products and their quantities that were included
        double maxValue = dp[n][(int) maxWeight];
        HashMap<Product, Integer> productQuantities = new HashMap<>();

        for (int i = n, w = (int) maxWeight; i > 0 && maxValue > 0; i--) {
            if (maxValue != dp[i - 1][w]) {
                Product product = products.get(i - 1);
                productQuantities.put(product, productQuantities.getOrDefault(product, 0) + 1);
                maxValue -= product.getValue();
                w -= (int) product.getWeight();
            }
        }

        // Create the KSModel with the total value and the product quantities
        KSModel ksModel = new KSModel(dp[n][(int) maxWeight], productQuantities);
        System.out.println(ksModel);
        return ksModel;
    }
}
