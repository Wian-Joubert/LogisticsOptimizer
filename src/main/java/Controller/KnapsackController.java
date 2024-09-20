package Controller;

import Model.KSModel;
import Model.Product;
import Model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;

public class KnapsackController {
    public void calculateKnapsack(ArrayList<Product> products, Vehicle vehicle) {
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

        // Backtrack to find the products that were included
        double maxValue = dp[n][(int) maxWeight];
        ArrayList<Product> selectedProducts = new ArrayList<>();

        for (int i = n, w = (int) maxWeight; i > 0 && maxValue > 0; i--) {
            if (maxValue != dp[i - 1][w]) {
                Product product = products.get(i - 1);
                selectedProducts.add(product);
                maxValue -= product.getValue();
                w -= product.getWeight();
            }
        }

        KSModel ksModel = new KSModel(dp[n][(int) maxWeight], selectedProducts);
        System.out.println(ksModel.toString());
    }
}
