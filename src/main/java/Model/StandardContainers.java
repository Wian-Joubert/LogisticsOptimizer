package Model;

public enum StandardContainers {
    DRY_10FT(2.991, 2.438, 2.591),
    DRY_20FT(6.058, 2.438, 2.591),
    DRY_40FT(12.192, 2.438, 2.591),
    REFRIGERATED_20FT(6.058, 2.438, 2.591),
    REFRIGERATED_40FT(12.192, 2.438, 2.591),
    OPEN_TOP_20FT(6.058, 2.438, 2.591),
    OPEN_TOP_40FT(12.192, 2.438, 2.591),
    HIGH_CUBE_40FT(12.192, 2.438, 2.896),
    HIGH_CUBE_45FT(13.716, 2.438, 2.896),
    BOX_TRUCK_14FT(4.11, 1.98, 1.96),
    BOX_TRUCK_20FT(6.02, 1.83, 1.98),
    BOX_TRUCK_22FT(6.68, 2.46, 2.46),
    BOX_TRUCK_24FT(7.26, 2.26, 2.46),
    BOX_TRUCK_26FT(7.90, 2.46, 2.46);


    private final double length;
    private final double width;
    private final double height;

    StandardContainers(double length, double width, double height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public static StandardContainers getContainerByName(String selectedContainer) {
        return switch (selectedContainer) {
            case "10ft Dry Container" -> StandardContainers.DRY_10FT;
            case "20ft Dry Container" -> StandardContainers.DRY_20FT;
            case "40ft Dry Container" -> StandardContainers.DRY_40FT;
            case "20ft Refrigerated Container" -> StandardContainers.REFRIGERATED_20FT;
            case "40ft Refrigerated Container" -> StandardContainers.REFRIGERATED_40FT;
            case "20ft Open Top Container" -> StandardContainers.OPEN_TOP_20FT;
            case "40ft Open Top Container" -> StandardContainers.OPEN_TOP_40FT;
            case "40ft High Cube Container" -> StandardContainers.HIGH_CUBE_40FT;
            case "45ft High Cube Container" -> StandardContainers.HIGH_CUBE_45FT;
            case "14ft Box Truck" -> StandardContainers.BOX_TRUCK_14FT;
            case "20ft Box Truck" -> StandardContainers.BOX_TRUCK_20FT;
            case "22ft Box Truck" -> StandardContainers.BOX_TRUCK_22FT;
            case "24ft Box Truck" -> StandardContainers.BOX_TRUCK_24FT;
            case "26ft Box Truck" -> StandardContainers.BOX_TRUCK_26FT;
            default -> null;
        };
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
}
