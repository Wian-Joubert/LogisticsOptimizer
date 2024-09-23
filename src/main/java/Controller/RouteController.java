package Controller;

import Model.TSModel;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class RouteController {
    private final Logger logger = LoggerFactory.getLogger(RouteController.class);
    double[][] objectiveMatrix;
    int[][] arriveMatrix;
    int[][] leaveMatrix;
    int[][] subTourMatrix;
    int[][] selfLoopMatrix;
    int numLocations;

    public void calculateTraveling(DistanceMatrix distanceMatrix) {
        try {
            if (distanceMatrix == null) {
                throw new RuntimeException("Distance Matrix is Null.");
            }

            // Create constraints
            objectiveMatrix = distanceMatrixTo2DArray(distanceMatrix);
            numLocations = objectiveMatrix.length;
            arriveMatrix = arriveConstraints(objectiveMatrix);
            leaveMatrix = leaveConstraints(objectiveMatrix);
            subTourMatrix = subTourConstraints(objectiveMatrix);  // Sub-tour matrix might be null
            selfLoopMatrix = selfLoopConstrains(objectiveMatrix);

            // Equalize lengths
            int largerMatrixLength = Math.max(arriveMatrix[0].length,
                    Math.max(leaveMatrix[0].length,
                            Math.max(selfLoopMatrix[0].length,
                                    subTourMatrix != null ? subTourMatrix[0].length : 0)));
            double[] flatObjective = flatten2DArray(objectiveMatrix);
            flatObjective = equalize1DArraySizes(flatObjective, largerMatrixLength);
            arriveMatrix = equalize2DArraySizes(arriveMatrix, largerMatrixLength);
            leaveMatrix = equalize2DArraySizes(leaveMatrix, largerMatrixLength);
            selfLoopMatrix = equalize2DArraySizes(selfLoopMatrix, largerMatrixLength);

            // If subTourMatrix is not null, equalize its size too
            if (subTourMatrix != null) {
                subTourMatrix = equalize2DArraySizes(subTourMatrix, largerMatrixLength);
            }

            // Construct signs and RHS
            String[] signsArray = constructSigns(arriveMatrix, leaveMatrix, subTourMatrix, selfLoopMatrix);
            int[] rhsArray = constructRHS(arriveMatrix, leaveMatrix, subTourMatrix, selfLoopMatrix, objectiveMatrix.length);

            // Merge arrays, excluding subTourMatrix if it's null
            int[][] subjectToMatrix = concatenateMatrices(arriveMatrix, leaveMatrix, subTourMatrix, selfLoopMatrix);

            TSModel tsModel = new TSModel(flatObjective, subjectToMatrix, signsArray, rhsArray, numLocations);
            System.out.println(tsModel);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Calculation Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Route Calculation Error: {}", ex.getMessage());
        }
    }

    /**
     * Generates a 2D matrix with constraints for arriving at each Location exactly once.
     * <p>
     * The outer loop (i) iterates over each Location as the destination.
     * The inner loop (j) iterates over each possible departure destination.
     * For each pair (i, j), the corresponding position in the constraintsMatrix is set to 1.
     * This ensures that every Location (j) is reached exactly once from all other Location (i).
     */
    private int[][] arriveConstraints(double[][] objectiveMatrix) {
        int rowElements = objectiveMatrix.length;
        int totalElements = getTotalElements(objectiveMatrix);
        int[][] constraintsMatrix = new int[rowElements][totalElements];

        // Populate the constraints for arriving in a Location once
        for (int i = 0; i < rowElements; i++) {
            for (int j = 0; j < rowElements; j++) {
                constraintsMatrix[j][i * rowElements + j] = 1; // Set `1` for arrival constraints
            }
        }
        return constraintsMatrix;
    }

    /**
     * Generates a 2D matrix with constraints for leaving each Location exactly once.
     * <p>
     * The outer loop (i) iterates over each Location as the departure destination.
     * The inner loop (j) iterates over each possible destination.
     * For each pair (i, j), the corresponding position in the constraintsMatrix is set to 1.
     * This ensures that every Location (i) is departed from exactly once to all other Location (j).
     */
    private int[][] leaveConstraints(double[][] objectiveMatrix) {
        int rowElements = objectiveMatrix.length;
        int totalElements = getTotalElements(objectiveMatrix);
        int[][] constraintsMatrix = new int[rowElements][totalElements];

        // Populate the constraints for leaving a Location once
        for (int i = 0; i < rowElements; i++) {
            for (int j = 0; j < rowElements; j++) {
                constraintsMatrix[i][i * rowElements + j] = 1; // Set `1` for departure constraints
            }
        }
        return constraintsMatrix;
    }

    /**
     * Generates a 2D matrix with sub-tour elimination constraints for the Traveling Salesman Problem (TSP).
     * <p>
     * The method constructs a matrix that enforces sub-tour elimination constraints using decision variables (x_ij) and
     * auxiliary variables (U_i and U_j). The goal is to prevent the formation of smaller loops (sub-tours) in the TSP solution.
     *
     * <p>
     * The outer loop (i) iterates over locations starting from Location 2, skipping the first one (index 1 and onwards).
     * The inner loop (j) iterates over all other locations except where i == j, ensuring constraints are only applied
     * between different locations (i and j).
     * For each valid (i, j) pair:
     * - A coefficient for the decision variable (x_ij) is set in the constraintsMatrix.
     * - Auxiliary variables U_i and U_j are used in the constraint, ensuring that no sub-tours form.
     * The constraints follow the rule: Ui - Uj + (number of locations) * x_ij ≤ (number of locations - 1).
     * This rule enforces that the distances in a potential sub-tour are kept within limits, eliminating the possibility
     * of visiting only a subset of locations without visiting every location.
     *
     * @param objectiveMatrix The distance matrix that defines the travel costs between locations.
     *                        It's assumed to be an NxN matrix representing N locations.
     * @return A 2D matrix with sub-tour elimination constraints.
     */
    private int[][] subTourConstraints(double[][] objectiveMatrix) {
        int rowElements = objectiveMatrix.length;

        // Return null matrix for problems with fewer than 4 locations
        if (rowElements < 4) {
            return null;
        }

        int totalElements = getTotalElements(objectiveMatrix);
        int[][] constraintsMatrix = constructSubTourMatrix(rowElements);
        System.out.println("subTour Constraint Matrix Init. Length: " + constraintsMatrix[0].length);

        int constraintRow = 0;
        int constraintsRowLength = constraintsMatrix[0].length;

        // Populate the sub-tour constraints for Locations 3 through n
        for (int i = 2; i < rowElements; i++) {  // Loop over Location (skipping Location 1 and 2)
            for (int j = 2; j < rowElements; j++) {  // Loop over Location, avoiding diagonal (i != j)
                if (i != j) {
                    // Sub-tour elimination constraint for x_ij
                    constraintsMatrix[constraintRow][i * rowElements + j] = rowElements;  // Coefficient for x_ij

                    // Ensure the auxiliary variable indices are correctly calculated
                    if (totalElements + i - 1 < constraintsRowLength) {
                        constraintsMatrix[constraintRow][totalElements + i - 1] = 1;  // Ui term (U_i)
                    }

                    if (totalElements + j - 1 < constraintsRowLength) {
                        constraintsMatrix[constraintRow][totalElements + j - 1] = -1; // Uj term (U_j)
                    }

                    // Move to the next row for the next constraint
                    constraintRow++;
                }
            }
        }
        return constraintsMatrix;
    }

    private int[][] selfLoopConstrains(double[][] objectiveMatrix) {
        int n = objectiveMatrix.length;
        int[][] selfLoopMatrix = new int[n][n * n]; // n rows, n * n columns

        // Loop to populate the selfLoopMatrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    // Set the corresponding diagonal element in the block of n*n elements
                    selfLoopMatrix[i][i * n + j] = 1;
                } else {
                    selfLoopMatrix[i][i * n + j] = 0;
                }
            }
        }
        return selfLoopMatrix;
    }

    private int[] constructRHS(int[][] arriveMatrix, int[][] leaveMatrix, int[][] subTourMatrix, int[][] selfLoopMatrix, int numLocations) {
        int arriveLength = arriveMatrix.length;
        int leaveLength = leaveMatrix.length;
        int subTourLength = (subTourMatrix != null) ? subTourMatrix.length : 0;
        int selfLoopLength = selfLoopMatrix.length;
        int rhsLength = arriveLength + leaveLength + subTourLength + selfLoopLength;

        int[] rhsArray = new int[rhsLength];

        // First part: arrive and leave matrices should have RHS of 1
        for (int i = 0; i < arriveLength + leaveLength; i++) {
            rhsArray[i] = 1;
        }
        // Second part: sub-tour elimination constraints have RHS of numLocations - 1 (if subTourMatrix is not null)
        if (subTourMatrix != null) {
            for (int i = arriveLength + leaveLength; i < arriveLength + leaveLength + subTourLength; i++) {
                rhsArray[i] = numLocations - 1;
            }
        }
        // Third part: self-loop constraints (i = j) should have RHS of 0
        for (int i = arriveLength + leaveLength + subTourLength; i < rhsLength; i++) {
            rhsArray[i] = 0;
        }
        return rhsArray;
    }

    private String[] constructSigns(int[][] arriveMatrix, int[][] leaveMatrix, int[][] subTourMatrix, int[][] selfLoopMatrix) {
        int arriveLength = arriveMatrix.length;
        int leaveLength = leaveMatrix.length;
        int subTourLength = (subTourMatrix != null) ? subTourMatrix.length : 0;
        int selfLoopLength = selfLoopMatrix.length;
        int signsLength = arriveLength + leaveLength + subTourLength + selfLoopLength;

        String[] signArray = new String[signsLength];

        // First part: arrive and leave constraints use "="
        for (int i = 0; i < arriveLength + leaveLength; i++) {
            signArray[i] = "=";
        }
        // Second part: sub-tour constraints use "<=" (if subTourMatrix is not null)
        if (subTourMatrix != null) {
            for (int i = arriveLength + leaveLength; i < arriveLength + leaveLength + subTourLength; i++) {
                signArray[i] = "<=";
            }
        }
        // Third part: self-loop constraints (i = j) use "="
        for (int i = arriveLength + leaveLength + subTourLength; i < signsLength; i++) {
            signArray[i] = "=";
        }
        return signArray;
    }

    private int[][] constructSubTourMatrix(int rowElements) {
        int numAuxVars = rowElements - 2;  // Auxiliary variables (U3, U4, ..., Un)

        // Calculate the number of sub-tour elimination constraints
        int numConstraints = (rowElements - 3) * (rowElements - 2);  // Sub-tours excluding Location 1, Location 2, and x_i1, x_i2 constraints
        int numColumns = rowElements * rowElements + numAuxVars;

        // Initialize the constraints matrix (rows: sub-tour constraints, columns: x_ij variables + aux variables)
        return new int[numConstraints][numColumns];
    }

    private double[][] distanceMatrixTo2DArray(DistanceMatrix distanceMatrix) throws RuntimeException {
        try {
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
        } catch (RuntimeException ex) {
            throw new RuntimeException("Error Converting Distance Matrix to Double Matrix: " + ex.getMessage(), ex);
        }
    }

    private static double getDistanceInKm(DistanceMatrixElement element) throws RuntimeException {
        try {
            String humanReadable = element.distance.humanReadable.trim();  // Trim any whitespace
            double distanceInKm = 0.0;

            // Extract the numeric part of the distance (without the unit)
            String numericValue = humanReadable.split(" ")[0];

            if (humanReadable.endsWith(" km")) {
                // If the distance ends with " km", handle kilometers directly
                distanceInKm = Double.parseDouble(numericValue);
            } else if (humanReadable.endsWith(" m")) {
                // If the distance ends with " m", handle meters
                double distanceInMeters = Double.parseDouble(numericValue);
                distanceInKm = (distanceInMeters <= 1) ? 50000.0 : distanceInMeters / 1000.0;
            }

            return distanceInKm;
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Error Converting Distance in Distance Matrix to Double: " + ex.getMessage(), ex);
        }
    }

    private int[][] concatenateMatrices(int[][] arriveMatrix, int[][] leaveMatrix, int[][] subTourMatrix, int[][] selfLoopMatrix) {
        int arriveRows = arriveMatrix.length;
        int leaveRows = leaveMatrix.length;
        int subTourRows = (subTourMatrix != null) ? subTourMatrix.length : 0;
        int selfLoopRows = selfLoopMatrix.length;

        int numColumns = arriveMatrix[0].length; // Assuming all matrices have the same number of columns
        int totalRows = arriveRows + leaveRows + subTourRows + selfLoopRows;

        int[][] appendedMatrix = new int[totalRows][numColumns];

        // Copy arriveMatrix rows
        for (int i = 0; i < arriveRows; i++) {
            System.arraycopy(arriveMatrix[i], 0, appendedMatrix[i], 0, numColumns);
        }
        // Copy leaveMatrix rows
        for (int i = 0; i < leaveRows; i++) {
            System.arraycopy(leaveMatrix[i], 0, appendedMatrix[arriveRows + i], 0, numColumns);
        }
        // Copy subTourMatrix rows, if not null
        if (subTourMatrix != null) {
            for (int i = 0; i < subTourRows; i++) {
                System.arraycopy(subTourMatrix[i], 0, appendedMatrix[arriveRows + leaveRows + i], 0, numColumns);
            }
        }
        // Copy selfLoopMatrix rows
        for (int i = 0; i < selfLoopRows; i++) {
            System.arraycopy(selfLoopMatrix[i], 0, appendedMatrix[arriveRows + leaveRows + subTourRows + i], 0, numColumns);
        }
        return appendedMatrix;
    }

    private double[] flatten2DArray(double[][] matrix) {
        int numRows = matrix.length;  // Number of rows in the 2D array
        int numCols = matrix[0].length;  // Number of columns in the 2D array (assumed consistent for all rows)

        // Create a 1D array with a size equal to the total number of elements in the 2D array
        double[] flattenedArray = new double[numRows * numCols];

        // Index for tracking the position in the 1D array
        int index = 0;

        // Loop through each row and column of the 2D array
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // Copy each element from the 2D array into the 1D array
                flattenedArray[index++] = matrix[i][j];
            }
        }

        return flattenedArray;  // Return the flattened 1D array
    }

    private double[] equalize1DArraySizes(double[] smallerMatrix, int largerMatrixLength) {
        if (smallerMatrix.length >= largerMatrixLength) {
            return smallerMatrix;
        }

        double[] largerMatrix = new double[largerMatrixLength];
        System.arraycopy(smallerMatrix, 0, largerMatrix, 0, smallerMatrix.length);

        return largerMatrix;
    }

    private int[][] equalize2DArraySizes(int[][] smallerMatrix, int largerMatrixLength) {
        int smallerRowLength = smallerMatrix[0].length;
        int numRows = smallerMatrix.length;

        int[][] equalizedMatrix = new int[numRows][largerMatrixLength];

        // Loop through each row of the smaller matrix
        for (int i = 0; i < numRows; i++) {
            // Copy the contents of the current row from the smaller matrix to the new matrix
            System.arraycopy(smallerMatrix[i], 0, equalizedMatrix[i], 0, smallerRowLength);
        }
        return equalizedMatrix;  // Return the new matrix with equalized row lengths
    }

    private int getTotalElements(double[][] objectiveMatrix) {
        int numRows = objectiveMatrix.length;
        int numCols = objectiveMatrix[0].length;
        return numRows * numCols;
    }
}
