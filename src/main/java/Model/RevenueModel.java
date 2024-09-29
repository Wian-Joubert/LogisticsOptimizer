package Model;

public class RevenueModel {
    final double totalValue;
    final double totalDistance;
    final double totalDuration;
    final int fuelConsumption;
    final double fuelUsed;
    final double totalFuelCost;
    final double totalEmployeeCost;
    final double totalShippingCost;
    final double profit;

    public RevenueModel(double totalValue, double totalDistance, double totalDuration,
                        int fuelConsumption, double fuelUsed, double totalFuelCost,
                        double totalEmployeeCost, double totalShippingCost, double profit) {
        this.totalValue = totalValue;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.fuelConsumption = fuelConsumption;
        this.fuelUsed = fuelUsed;
        this.totalFuelCost = totalFuelCost;
        this.totalEmployeeCost = totalEmployeeCost;
        this.totalShippingCost = totalShippingCost;
        this.profit = profit;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public int getFuelConsumption() {
        return fuelConsumption;
    }

    public double getFuelUsed() {
        return fuelUsed;
    }

    public double getTotalFuelCost() {
        return totalFuelCost;
    }

    public double getTotalEmployeeCost() {
        return totalEmployeeCost;
    }

    public double getTotalShippingCost() {
        return totalShippingCost;
    }

    public double getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return "RevenueModel{" +
                "totalValue=" + totalValue +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", fuelConsumption=" + fuelConsumption +
                ", fuelUsed=" + fuelUsed +
                ", totalFuelCost=" + totalFuelCost +
                ", totalEmployeeCost=" + totalEmployeeCost +
                ", totalShippingCost=" + totalShippingCost +
                ", profit=" + profit +
                '}';
    }
}
