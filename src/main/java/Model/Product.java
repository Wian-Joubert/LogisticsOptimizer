package Model;

public class Product {
    private final String name;
    private final String currency;
    private final double value;
    private final int weight;
    private final int length;
    private final int width;
    private final int height;
    private final int volume;

    public Product(String name, String currency, double value, int weight, int length, int width, int height){
        this.name = name;
        this.currency = currency;
        this.value = value;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.volume = length * width * height;
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
    public int getWeight() {
        return weight;
    }
    public int getLength() {
        return length;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getVolume() {
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
