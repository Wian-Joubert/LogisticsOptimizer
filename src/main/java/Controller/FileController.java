package Controller;

import Model.Place;
import Model.Product;
import Model.Vehicle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class FileController {
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String STATIC_KEY = "1234567890123456"; // 16-byte key for AES-128 for testing

    String projectDir = System.getProperty("user.dir");
    String resourcesPath = projectDir + "/src/main/resources/";

    // Generate a static key (Replace this with secure key retrieval)
    private SecretKey getKey() {
        return new SecretKeySpec(STATIC_KEY.getBytes(), ALGORITHM);
    }

    // Encrypt the API key
    private String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt the API key
    private String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // Read the encrypted API key from the config file
    public String readApiKey() {
        try (FileInputStream fis = new FileInputStream(resourcesPath + "api_key.bin")) {
            byte[] data = fis.readAllBytes();
            String encryptedKey = new String(data);
            SecretKey key = getKey();
            logger.info("API Key read successfully.");
            return decrypt(encryptedKey, key);
        } catch (Exception ex) {
            logger.error("Error reading API File: {}", ex.getMessage());
            return null;
        }
    }

    // Write the encrypted API key to the config file
    public void writeApiKey(String apiKey) {
        try (FileOutputStream fos = new FileOutputStream(resourcesPath + "api_key.bin")) {
            SecretKey key = getKey();
            String encryptedKey = encrypt(apiKey, key);
            fos.write(encryptedKey.getBytes());
            logger.info("API Key written successfully.");
            JOptionPane.showMessageDialog(null, "API Key saved successfully.");
        } catch (Exception ex) {
            logger.error("Error writing API File: {}", ex.getMessage());
        }
    }

    public void saveProductList(ArrayList<Product> products, String input, boolean report) {
        try (FileOutputStream fos = new FileOutputStream(resourcesPath + "Products/" + input + ".txt")) {
            for (Product product : products) {
                fos.write((product.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
            logger.info("Product List {} written successfully.", input);
            if (report) {
                JOptionPane.showMessageDialog(null, String.format("Product List %s saved successfully.", input));
            }
        } catch (Exception ex) {
            logger.error("Error writing Product List {}: {}", input, ex.getMessage());
        }
    }

    public void saveRouteList(ArrayList<Place> places, String input, boolean report) {
        try (FileOutputStream fos = new FileOutputStream(resourcesPath + "Routes/" + input + ".txt")) {
            for (Place place : places) {
                fos.write((place.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
            logger.info("Route List {} written successfully.", input);
            if (report) {
                JOptionPane.showMessageDialog(null, String.format("Route List %s saved successfully.", input));
            }
        } catch (Exception ex) {
            logger.error("Error writing Route List {}: {}", input, ex.getMessage());
        }
    }

    public void saveVehicle(Vehicle vehicle, String input, boolean report) {
        try (FileOutputStream fos = new FileOutputStream(resourcesPath + "Vehicles/" + input + ".txt")) {
                fos.write((vehicle.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            logger.info("Vehicle {} written successfully.", input);
            if (report) {
                JOptionPane.showMessageDialog(null, String.format("Vehicle %s saved successfully.", input));
            }
        } catch (Exception ex) {
            logger.error("Error writing Vehicle {}: {}", input, ex.getMessage());
        }
    }

    public ArrayList<Product> loadProductList(String input) {
        ArrayList<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resourcesPath + "Products/" + input))) {
            String line;
            while ((line = br.readLine()) != null) {
                Product product = getProduct(line);
                products.add(product);
            }
            logger.info("Product List {} loaded successfully.", input);
        } catch (Exception ex) {
            logger.error("Error loading Product List {}: {}", input, ex.getMessage());
        }
        return products;
    }

    private Product getProduct(String line) {
        String[] parts = line.split(", ");
        String name = parts[0].substring(parts[0].indexOf('\'') + 1, parts[0].lastIndexOf('\''));
        String currency = parts[1].substring(parts[1].indexOf('\'') + 1, parts[1].lastIndexOf('\''));
        double value = Double.parseDouble(parts[2].split("=")[1]);
        double weight = Double.parseDouble(parts[3].split("=")[1]);
        double length = Double.parseDouble(parts[4].split("=")[1]);
        double width = Double.parseDouble(parts[5].split("=")[1]);
        double height = Double.parseDouble(parts[6].split("=")[1]);
        return new Product(name, currency, value, weight, length, width, height);
    }

    public ArrayList<Place> loadRouteList(String input) {
        ArrayList<Place> places = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resourcesPath + "Routes/" + input))) {
            String line;
            while ((line = br.readLine()) != null) {
                Place place = getPlace(line);
                places.add(place);
            }
            logger.info("Route List {} loaded successfully.", input);
        } catch (Exception ex) {
            logger.error("Error loading Route List {}: {}", input, ex.getMessage());
        }
        return places;
    }

    private Place getPlace(String line) {
        String[] parts = line.split(", ");
        String street = parts[0].substring(parts[0].indexOf('\'') + 1, parts[0].lastIndexOf('\''));
        String town = parts[1].substring(parts[1].indexOf('\'') + 1, parts[1].lastIndexOf('\''));
        String city = parts[2].substring(parts[2].indexOf('\'') + 1, parts[2].lastIndexOf('\''));
        String postcode = parts[3].substring(parts[3].indexOf('\'') + 1, parts[3].lastIndexOf('\''));
        return new Place(street, town, city, postcode);
    }

    public Vehicle loadVehicle(String input) {
        Vehicle vehicle = null;
        try (BufferedReader br = new BufferedReader(new FileReader(resourcesPath + "Vehicles/" + input))) {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.split(", ");
                int fuelConsumption = Integer.parseInt(parts[0].split("=")[1]);
                double maxWeight = Double.parseDouble(parts[1].split("=")[1]);
                double conLength = Double.parseDouble(parts[2].split("=")[1]);
                double conWidth = Double.parseDouble(parts[3].split("=")[1]);
                double conHeight = Double.parseDouble(parts[4].split("=")[1]);
                vehicle = new Vehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
            }
            logger.info("Vehicle {} loaded successfully.", input);
        } catch (Exception ex) {
            logger.error("Error loading Vehicle {}: {}", input, ex.getMessage());
        }
        return vehicle;
    }

    public void saveCosts(String currency, double hourlyRate, double fuelCost) {
        StringBuilder builder = new StringBuilder();
        builder.append(currency);
        builder.append(", ").append(hourlyRate);
        builder.append(", ").append(fuelCost);
        try (FileOutputStream fos = new FileOutputStream(resourcesPath + "defaultCosts.txt")) {
            fos.write((builder + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            logger.info("Costs File written successfully.");
                JOptionPane.showMessageDialog(null, "Costs File saved successfully.");
        } catch (Exception ex) {
            logger.error("Error writing Costs File: {}", ex.getMessage());
            JOptionPane.showMessageDialog(null, "Costs File was not Saved.", "File Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String[] loadCosts() {
        String[] costs = new String[3];
        try (BufferedReader reader = new BufferedReader(new FileReader(resourcesPath + "defaultCosts.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",\\s*");
                if (parts.length == 3) {
                    costs[0] = parts[0]; // Currency
                    costs[1] = parts[1]; // Hourly Rate
                    costs[2] = parts[2]; // Fuel Cost
                } else {
                    logger.warn("Unexpected format in Costs File: {}", line);
                }
                logger.info("Costs read successfully.");
            }
        } catch (IOException ex) {
            logger.error("Error reading Costs File: {}", ex.getMessage());
            JOptionPane.showMessageDialog(null, "Costs File could not be loaded.", "File Load Error", JOptionPane.ERROR_MESSAGE);
        }
        return costs;
    }

}
