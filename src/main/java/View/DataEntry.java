package View;

import Controller.*;
import Model.*;

import com.google.maps.model.DistanceMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class DataEntry extends JFrame{
    private JPanel rootPanel;
    private JPanel mainPanel;
    private JPanel mainLeftPanel;
    private JPanel mainRightPanel;
    private JPanel mainCenterPanel;
    private JLabel lblLeftSub;
    private JLabel lblCenterSub;
    private JLabel lblRightSub;
    private JPanel leftCenterPanel;
    private JPanel rightCenterPanel;
    private JPanel centerCenterPanel;
    private JTable prodTable;
    private JTextField prodName;
    private JButton prodAdd;
    private JButton prodEdit;
    private JButton prodDelete;
    private JButton prodReset;
    private JTextField prodLength;
    private JTextField prodWidth;
    private JTextField prodHeight;
    private JTextField prodWeight;
    private JTextField prodValue;
    private JComboBox currencyCombo;
    private JTable routeTable;
    private JButton roAdd;
    private JButton roReset;
    private JButton roEdit;
    private JButton roDelete;
    private JTextField roStreet;
    private JTextField roTown;
    private JTextField roCity;
    private JTextField roPostCode;
    private JTextPane vehDetailsPane;
    private JTextField vehHeight;
    private JTextField vehWidth;
    private JTextField vehLength;
    private JSpinner vehFuelCon;
    private JSpinner vehMaxLoad;
    private JButton vehAdd;
    private JButton vehReset;
    private JButton saveProductList;
    private JButton loadProductList;
    private JButton saveRouteList;
    private JButton loadRouteList;
    private JButton saveVehicle;
    private JButton loadVehicle;
    private JButton calculateOptimalSolutionsButton;
    private JButton saveAll;
    private JButton loadAll;
    private JButton openFile;
    private JButton btnSettings;
    private JComboBox vehStandardContainer;
    private JCheckBox vehCustomContainer;
    private JButton inputGoogleAddress;

    private static DefaultTableModel productModel;
    private static DefaultTableModel routeModel;
    private static StyledDocument vehicleDoc;
    private final Logger logger = LoggerFactory.getLogger(DataEntry.class);
    ValidationController vc = new ValidationController();
    FileController fileController = new FileController();

    private JFrame thisForm;

    public DataEntry() {
        this.setTitle("Logistics Optimizer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1480, 740);
        this.setContentPane(rootPanel);
        this.setLocationRelativeTo(null);

        initTables();

        prodAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = makeProduct();
                if (product != null) {
                    addProductToTable(product);
                    clearProduct();
                }
                prodName.requestFocus();
            }
        });
        vehAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicle = makeVehicle();
                if (vehicle != null) {
                    addVehicleToPane(vehicle);
                }
            }
        });
        roAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Place place = makePlace();
                if (place != null) {
                    addRouteToTable(place);
                    clearRoute();
                }
                roStreet.requestFocus();
            }
        });
        vehStandardContainer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) vehStandardContainer.getSelectedItem();
                if ("Custom".equals(selectedItem)) {
                    vehLength.setText("");
                    vehWidth.setText("");
                    vehHeight.setText("");
                    vehCustomContainer.setSelected(true);
                    vehStandardContainer.setEnabled(false);
                } else {
                    try {
                        StandardContainers container;
                        if (selectedItem == null) {
                            throw new RuntimeException("selectedItem is null.");
                        }
                        container = StandardContainers.getContainerByName(selectedItem);
                        if (container != null) {
                            vehLength.setText(String.valueOf(container.getLength()));
                            vehWidth.setText(String.valueOf(container.getWidth()));
                            vehHeight.setText(String.valueOf(container.getHeight()));
                            vehCustomContainer.setSelected(false);
                            vehStandardContainer.setEnabled(true);
                        }
                    } catch (RuntimeException ex) {
                        logger.error("Container selector is null. Error: {}", ex.getMessage());
                    }
                }
            }
        });
        vehCustomContainer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vehCustomContainer.isSelected()) {
                    vehStandardContainer.setSelectedIndex(vehStandardContainer.getItemCount() - 1);
                    vehLength.setText("");
                    vehWidth.setText("");
                    vehHeight.setText("");
                    vehStandardContainer.setEnabled(false);
                } else {
                    vehStandardContainer.setEnabled(true);
                }
            }
        });
        prodTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = prodTable.getSelectedRow();
                if (selectedRow != -1) {
                    String[] rowValues = getRowValues(prodTable, selectedRow);
                    prodName.setText(rowValues[0]);
                    currencyCombo.setSelectedItem(rowValues[1]);
                    prodValue.setText(rowValues[2]);
                    prodWeight.setText(rowValues[3]);
                    prodLength.setText(rowValues[4]);
                    prodWidth.setText(rowValues[5]);
                    prodHeight.setText(rowValues[6]);
                }
                super.mouseClicked(e);
            }
        });
        routeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = routeTable.getSelectedRow();
                if (selectedRow != -1) {
                    String[] rowValues = getRowValues(routeTable, selectedRow);
                    roStreet.setText(rowValues[0]);
                    roTown.setText(rowValues[1]);
                    roCity.setText(rowValues[2]);
                    roPostCode.setText(rowValues[3]);
                }
                super.mouseClicked(e);
            }
        });
        prodEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = prodTable.getSelectedRow();
                try {
                    if (selectedRow == -1) {
                        throw new RuntimeException("Select a row before Updating.");
                    }
                    String[] rowValues = {prodName.getText().trim(), (String) currencyCombo.getSelectedItem(), prodValue.getText().trim(), prodWeight.getText().trim(), prodLength.getText().trim(), prodWidth.getText().trim(), prodHeight.getText().trim()};
                    ValidationResult result = vc.validateProduct(rowValues[0], rowValues[1], Double.parseDouble(rowValues[2]), Double.parseDouble(rowValues[3]), Double.parseDouble(rowValues[4]), Double.parseDouble(rowValues[5]), Double.parseDouble(rowValues[6]));
                    if (!result.isValid()) {
                        throw new RuntimeException(result.getMessage());
                    }
                    for (int i = 0; i < rowValues.length; i++) {
                        prodTable.setValueAt(rowValues[i], selectedRow, i);
                    }
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Product Update Error", JOptionPane.ERROR_MESSAGE);
                    logger.error("Failed to Update Product: {}", ex.getMessage());
                }
            }
        });
        roEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = routeTable.getSelectedRow();
                try {
                    if (selectedRow == -1) {
                        throw new RuntimeException("Select a row before Updating.");
                    }
                    String[] rowValues = {roStreet.getText().trim(), roTown.getText().trim(), roCity.getText().trim(), roPostCode.getText().trim(),};
                    ValidationResult result = vc.validatePlace(rowValues[0], rowValues[1], rowValues[2], rowValues[3]);

                    if (!result.isValid()) {
                        throw new RuntimeException(result.getMessage());
                    }
                    for (int i = 0; i < rowValues.length; i++) {
                        routeModel.setValueAt(rowValues[i], selectedRow, i);
                    }
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Update Error", JOptionPane.ERROR_MESSAGE);
                    logger.error("Failed to Update Route: {}", ex.getMessage());
                }
            }
        });
        prodReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearProduct();
            }
        });
        vehReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearVehicle();
            }
        });
        roReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearRoute();
            }
        });
        prodDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = prodTable.getSelectedRow();
                try {
                    if (selectedRow == -1) {
                        throw new RuntimeException("Select a row before Deleting.");
                    }
                    productModel.removeRow(selectedRow);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Product Delete Error", JOptionPane.ERROR_MESSAGE);
                    logger.error("Failed to Delete Product: {}", ex.getMessage());
                }
            }
        });
        roDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = routeTable.getSelectedRow();
                try {
                    if (selectedRow == -1) {
                        throw new RuntimeException("Select a row before Deleting.");
                    }
                    routeModel.removeRow(selectedRow);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Delete Error", JOptionPane.ERROR_MESSAGE);
                    logger.error("Failed to Delete Route: {}", ex.getMessage());
                }
            }
        });
        calculateOptimalSolutionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Product> products = getProducts();
                Vehicle vehicle = getVehicle();
                ArrayList<Place> places = getPlaces();

                if (vehicle == null){
                    throw new RuntimeException("Vehicle is null.");
                }
                KnapsackController knapsackController = new KnapsackController();
                KSModel ksModel = knapsackController.calculateKnapsack(products, vehicle);

                DistanceMatrixController distanceMatrixController = new DistanceMatrixController();
                DistanceMatrix distanceMatrix = distanceMatrixController.distanceMatrixCall(places);
                RouteController routeController = new RouteController();
                TSModel tsModel = routeController.calculateTraveling(distanceMatrix);

                if (ksModel == null || tsModel == null){
                    throw new RuntimeException("Data models are null.");
                }
                RevenueController revenueController = new RevenueController();
                RevenueModel revenueModel = revenueController.calculateRevenue(ksModel, tsModel, vehicle);

                DataView dataViewFrame = new DataView(tsModel, ksModel, revenueModel);
                dataViewFrame.setThisForm(dataViewFrame);
                dataViewFrame.setVisible(true);
                thisForm.setVisible(false);
            }
        });
        btnSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settingsFrame = new Settings();
                settingsFrame.setVisible(true);
            }
        });
        inputGoogleAddress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String streetAddress = "";
                String town = "";
                String city = "";
                String postCode = "";

                while (true) {
                    String input = JOptionPane.showInputDialog(null,
                            "Input Google copied address:\n\nStreet Address, Town, City, Post Code",
                            "Address Input",
                            JOptionPane.QUESTION_MESSAGE);

                    if (input == null) {
                        return; // Exit if the user cancels
                    }

                    input = input.trim();

                    if (input.isBlank()) {
                        JOptionPane.showMessageDialog(null, "Address cannot be blank. Please try again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue; // Prompt for valid input
                    }

                    String[] parts = input.split(",");

                    if (parts.length == 4) {
                        streetAddress = parts[0].trim();
                        town = parts[1].trim();
                        city = parts[2].trim();
                        postCode = parts[3].trim();
                        break; // Valid input, exit loop

                    } else if (parts.length == 5) {
                        streetAddress = parts[0].trim();
                        town = parts[2].trim();
                        city = parts[3].trim();
                        postCode = parts[4].trim();
                        break; // Valid input, exit loop

                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid address format. Please enter the correct address format.", "Route Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Set the text fields only after valid input
                roStreet.setText(streetAddress);
                roTown.setText(town);
                roCity.setText(city);
                roPostCode.setText(postCode);
            }
        });
        saveProductList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    String input = JOptionPane.showInputDialog(null,
                            "Input Product List name:\n",
                            "Save Product List",
                            JOptionPane.QUESTION_MESSAGE);
                    if (input == null) {
                        return;
                    }
                    input = input.trim();
                    if (input.isBlank()) {
                        JOptionPane.showMessageDialog(null, "File name cannot be blank. Try Again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    ValidationResult result = vc.validateFileInput(input);
                    if (!result.isValid()) {
                        JOptionPane.showMessageDialog(null, "Invalid Windows file name. Try Again.", "File Name Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    ArrayList<Product> products = getProducts();
                    fileController.saveProductList(products, input, true);
                    break;
                }
            }
        });
        saveRouteList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    String input = JOptionPane.showInputDialog(null,
                            "Input Route List name:\n",
                            "Save Route List",
                            JOptionPane.QUESTION_MESSAGE);
                    if (input == null) {
                        return;
                    }
                    input = input.trim();
                    if (input.isBlank()) {
                        JOptionPane.showMessageDialog(null, "File name cannot be blank. Try Again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    ValidationResult result = vc.validateFileInput(input);
                    if (!result.isValid()) {
                        JOptionPane.showMessageDialog(null, "Invalid Windows file name. Try Again.", "File Name Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    ArrayList<Place> places = getPlaces();
                    fileController.saveRouteList(places, input, true);
                    break;
                }
            }
        });
        saveVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    String input = JOptionPane.showInputDialog(null,
                            "Input Vehicle name:\n",
                            "Save Vehicle",
                            JOptionPane.QUESTION_MESSAGE);
                    if (input == null) {
                        return;
                    }
                    input = input.trim();
                    if (input.isBlank()) {
                        JOptionPane.showMessageDialog(null, "File name cannot be blank. Try Again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    ValidationResult result = vc.validateFileInput(input);
                    if (!result.isValid()) {
                        JOptionPane.showMessageDialog(null, "Invalid Windows file name. Try Again.", "File Name Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    Vehicle vehicle = getVehicle();
                    fileController.saveVehicle(vehicle, input, true);
                    break;
                }
            }
        });
        loadProductList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = searchFile("Products");
                if (file == null) {
                    return;
                }
                ArrayList<Product> products = fileController.loadProductList(file);
                for (Product product : products) {
                    addProductToTable(product);
                }
                JOptionPane.showMessageDialog(null, "File Loaded Successfully.");
            }
        });
        loadRouteList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = searchFile("Routes");
                if (file == null) {
                    return;
                }
                ArrayList<Place> places = fileController.loadRouteList(file);
                for (Place place : places) {
                    addRouteToTable(place);
                }
                JOptionPane.showMessageDialog(null, "File Loaded Successfully.");
            }
        });
        loadVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = searchFile("Vehicles");
                if (file == null) {
                    return;
                }
                Vehicle vehicle = fileController.loadVehicle(file);
                addVehicleToPane(vehicle);
                JOptionPane.showMessageDialog(null, "File Loaded Successfully.");
            }
        });
        saveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] fileNames = new String[3];
                String[] type = {"Product List", "Route List", "Vehicle"};
                for (int i = 0; i < 3; ) {
                    String input = JOptionPane.showInputDialog(null,
                            String.format("Input %s name:\n", type[i]),
                            String.format("Save %s", type[i]),
                            JOptionPane.QUESTION_MESSAGE);

                    if (input == null) {
                        return; // User cancelled
                    }

                    String trimmedInput = input.trim();

                    if (trimmedInput.isBlank()) {
                        JOptionPane.showMessageDialog(null, "File name cannot be blank. Try Again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue; // Ask for the same input again
                    }

                    ValidationResult result = vc.validateFileInput(trimmedInput);
                    if (!result.isValid()) {
                        JOptionPane.showMessageDialog(null, "Invalid Windows file name. Try Again.", "File Name Error", JOptionPane.ERROR_MESSAGE);
                        continue; // Ask for the same input again
                    }

                    fileNames[i] = trimmedInput; // Save valid input
                    i++; // Move to the next input
                }
                ArrayList<Product> products = getProducts();
                fileController.saveProductList(products, fileNames[0], false);
                ArrayList<Place> places = getPlaces();
                fileController.saveRouteList(places, fileNames[1], false);
                Vehicle vehicle = getVehicle();
                fileController.saveVehicle(vehicle, fileNames[2], false);
                JOptionPane.showMessageDialog(null, "Files saved successfully.");
            }
        });
        loadAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] fileNames = new String[3];
                String[] type = {"Products", "Routes", "Vehicles"};
                for (int i = 0; i < 3; ) {
                    String file = searchFile(type[i]);
                    if (file == null) {
                        return;
                    }
                    fileNames[i] = file;
                    i++;
                }
                try {
                    ArrayList<Product> products = fileController.loadProductList(fileNames[0]);
                    for (Product product : products) {
                        addProductToTable(product);
                    }
                    ArrayList<Place> places = fileController.loadRouteList(fileNames[1]);
                    for (Place place : places) {
                        addRouteToTable(place);
                    }
                    Vehicle vehicle = fileController.loadVehicle(fileNames[2]);
                    addVehicleToPane(vehicle);
                    JOptionPane.showMessageDialog(null, "Files Loaded Successfully.");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void setThisForm(JFrame form){
        this.thisForm = form;
    }

    private void initTables() {
        //Init Tables
        String[] prodColumns = {"Name", "Currency", "Value", "Weight", "Length", "Width", "Height", "Volume"};
        productModel = new DefaultTableModel(prodColumns, 0);
        prodTable.setModel(productModel);

        String[] routeColumns = {"Street", "City", "Town", "Post Code"};
        routeModel = new DefaultTableModel(routeColumns, 0);
        routeTable.setModel(routeModel);

        vehicleDoc = vehDetailsPane.getStyledDocument();
    }

    private String searchFile(String type) {
        String projectDir = System.getProperty("user.dir");
        String resourcesPath = projectDir + "/src/main/resources/" + type + "/";

        JFileChooser fileChooser = new JFileChooser(resourcesPath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select a file");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            return fileToOpen.getName(); // Return file name with extension
        }
        return null; // Return null if no file was selected
    }

    private void clearRoute() {
        roStreet.setText(null);
        roTown.setText(null);
        roCity.setText(null);
        roPostCode.setText(null);
        routeTable.clearSelection();
    }

    private void clearVehicle() {
        vehFuelCon.setValue(0);
        vehMaxLoad.setValue(0);
        vehCustomContainer.setSelected(false);
        vehStandardContainer.setSelectedIndex(0);
        vehLength.setText(null);
        vehWidth.setText(null);
        vehHeight.setText(null);
    }

    private void clearProduct() {
        prodName.setText(null);
        currencyCombo.setSelectedIndex(0);
        prodValue.setText(null);
        prodWeight.setText(null);
        prodLength.setText(null);
        prodWidth.setText(null);
        prodHeight.setText(null);
        prodTable.clearSelection();
    }

    private String[] getRowValues(JTable table, int selectedRow) {
        String[] rowValues = new String[table.getColumnCount()];
        for (int i = 0; i < rowValues.length; i++) {
            rowValues[i] = table.getValueAt(selectedRow, i).toString();
        }
        return rowValues;
    }

    private void addProductToTable(Product product) {
        Object[] rowData = {product.getName(), product.getCurrency(), product.getValue(), product.getWeight(), product.getLength(), product.getWidth(), product.getHeight(), product.getVolume()};
        productModel.addRow(rowData);
    }

    private void addRouteToTable(Place place) {
        Object[] rowData = {place.getStreet(), place.getTown(), place.getCity(), place.getPostcode()};
        routeModel.addRow(rowData);
    }

    private void addVehicleToPane(Vehicle vehicle) {
        String[] vehicleDetails = {String.format("Fuel Consumption:\t %skm per Litre\n", vehicle.getFuelConsumption()), String.format("Max Weight:\t\t %skg\n", vehicle.getMaxWeight()), String.format("Container Length:\t %sm\n", vehicle.getConLength()), String.format("Container Width:\t %sm\n", vehicle.getConWidth()), String.format("Container Height:\t %sm\n", vehicle.getConHeight()), String.format("Container Volume:\t %sm^3\n", vehicle.getConVolume()),};
        try {
            vehicleDoc.remove(0, vehicleDoc.getLength());
            for (String detail : vehicleDetails) {
                vehicleDoc.insertString(vehicleDoc.getLength(), detail, null);
            }
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(null, "There was an Error Displaying Vehicle Details", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Display New Vehicle: {}", ex.getMessage());
        }
    }

    private Place makePlace() {
        String streetAddress;
        String town;
        String city;
        String postcode;

        try {
            streetAddress = roStreet.getText().trim();
            town = roTown.getText().trim();
            city = roCity.getText().trim();
            postcode = roPostCode.getText().trim();

            ValidationResult result = vc.validatePlace(streetAddress, town, city, postcode);
            if (!result.isValid()) {
                throw new RuntimeException(result.getMessage());
            }
            return new Place(streetAddress, town, city, postcode);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Route Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Route Inputs have Mismatched Datatypes: {}", ex.getMessage());
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Route Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Route Inputs have Missing Info: {}", ex.getMessage());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Create New Route: {}", ex.getMessage());
        }
        return null;
    }

    private Vehicle makeVehicle() {
        int fuelConsumption;
        double maxWeight;
        double conLength;
        double conWidth;
        double conHeight;

        try {
            fuelConsumption = Integer.parseInt(vehFuelCon.getValue().toString());
            maxWeight = Double.parseDouble(vehMaxLoad.getValue().toString());
            conLength = Double.parseDouble(vehLength.getText().trim());
            conWidth = Double.parseDouble(vehWidth.getText().trim());
            conHeight = Double.parseDouble(vehHeight.getText().trim());

            ValidationResult result = vc.validateVehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
            if (!result.isValid()) {
                throw new RuntimeException(result.getMessage());
            }
            return new Vehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Vehicle Inputs have Mismatched Datatypes: {}", ex.getMessage());
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Vehicle Inputs have Missing Info: {}", ex.getMessage());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Create New Vehicle: {}", ex.getMessage());
        }
        return null;
    }

    private Product makeProduct() {
        String name;
        String currency;
        double value;
        double weight;
        double length;
        double width;
        double height;

        try {
            name = prodName.getText().trim();
            currency = (String) currencyCombo.getSelectedItem();
            value = Double.parseDouble(prodValue.getText().trim());
            weight = Double.parseDouble(prodWeight.getText().trim());
            length = Double.parseDouble(prodLength.getText().trim());
            width = Double.parseDouble(prodWidth.getText().trim());
            height = Double.parseDouble(prodHeight.getText().trim());

            ValidationResult result = vc.validateProduct(name, currency, value, weight, length, width, height);
            if (!result.isValid()) {
                throw new RuntimeException(result.getMessage());
            }
            return new Product(name, currency, value, weight, length, width, height);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Product Inputs have Mismatched Datatypes: {}", ex.getMessage());
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Product Inputs have Missing Info: {}", ex.getMessage());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Create New Product: {}", ex.getMessage());
        }
        return null;
    }

    private ArrayList<Product> getProducts() throws RuntimeException {
        String name;
        String currency;
        double value;
        double weight;
        double length;
        double width;
        double height;
        ArrayList<Product> products = new ArrayList<>();

        if (prodTable.getRowCount() <= 0) {
            throw new RuntimeException("There are no Products in Table.");
        }

        for (int i = 0; i < prodTable.getRowCount(); i++) {
            name = prodTable.getValueAt(i, 0).toString();
            currency = prodTable.getValueAt(i, 1).toString();
            value = Double.parseDouble(prodTable.getValueAt(i, 2).toString());
            weight = Double.parseDouble(prodTable.getValueAt(i, 3).toString());
            length = Double.parseDouble(prodTable.getValueAt(i, 4).toString());
            width = Double.parseDouble(prodTable.getValueAt(i, 5).toString());
            height = Double.parseDouble(prodTable.getValueAt(i, 6).toString());
            Product product = new Product(name, currency, value, weight, length, width, height);
            products.add(product);
        }
        return products;
    }

    private Vehicle getVehicle() throws RuntimeException {
        try {
            String text = vehicleDoc.getText(0, vehicleDoc.getLength());
            if (text.isEmpty()) {
                throw new RuntimeException("There is no Vehicle Selected.");
            }
            String[] lines = text.split("\n");

            int fuelConsumption = Integer.parseInt(lines[0].replaceAll("[^0-9]", ""));
            double maxWeight = Double.parseDouble(lines[1].replaceAll("[^0-9.]", ""));
            double conLength = Double.parseDouble(lines[2].replaceAll("[^0-9.]", ""));
            double conWidth = Double.parseDouble(lines[3].replaceAll("[^0-9.]", ""));
            double conHeight = Double.parseDouble(lines[4].replaceAll("[^0-9.]", ""));

            return new Vehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(null, "There was an Error Retrieving Vehicle Details", "Vehicle Retrieval Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to retrieve Vehicle Info from Panel: {}", ex.getMessage());
            return null;
        }
    }

    private ArrayList<Place> getPlaces() throws RuntimeException {
        String streetAddress;
        String town;
        String city;
        String postcode;
        ArrayList<Place> places = new ArrayList<>();

        if (routeTable.getRowCount() <= 0) {
            throw new RuntimeException("There are no Routes in Table.");
        }

        for (int i = 0; i < routeTable.getRowCount(); i++) {
            streetAddress = routeTable.getValueAt(i, 0).toString();
            town = routeTable.getValueAt(i, 1).toString();
            city = routeTable.getValueAt(i, 2).toString();
            postcode = routeTable.getValueAt(i, 3).toString();
            Place place = new Place(streetAddress, town, city, postcode);
            places.add(place);
        }

        return places;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
