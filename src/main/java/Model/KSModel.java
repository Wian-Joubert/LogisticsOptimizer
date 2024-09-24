package Model;

import java.util.ArrayList;
import java.util.HashMap;

public class KSModel {
    private final double maxValue;
    private final HashMap<Product, Integer> productQuantities;

    // Constructor with max value and product quantities
    public KSModel(double maxValue, HashMap<Product, Integer> productQuantities) {
        this.maxValue = maxValue;
        this.productQuantities = productQuantities;
    }

    // Get maximum value achieved
    public double getMaxValue() {
        return maxValue;
    }

    // Get product quantities map
    public HashMap<Product, Integer> getProductQuantities() {
        return productQuantities;
    }

    // Override toString for custom output
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Value Achieved: ").append(maxValue).append("\n");
        sb.append("Selected Products:\n");
        for (Product product : productQuantities.keySet()) {
            int quantity = productQuantities.get(product);
            sb.append("- ").append(product.getName())
                    .append(", Units: ").append(quantity)
                    .append(", Total Value: ").append(product.getValue() * quantity).append("\n");
        }
        return sb.toString();
    }
}
