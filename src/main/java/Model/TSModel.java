package Model;

import java.util.Arrays;

public class TSModel {
    private final double[] objectiveFunction;
    private final int[][] subjectToMatrix;
    private final String[] signArray;
    private final int[] rhsArray;
    private int[] decisionArray;
    private int[] optimalSolution;
    private double objectiveValue = 100000;

    // Updated constructor to accept numberOfLocations
    public TSModel(double[] objectiveFunction, int[][] subjectToMatrix, String[] signArray, int[] rhsArray) {
        this.objectiveFunction = objectiveFunction;
        this.subjectToMatrix = subjectToMatrix;
        this.signArray = signArray;
        this.rhsArray = rhsArray;
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

    private void calculateOptimalRoute() {
        decisionArray = new int[objectiveFunction.length];
        optimalSolution = new int[objectiveFunction.length];
        explorePermutations(0);
    }

    private void explorePermutations(int index) {
        // Check if we are at the end of the decision array
        if (index == decisionArray.length) {
            if (!hasSelfLoop() && meetsConstraints()) {
                double currentObjectiveValue = calculateObjectiveValue();
                if (currentObjectiveValue < objectiveValue) {
                    objectiveValue = currentObjectiveValue;
                    System.arraycopy(decisionArray, 0, optimalSolution, 0, decisionArray.length);
                    System.out.println("New optimal solution found: " + arrayToString(optimalSolution) +
                            " with objective value: " + currentObjectiveValue);
                }
            }
            return;
        }

        // If the current index corresponds to a self-loop (50000), skip
        if (objectiveFunction[index] == 50000) {
            explorePermutations(index + 1);
            return;
        }

        // Loop to set the current index to 0 or 1
        for (int value : new int[]{0, 1}) {
            decisionArray[index] = value;
            explorePermutations(index + 1);
        }
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
                // Add more cases if you have other types of constraints
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