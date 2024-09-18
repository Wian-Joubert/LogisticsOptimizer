package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Vehicle {
    private final int fuelConsumption;
    private final double maxWeight;
    private final double conLength;
    private final double conWidth;
    private final double conHeight;
    private final BigDecimal conVolume;

    public Vehicle(int fuelConsumption, double maxWeight, double conLength, double conWidth, double conHeight) {
        this.fuelConsumption = fuelConsumption;
        this.maxWeight = maxWeight;
        this.conLength = conLength;
        this.conWidth = conWidth;
        this.conHeight = conHeight;
        this.conVolume = new BigDecimal(conLength * conWidth * conHeight).setScale(3, RoundingMode.HALF_UP);
    }

    public int getFuelConsumption() {
        return fuelConsumption;
    }
    public double getMaxWeight() {
        return maxWeight;
    }
    public double getConLength() {
        return conLength;
    }
    public double getConWidth() {
        return conWidth;
    }
    public double getConHeight() {
        return conHeight;
    }
    public BigDecimal getConVolume() {
        return conVolume;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "fuelConsumption=" + fuelConsumption +
                ", maxWeight=" + maxWeight +
                ", conLength=" + conLength +
                ", conWidth=" + conWidth +
                ", conHeight=" + conHeight +
                ", conVolume=" + conVolume +
                '}';
    }
}
