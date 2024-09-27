import java.math.BigInteger;
import java.util.Arrays;

public class PolynomialConstantFinder {

    public static void main(String[] args) {
        String input = "{ \"keys\": { \"n\": 4, \"k\": 3 }, \"1\": { \"base\": \"10\", \"value\": \"4\" }, \"2\": { \"base\": \"2\", \"value\": \"111\" }, \"3\": { \"base\": \"10\", \"value\": \"12\" }, \"6\": { \"base\": \"4\", \"value\": \"213\" } }";

        findConstantTerm(input);
    }

    public static void findConstantTerm(String input) {
        int k = Integer.parseInt(extractValue(input, "\"k\": ", "}").trim());  // Trim added to remove extra spaces

        double[] x = new double[k];
        double[] y = new double[k];

        for (int i = 1; i <= k; i++) {
            String baseString = extractValue(input, "\"" + i + "\": { \"base\": \"", "\",").trim();
            String valueString = extractValue(input, "\"value\": \"", "\" }", i).trim();

            int base = Integer.parseInt(baseString);
            BigInteger value = new BigInteger(valueString, base);

            x[i - 1] = i;
            y[i - 1] = value.doubleValue();
        }

        double[][] coefficientMatrix = new double[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                coefficientMatrix[i][j] = Math.pow(x[i], k - j - 1);
            }
        }

        double[] coefficients = gaussianElimination(coefficientMatrix, y);

        double constantTerm = coefficients[k - 1];

        System.out.println("The constant term 'c' is: " + constantTerm);
    }

    // Helper method to extract a value from the input string based on surrounding key markers
    public static String extractValue(String input, String startMarker, String endMarker) {
        int startIndex = input.indexOf(startMarker) + startMarker.length();
        int endIndex = input.indexOf(endMarker, startIndex);
        return input.substring(startIndex, endIndex);
    }


    public static String extractValue(String input, String startMarker, String endMarker, int occurrence) {
        int startIndex = input.indexOf(startMarker, input.indexOf("\"" + occurrence + "\": {")) + startMarker.length();
        int endIndex = input.indexOf(endMarker, startIndex);
        return input.substring(startIndex, endIndex);
    }

    public static double[] gaussianElimination(double[][] matrix, double[] rhs) {
        int n = rhs.length;

        for (int i = 0; i < n; i++) {
            // Find pivot for column i
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[max][i])) {
                    max = j;
                }
            }

            // Swap rows i and max in both matrix and rhs
            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            double t = rhs[i];
            rhs[i] = rhs[max];
            rhs[max] = t;

            // Pivot within matrix
            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i] / matrix[i][i];
                rhs[j] -= factor * rhs[i];
                for (int k = i; k < n; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
        }

        // Back substitution
        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += matrix[i][j] * solution[j];
            }
            solution[i] = (rhs[i] - sum) / matrix[i][i];
        }
        return solution;
    }
}
