package Model;

import java.util.ArrayList;

public class KSModel {
    private final double maxValue;
    private final ArrayList<Product> selectedProducts;

    public KSModel(double maxValue, ArrayList<Product> selectedProducts) {
        this.maxValue = maxValue;
        this.selectedProducts = selectedProducts;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public ArrayList<Product> getSelectedProducts() {
        return selectedProducts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Maximum Value: ").append(maxValue).append("\n");
        sb.append("Selected Products:\n");
        for (Product product : selectedProducts) {
            sb.append("- ").append(product.getName()).append("\n");
        }
        return sb.toString();
    }
}
