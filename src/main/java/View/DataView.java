package View;

import Model.KSModel;
import Model.RevenueModel;
import Model.TSModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DataView extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel panel1;
    private JButton backToEntry;
    private JEditorPane prodView;
    private JEditorPane routePane;
    private JEditorPane revenuePane;
    private JEditorPane overPane;

    private JFrame thisForm;
    private final TSModel tsModel;
    private final KSModel ksModel;
    private final RevenueModel revenueModel;


    public DataView(TSModel tsModel, KSModel ksModel, RevenueModel revenueModel) {
        this.setTitle("Logistics Optimizer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1480, 740);
        this.setContentPane(panel1);
        this.setLocationRelativeTo(null);

        this.tsModel = tsModel;
        this.ksModel = ksModel;
        this.revenueModel = revenueModel;

        displayInformation();

        backToEntry.addActionListener(e -> {
            DataEntry dataEntryForm = new DataEntry();
            dataEntryForm.setThisForm(dataEntryForm);
            dataEntryForm.setVisible(true);
            thisForm.setVisible(false);
        });
    }

    private void displayInformation() {
        prodView.setContentType("text/html");
        prodView.setText(generateKSHTML());

        // Display route information in routePane
        routePane.setContentType("text/html");
        routePane.setText(generateTSHTML());

        // Display revenue information in revenuePane
        revenuePane.setContentType("text/html");
        revenuePane.setText(generateRevHTML());

        // Display an overview in overPane
        overPane.setContentType("text/html");
        overPane.setText(generateOverHTML());
    }

    private String generateOverHTML() {
        StringBuilder html = new StringBuilder();

        // Start HTML structure with centered content and CSS for table borders
        html.append("<html><body style='text-align: center;'>");

        // Title
        html.append("<h1>Logistics Summary Report</h1>");

        // Route and Travel Information (from TSModel)
        html.append("<h2>Travel Information</h2>");
        html.append("<p><strong>Route:</strong> ").append(tsModel.getRoute()).append("</p>");
        html.append("<p><strong>Total Distance:</strong> ").append(String.format("%.3f", revenueModel.getTotalDistance())).append(" km</p>");
        html.append("<p><strong>Total Duration:</strong> ").append(String.format("%.3f", revenueModel.getTotalDuration())).append(" hours</p>");

        // Product Information (from KSModel)
        html.append("<h2>Products</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        html.append("<tr><th>Product</th><th>Quantity</th></tr>");
        ksModel.getProductQuantities().forEach((product, quantity) -> html.append("<tr><td>").append(product.getName()).append("</td><td>").append(quantity).append("</td></tr>"));
        html.append("</table>");

        // Cost and Revenue Information (from RevenueModel)
        html.append("<h2>Cost and Revenue</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        html.append("<tr><th>Metric</th><th>Value</th></tr>");
        html.append("<tr><td>Total Value of Products</td><td>").append(String.format("%.3f", revenueModel.getTotalValue())).append("</td></tr>");
        html.append("<tr><td>Total Fuel Cost</td><td>").append(String.format("%.3f", revenueModel.getTotalFuelCost())).append("</td></tr>");
        html.append("<tr><td>Total Employee Cost</td><td>").append(String.format("%.3f", revenueModel.getTotalEmployeeCost())).append("</td></tr>");
        html.append("<tr><td>Total Shipping Cost</td><td>").append(String.format("%.3f", revenueModel.getTotalShippingCost())).append("</td></tr>");
        html.append("<tr><td>Profit</td><td>").append(String.format("%.3f", revenueModel.getProfit())).append("</td></tr>");
        html.append("</table>");

        // End HTML structure
        html.append("</body></html>");

        return html.toString();
    }

    private String generateTSHTML() {
        StringBuilder html = new StringBuilder();

        // Start HTML structure
        html.append("<html><body style='text-align: center;'>");

        // Title for the TSModel
        html.append("<h1>Traveling Salesman Problem Model</h1>");

        // Objective function
        html.append("<h2>Objective Function</h2>");
        html.append("<p>").append(Arrays.toString(tsModel.getObjectiveFunction())).append("</p>");

        // Subject-to Matrix
        html.append("<h2>Subject To Matrix</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        for (int[] row : tsModel.getSubjectToMatrix()) {
            html.append("<tr>");
            for (int value : row) {
                html.append("<td style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>").append(value).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");

        // Sign Array
        html.append("<h2>Sign Array</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        html.append("<tr>");
        for (String sign : tsModel.getSignArray()) {
            html.append("<td style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>").append(sign).append("</td>");
        }
        html.append("</tr>");
        html.append("</table>");

        // RHS Array
        html.append("<h2>RHS Array</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        html.append("<tr>");
        for (int rhsValue : tsModel.getRhsArray()) {
            html.append("<td style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>").append(rhsValue).append("</td>");
        }
        html.append("</tr>");
        html.append("</table>");

        // Decision Array
        html.append("<h2>Decision Array</h2>");
        html.append("<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>");
        html.append("<tr>");
        for (int decision : tsModel.getDecisionArray()) {
            html.append("<td style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>").append(decision).append("</td>");
        }
        html.append("</tr>");
        html.append("</table>");

        // Optimal Solution
        html.append("<h2>Optimal Solution</h2>");
        html.append("<p>").append(Arrays.toString(tsModel.getOptimalSolution())).append("</p>");

        // Objective Value
        html.append("<h2>Objective Value</h2>");
        html.append("<p>").append(String.format("%.3f", tsModel.getObjectiveValue())).append(" km</p>");

        // End HTML structure
        html.append("</body></html>");

        return html.toString();
    }

    private String generateRevHTML() {

        // Start HTML structure

        // Title for the RevenueModel
        // Create a table to display the values
        // Add all values as table rows
        // Close the table
        // End HTML structure

        return "<html><body style='text-align: center;'>" +

                // Title for the RevenueModel
                "<h1>Revenue Model Report</h1>" +

                // Create a table to display the values
                "<table style='border: 1px solid black; border-collapse: collapse; margin: 0 auto;'>" +
                "<tr><th>Metric</th><th>Value</th></tr>" +

                // Add all values as table rows
                "<tr><td>Total Value</td><td>" + String.format("%.3f", revenueModel.getTotalValue()) + "</td></tr>" +
                "<tr><td>Total Distance</td><td>" + String.format("%.3f", revenueModel.getTotalDistance()) + " km</td></tr>" +
                "<tr><td>Total Duration</td><td>" + String.format("%.3f", revenueModel.getTotalDuration()) + " hours</td></tr>" +
                "<tr><td>Fuel Consumption (L:km)</td><td>" + String.format(Integer.toString(revenueModel.getFuelConsumption())) + "</td></tr>" +
                "<tr><td>Total Fuel Used </td><td>" + String.format("%.3f", revenueModel.getFuelUsed()) + " l</td></tr>" +
                "<tr><td>Total Fuel Cost</td><td>" + String.format("%.3f", revenueModel.getTotalFuelCost()) + "</td></tr>" +
                "<tr><td>Total Employee Cost</td><td>" + String.format("%.3f", revenueModel.getTotalEmployeeCost()) + "</td></tr>" +
                "<tr><td>Total Shipping Cost</td><td>" + String.format("%.3f", revenueModel.getTotalShippingCost()) + "</td></tr>" +
                "<tr><td>Profit</td><td>" + String.format("%.3f", revenueModel.getProfit()) + "</td></tr>" +

                // Close the table
                "</table>" +

                // End HTML structure
                "</body></html>";
    }

    private String generateKSHTML() {
        StringBuilder html = new StringBuilder();

        // Basic HTML structure
        html.append("<html><body style='text-align: center;'>");

        // Header for total value
        html.append("<h1>Total Value Achieved: ").append(String.format("%.3f", ksModel.getMaxValue())).append("</h1>");

        // Product list
        html.append("<h2>Selected Products</h2>");
        html.append("<ul style='list-style-type: none; padding: 0;'>");

        // Loop through each product and quantity
        ksModel.getProductQuantities().forEach((product, quantity) -> {
            double totalValue = product.getValue() * quantity;

            // Create list items for each product
            html.append("<li>")
                    .append("<strong>Product Name:</strong> ").append(product.getName()).append("<br>")
                    .append("<strong>Units:</strong> ").append(quantity).append("<br>")
                    .append("<strong>Total Value:</strong> ").append(String.format("%.3f", totalValue))
                    .append("</li><br>");
        });

        html.append("</ul>");
        html.append("</body></html>");

        return html.toString();
    }

    public void setThisForm(JFrame form) {
        this.thisForm = form;
    }
}
