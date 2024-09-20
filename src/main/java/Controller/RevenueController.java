package Controller;

import Model.KSModel;
import Model.Product;
import Model.TSModel;
import Model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;

public class RevenueController {
    private final Logger logger = LoggerFactory.getLogger(RevenueController.class);

    public void calculateRevenue(KSModel ksModel, TSModel tsModel, Vehicle vehicle){
        try {
            // Retrieve the total value of products from KSModel
            double totalValue = ksModel.getMaxValue();

            // Retrieve the total distance from TSModel
            double totalDistance = tsModel.getObjectiveValue(); // Assuming this is in kilometers

            // Get vehicle's fuel consumption (km per liter)
            int fuelConsumption = vehicle.getFuelConsumption();

            // Calculate total fuel used
            double fuelUsed = totalDistance / fuelConsumption; // in liters

            // Assuming fuel cost is a constant (you can replace this with a parameter)
            double fuelCostPerLiter = 10.0; // Adjust this value based on your needs
            double totalFuelCost = fuelUsed * fuelCostPerLiter;

            // Calculate profit
            double profit = totalValue - totalFuelCost;

            // Output the results
            System.out.println("Total Value of Products: " + totalValue);
            System.out.println("Total Distance: " + totalDistance + " km");
            System.out.println("Fuel Used: " + fuelUsed + " liters");
            System.out.println("Total Fuel Cost: " + totalFuelCost);
            System.out.println("Profit: " + profit);

        } catch (RuntimeException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Revenue Calculation Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Revenue Calculation Error: {}", ex.getMessage());
        }
    }
}
