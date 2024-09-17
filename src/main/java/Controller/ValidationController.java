package Controller;

import Model.ValidationResult;

public class ValidationController {
    public ValidationResult validateProduct(String name, String currency, double value, int weight, int length, int width, int height){
        if (name.isBlank() || name.isEmpty()){
            return new ValidationResult(false, "Product Name is Empty.");
        }
        if (currency.isBlank() || currency.isEmpty()){
            return new ValidationResult(false, "Product Currency is not selected");
        }
        if (value <= 0){
            return new ValidationResult(false, "Product Value cannot be <= 0.");
        }
        if (weight <= 0){
            return new ValidationResult(false, "Product Weight cannot be <= 0.");
        }
        if (length <= 0){
            return new ValidationResult(false, "Product Length cannot be <= 0.");
        }
        if (width <= 0){
            return new ValidationResult(false, "Product Width cannot be <= 0.");
        }
        if (height <= 0){
            return new ValidationResult(false, "Product Height cannot be <= 0.");
        }
        return new ValidationResult(true);
    }

    public ValidationResult validateVehicle(int fuelConsumption, int maxWeight, int conLength, int conWidth, int conHeight){
        if (fuelConsumption <= 0){
            return new ValidationResult(false, "Vehicle Fuel Consumption cannot be <= 0.");
        }
        if (maxWeight <= 0){
            return new ValidationResult(false, "Container Maximum Load cannot be <= 0.");
        }
        if (conLength <= 0){
            return new ValidationResult(false, "Container Length cannot be <= 0.");
        }
        if (conWidth <= 0){
            return new ValidationResult(false, "Container Width cannot be <= 0.");
        }
        if (conHeight <= 0){
            return new ValidationResult(false, "Container Height cannot be <= 0.");
        }
        return new ValidationResult(true);
    }

    public ValidationResult validatePlace(String street, String town, String city, String postcode){
        if (street.isEmpty() || street.isBlank()){
            return new ValidationResult(false, "Street cannot be Empty.");
        }
        String streetPattern = ".*\\d+.*\\s+.*\\s+(St|Ave|Rd|Blvd|Ln|Dr|Pl|Ct|Terr|Way|Pkwy|Cir)$";
        if (!street.matches(streetPattern)) {
            return new ValidationResult(false, "Street must contain a Number, Street Name, and a Valid Road Type.");
        }
        if (town != null && !town.isEmpty()) {
            String townCityPattern = "^[a-zA-Z\\s-]+$";
            if (!town.matches(townCityPattern)) {
                return new ValidationResult(false, "Town must contain a Town Name, Names may contain spaces and hyphens. Cannot contain a Number.");
            }
        }
        if (city != null && !city.isEmpty()) {
            String townCityPattern = "^[a-zA-Z\\s-]+$";
            if (!city.matches(townCityPattern)) {
                return new ValidationResult(false, "City must contain a City Name, Names may contain spaces and hyphens. Cannot contain a Number.");
            }
        }
        if (postcode.isEmpty() || postcode.isBlank()) {
            return new ValidationResult(false, "Post Code cannot be Empty.");
        }
        return new ValidationResult(true);
    }
}
