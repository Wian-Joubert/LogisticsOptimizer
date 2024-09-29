package Controller;

import Model.KSModel;
import Model.RevenueModel;
import Model.TSModel;
import Model.Vehicle;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class RevenueController {
    private final Logger logger = LoggerFactory.getLogger(RevenueController.class);
    private TSModel tsModel;
    final FileController fileController = new FileController();

    public RevenueModel calculateRevenue(KSModel ksModel, TSModel tsModel, Vehicle vehicle) {
        this.tsModel = tsModel;
        String[] defaultCosts = fileController.loadCosts();
        double fuelCostPerLiter = Double.parseDouble(defaultCosts[2]);
        double employeeHourlyPay = Double.parseDouble(defaultCosts[1]);

        try {
            // Retrieve the total value of products from KSModel
            double totalValue = ksModel.getMaxValue();

            // Retrieve the total distance from TSModel
            double totalDistance = tsModel.getObjectiveValue(); // Assuming this is in kilometers

            // Get vehicle's fuel consumption (km per liter)
            int fuelConsumption = vehicle.getFuelConsumption();

            // Calculate total fuel used
            double fuelUsed = totalDistance / fuelConsumption; // in liters

            // Calculate total fuel cost based on the set fuel cost per liter
            double totalFuelCost = fuelUsed * fuelCostPerLiter;

            // Calculate employee cost based on the hours worked and hourly pay
            double estimatedWorkHours = calculateHours();
            double totalEmployeeCost = estimatedWorkHours * employeeHourlyPay;

            // Calculate total cost (fuel + employee costs)
            double totalShippingCost = totalFuelCost + totalEmployeeCost;

            // Calculate profit (total value of products minus shipping cost)
            double profit = totalValue - totalShippingCost;

            // Output the results
            System.out.println("Total Value of Products: " + totalValue);
            System.out.println("Total Distance: " + totalDistance + " km");
            System.out.println("Fuel Used: " + fuelUsed + " liters");
            System.out.println("Total Fuel Cost: " + totalFuelCost);
            System.out.println("Total Employee Cost: " + totalEmployeeCost);
            System.out.println("Total Shipping Cost: " + totalShippingCost);
            System.out.println("Profit: " + profit);

            return new RevenueModel(totalValue, totalDistance, estimatedWorkHours, fuelConsumption, fuelUsed, totalFuelCost, totalEmployeeCost, totalShippingCost, profit);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Revenue Calculation Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Revenue Calculation Error: {}", ex.getMessage());
            return null;
        }
    }

    private Double calculateHours() {
        DistanceMatrix distanceMatrix = tsModel.getDistanceMatrix();
        int[] decisionArray = tsModel.getDecisionArray();
        double[][] durations = distanceMatrixToDuration2DArray(distanceMatrix);
        double totalDuration = 0.0; // Total duration in seconds

        int currentLocation = 0; // Corresponds to L1
        boolean[] visited = new boolean[durations.length]; // Track visited locations
        visited[currentLocation] = true;

        // Decode the route based on the decision array
        for (int j = 0; j < durations.length; j++) {
            int index = currentLocation * durations.length + j;

            // Check if there's a connection from currentLocation to j
            if (index < decisionArray.length && decisionArray[index] == 1 && !visited[j]) {
                // Get the duration from the durations array
                double duration = durations[currentLocation][j]; // Get duration in seconds
                totalDuration += duration;
                visited[j] = true;
                currentLocation = j;
                j = -1;
            }
        }
        return totalDuration / 3600.0;
    }

    // Method to convert DistanceMatrix to a 2D array of durations in seconds
    private double[][] distanceMatrixToDuration2DArray(DistanceMatrix distanceMatrix) throws RuntimeException {
        try {
            int numRows = distanceMatrix.rows.length;
            int numCols = distanceMatrix.rows[0].elements.length;
            double[][] durations = new double[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                DistanceMatrixRow row = distanceMatrix.rows[i];
                for (int j = 0; j < numCols; j++) {
                    DistanceMatrixElement element = row.elements[j];
                    double durationInSeconds = getDurationInSeconds(element);
                    durations[i][j] = durationInSeconds;
                }
            }
            return durations;
        } catch (RuntimeException ex) {
            throw new RuntimeException("Error Converting Distance Matrix to Duration Matrix: " + ex.getMessage(), ex);
        }
    }

    // Method to get duration in seconds from a DistanceMatrixElement
    private static double getDurationInSeconds(DistanceMatrixElement element) throws RuntimeException {
        try {
            // Extract the duration in seconds
            return element.duration.inSeconds; // Assuming the element has a duration field with duration in seconds
        } catch (Exception ex) {
            throw new RuntimeException("Error extracting duration from Distance Matrix Element: " + ex.getMessage(), ex);
        }
    }
}
