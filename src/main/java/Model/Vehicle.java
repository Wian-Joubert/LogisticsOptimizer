package Model;

public class Vehicle {
    private final int fuelConsumption;
    private final int maxWeight;
    private final int conLength;
    private final int conWidth;
    private final int conHeight;
    private final int conVolume;

    public Vehicle(int fuelConsumption, int maxWeight, int conLength, int conWidth, int conHeight) {
        this.fuelConsumption = fuelConsumption;
        this.maxWeight = maxWeight;
        this.conLength = conLength;
        this.conWidth = conWidth;
        this.conHeight = conHeight;
        this.conVolume = (conLength/100) * (conWidth/100) * (conHeight/100);
    }

    public int getFuelConsumption() {
        return fuelConsumption;
    }
    public int getMaxWeight() {
        return maxWeight;
    }
    public int getConLength() {
        return conLength;
    }
    public int getConWidth() {
        return conWidth;
    }
    public int getConHeight() {
        return conHeight;
    }
    public int getConVolume() {
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
