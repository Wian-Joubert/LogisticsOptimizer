package Model;

import com.google.maps.model.DistanceMatrix;

import java.util.Arrays;

public class TSModel {
    private final double[] objectiveFunction;
    private final int[][] subjectToMatrix;
    private final String[] signArray;
    private final int[] rhsArray;
    private int[] decisionArray;
    private int[] optimalSolution;
    private double objectiveValue = 100000;
    private final int numLocations;
    private final DistanceMatrix distanceMatrix;
    private String route;

    public TSModel(double[] objectiveFunction, int[][] subjectToMatrix, String[] signArray, int[] rhsArray, int numLocations, DistanceMatrix distanceMatrix) {
        this.objectiveFunction = objectiveFunction;
        this.subjectToMatrix = subjectToMatrix;
        this.signArray = signArray;
        this.rhsArray = rhsArray;
        this.numLocations = numLocations;
        this.distanceMatrix = distanceMatrix;
        calculateOptimalRoute();
    }

    public double[] getObjectiveFunction() {
        return objectiveFunction;
    }

    public int[][] getSubjectToMatrix() {
        return subjectToMatrix;
    }

    public String[] getSignArray() {
        return signArray;
    }

    public int[] getRhsArray() {
        return rhsArray;
    }

    public int[] getOptimalSolution() {
        return optimalSolution;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public DistanceMatrix getDistanceMatrix(){
        return distanceMatrix;
    }

    public String getRoute() {
        return route;
    }

    public int getNumLocations() {
        return numLocations;
    }

    public int[] getDecisionArray() {
        return decisionArray;
    }

    private void calculateOptimalRoute() {
        decisionArray = new int[objectiveFunction.length];
        //JOptionPane.showMessageDialog(null, "decisionArray length: " + decisionArray.length);
        optimalSolution = new int[objectiveFunction.length];
        explorePermutations(0);
    }

    private void explorePermutations(int index) {
        // Check if we are at the end of the decision array
        if (index == decisionArray.length) {
            if (!hasSelfLoop() && meetsConstraints() && !hasImmediateReturn()) {
                double currentObjectiveValue = calculateObjectiveValue();
                if (currentObjectiveValue < objectiveValue) {
                    objectiveValue = currentObjectiveValue;
                    System.arraycopy(decisionArray, 0, optimalSolution, 0, decisionArray.length);
                    System.out.println("New optimal solution found: " + arrayToString(optimalSolution) +
                            " with objective value: " + currentObjectiveValue);
                    printRouteInterpretation(optimalSolution);
                }
            }
            return;
        }

        // If the current index corresponds to a self-loop (50000), skip
        if (objectiveFunction[index] == 50000) {
            explorePermutations(index + 1);
            return;
        }

        // Calculate the row and column based on the flattened index
        int row = index / numLocations;
        // Stop counting before the dummy variables (U_n-1), i.e., ignore the last n-1 columns
        int numValidCols = numLocations - (numLocations - 1);  // This excludes the dummy variables

        // Skip if more than one "1" is already in the valid columns for the current row
        int countOnes = 0;
        for (int i = row * numLocations; i < (row * numLocations) + numValidCols; i++) {
            if (decisionArray[i] == 1) {
                countOnes++;
            }
            if (countOnes > 1) {
                return; // Skip further exploration if more than one "1" is present
            }
        }

        // Loop to set the current index to 0 or 1
        for (int value : new int[]{0, 1}) {
            decisionArray[index] = value;
            explorePermutations(index + 1);
        }
    }

    private boolean hasImmediateReturn() {
        for (int i = 0; i < numLocations; i++) {
            for (int j = 0; j < numLocations; j++) {
                if (decisionArray[i * numLocations + j] == 1 && decisionArray[j * numLocations + i] == 1) {
                    return true;
                }
            }
        }
        return false;
    }


    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Method to check for self-loops where i == j and the objective function contains 50000
    private boolean hasSelfLoop() {
        for (int i = 0; i < decisionArray.length; i++) {
            if (objectiveFunction[i] == 50000 && decisionArray[i] == 1) {
                return true;  // Self-loop detected, skip this permutation
            }
        }
        return false;  // No self-loop found
    }

    private boolean meetsConstraints() {
        for (int i = 0; i < subjectToMatrix.length; i++) {
            double sumProduct = 0;

            // Calculate the sum product for the current constraint
            for (int j = 0; j < decisionArray.length; j++) {
                sumProduct += decisionArray[j] * subjectToMatrix[i][j];
            }

            // Check the constraint
            switch (signArray[i]) {
                case "<=":
                    if (sumProduct > rhsArray[i]) {
                        return false; // Constraint violated
                    }
                    break;
                case "=":
                    if (sumProduct != rhsArray[i]) {
                        return false; // Constraint violated
                    }
                    break;
            }
        }
        return true; // All constraints are satisfied
    }

    private double calculateObjectiveValue() {
        double sumProduct = 0;
        for (int i = 0; i < decisionArray.length; i++) {
            sumProduct += decisionArray[i] * objectiveFunction[i];
        }
        return sumProduct;
    }

    public void printRouteInterpretation(int[] decisionArray) {
        StringBuilder route = new StringBuilder("L1");
        int currentLocation = 0; // Start at L1
        boolean[] visited = new boolean[numLocations]; // Track visited locations
        visited[currentLocation] = true; // Mark L1 as visited

        // Find the route based on the decision array
        while (true) {
            boolean foundNext = false;

            for (int j = 0; j < numLocations; j++) {
                // Calculate the index in the decision array for the edge x_ij
                int index = currentLocation * numLocations + j;

                // Check if there's a connection from currentLocation to j
                if (index < decisionArray.length && decisionArray[index] == 1 && !visited[j]) {
                    // Append the next location to the route
                    route.append(" -> L").append(j + 1);
                    currentLocation = j; // Move to the next location
                    visited[currentLocation] = true; // Mark the new location as visited
                    foundNext = true;
                    break; // Exit the loop to start from the new currentLocation
                }
            }
            // Break if no next location was found
            if (!foundNext) {
                break;
            }
        }
        // Print the resulting route
        System.out.println("Route: " + route + " -> L1");
        this.route = route + " -> L1";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TSModel {\n");

        // Objective Function
        builder.append("  Objective Function: ").append(Arrays.toString(objectiveFunction)).append("\n");

        // Subject To Matrix
        builder.append("  Subject To Matrix:\n");
        for (int[] row : subjectToMatrix) {
            builder.append("    ");
            for (int val : row) {
                builder.append(String.format(Integer.toString(val))).append("  ");  // Format for alignment
            }
            builder.append("\n");
        }
        // Sign Array
        builder.append("  Sign Array: ").append(Arrays.toString(signArray)).append("\n");
        // RHS Array
        builder.append("  RHS Array: ").append(Arrays.toString(rhsArray)).append("\n");
        // Decision Array
        builder.append("  Decision Array: ").append(Arrays.toString(decisionArray)).append("\n");
        // Optimal Solution
        builder.append("  Optimal Solution: ").append(Arrays.toString(optimalSolution)).append("\n");
        // Objective Value
        builder.append("  Objective Value: ").append(objectiveValue).append("\n");
        builder.append("}");
        return builder.toString();
    }
}