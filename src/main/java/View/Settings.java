package View;

import Controller.FileController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Settings extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField apiKey;
    private JButton saveAsKeyButton;
    private JPasswordField apiKeyField;
    private JButton saveKey;
    private JComboBox currencyCombo;
    private JButton saveCostsButton;
    private JTextField hourlyCost;
    private JTextField avgFuelCost;

    final FileController fileController = new FileController();

    public Settings() {
        this.setTitle("Settings");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Close only the Settings window
        this.setSize(500, 400);
        this.setContentPane(panel1);
        this.setLocationRelativeTo(null);

        if (!loadCosts() || !loadApiKey()) {
            JOptionPane.showMessageDialog(null, "API Key or Default Costs are missing.\nPlease enter values before continuing.", "Load Error", JOptionPane.ERROR_MESSAGE);
        }

        saveKey.addActionListener(e -> {
            String api_key;
            api_key = Arrays.toString(apiKeyField.getPassword());
            fileController.writeApiKey(api_key);
        });
        saveCostsButton.addActionListener(e -> {
            String currency;
            double hourlyRate = 0;
            double fuelCost = 0;

            if (currencyCombo.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "There is no currency selected.", "Cost Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currency = (String) currencyCombo.getSelectedItem();
            try {
                if (hourlyCost.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "There is no value in Hourly Rate.", "Cost Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hourlyRate = Double.parseDouble(hourlyCost.getText().trim());
                if (avgFuelCost.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "There is no value in Fuel Cost.", "Cost Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fuelCost = Double.parseDouble(avgFuelCost.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid Input for Cost Values.", "Cost Save Error", JOptionPane.ERROR_MESSAGE);
                hourlyCost.requestFocus();
            }
            fileController.saveCosts(currency, hourlyRate, fuelCost);
        });
    }

    private boolean loadApiKey() {
        try {
            String apiKey = fileController.readApiKey();
            apiKeyField.setText(apiKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean loadCosts() {
        try {
            String[] costs = fileController.loadCosts();
            currencyCombo.setSelectedItem(costs[0]);
            hourlyCost.setText(costs[1]);
            avgFuelCost.setText(costs[2]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}