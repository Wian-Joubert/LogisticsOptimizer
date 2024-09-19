package Controller;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class RouteController {
    private final Logger logger = LoggerFactory.getLogger(RouteController.class);
    double[][] objectiveMatrix;  // Changed to a 2D array

    public void calculateTraveling(DistanceMatrix distanceMatrix) {
        try {
            if (distanceMatrix == null) {
                throw new RuntimeException("Distance Matrix is Null.");
            }
            // Convert DistanceMatrix to 2D array of doubles
            objectiveMatrix = distanceMatrixTo2DArray(distanceMatrix);

            // Log the matrix for debugging
            logger.info("Distance matrix calculated:");
            for (int i = 0; i < objectiveMatrix.length; i++) {
                for (int j = 0; j < objectiveMatrix[i].length; j++) {
                    logger.info("Distance[{}][{}] = {}", i, j, objectiveMatrix[i][j]);
                }
            }


        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Calculation Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Route Calculation Error: {}", ex.getMessage());
        }
    }

    /**
     * Generates a 2D matrix with constraints for arriving at each city exactly once.
     * <p>
     * The outer loop (i) iterates over each city as the destination city.
     * The inner loop (j) iterates over each possible departure city.
     * For each pair (i, j), the corresponding position in the constraintsMatrix is set to 1.
     * This ensures that every city (j) is reached exactly once from all other cities (i).
     */
    private double[][] arriveConstraints(double[][] objectiveMatrix) {
        int rowElements = objectiveMatrix[0].length; // number of cities (rows in the matrix)
        int totalElements = getTotalElements(objectiveMatrix); // total number of variables in the matrix
        double[][] constraintsMatrix = new double[rowElements][totalElements];

        // Populate the constraints for arriving in a city once
        for (int i = 0; i < rowElements; i++) {
            for (int j = 0; j < rowElements; j++) {
                constraintsMatrix[j][i * rowElements + j] = 1; // Set `1` for arrival constraints
            }
        }
        return constraintsMatrix;
    }

    /**
     * Generates a 2D matrix with constraints for leaving each city exactly once.
     * <p>
     * The outer loop (i) iterates over each city as the departure city.
     * The inner loop (j) iterates over each possible destination city.
     * For each pair (i, j), the corresponding position in the constraintsMatrix is set to 1.
     * This ensures that every city (i) is departed from exactly once to all other cities (j).
     */
    private double[][] leaveConstraints(double[][] objectiveMatrix) {
        int rowElements = objectiveMatrix[0].length; // number of cities (rows in the matrix)
        int totalElements = getTotalElements(objectiveMatrix); // total number of variables in the matrix
        double[][] constraintsMatrix = new double[rowElements][totalElements];

        // Populate the constraints for leaving a city once
        for (int i = 0; i < rowElements; i++) {
            for (int j = 0; j < rowElements; j++) {
                constraintsMatrix[i][i * rowElements + j] = 1; // Set `1` for departure constraints
            }
        }
        return constraintsMatrix;
    }


//    private double[][] subTourConstraints(double[][] objectiveMatrix){
//        int totalElements = getTotalElements(objectiveMatrix);
//    }

    private double[][] distanceMatrixTo2DArray(DistanceMatrix distanceMatrix) {
        int numRows = distanceMatrix.rows.length;
        int numCols = distanceMatrix.rows[0].elements.length;
        double[][] distances = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            DistanceMatrixRow row = distanceMatrix.rows[i];
            for (int j = 0; j < numCols; j++) {
                DistanceMatrixElement element = row.elements[j];
                double distanceInKm = getDistanceInKm(element);

                distances[i][j] = distanceInKm;
            }
        }
        return distances;
    }

    private static double getDistanceInKm(DistanceMatrixElement element) {
        String humanReadable = element.distance.humanReadable;
        double distanceInKm = 0.0;

        if (humanReadable.endsWith("m")) {
            // Handle meters: if 1m or less, set to 50000 km
            double distanceInMeters = Double.parseDouble(humanReadable.replace(" m", ""));
            distanceInKm = (distanceInMeters <= 1) ? 50000.0 : distanceInMeters / 1000.0;
        } else if (humanReadable.endsWith("km")) {
            // Handle kilometers directly
            distanceInKm = Double.parseDouble(humanReadable.replace(" km", ""));
        }
        return distanceInKm;
    }

    private int getTotalElements(double[][] objectiveMatrix) {
        int numRows = objectiveMatrix.length;
        int numCols = objectiveMatrix[0].length;
        return numRows * numCols;
    }
}
