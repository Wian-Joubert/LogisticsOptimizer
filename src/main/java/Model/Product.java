package Model;

public class Product {
    private final String name;
    private final String currency;
    private final double value;
    private final double weight;
    private final double length;
    private final double width;
    private final double height;
    private final double volume;

    public Product(String name, String currency, double value, double weight, double length, double width, double height){
        this.name = name;
        this.currency = currency;
        this.value = value;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.volume = (length/100) * (width/100) * (height/100);
    }

    public String getName() {
        return name;
    }
    public String getCurrency() {
        return currency;
    }
    public double getValue() {
        return value;
    }
    public double getWeight() {
        return weight;
    }
    public double getLength() {
        return length;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public double getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", value=" + value +
                ", weight=" + weight +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", volume=" + volume +
                '}';
    }
}
