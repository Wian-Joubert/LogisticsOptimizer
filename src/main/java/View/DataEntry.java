package View;

import Controller.DistanceMatrixController;
import Controller.RouteController;
import Controller.ValidationController;
import Model.*;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Set;

public class DataEntry {
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

    private DefaultTableModel productModel;
    private DefaultTableModel routeModel;
    private StyledDocument vehicleDoc;
    private final Logger logger = LoggerFactory.getLogger(DataEntry.class);
    ValidationController vc = new ValidationController();

    public DataEntry() {
        initTables();

        prodAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = makeProduct();
                if (product != null){
                    addProductToTable(product);
                }
            }
        });
        vehAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicle = makeVehicle();
                if (vehicle != null){
                    addVehicleToPane(vehicle);
                }
            }
        });
        roAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Place place = makePlace();
                if (place != null){
                    addRouteToTable(place);
                }
                clearRoute();
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
                    StandardContainers container = StandardContainers.getContainerByName(selectedItem);
                    if (container != null) {
                        vehLength.setText(String.valueOf(container.getLength()));
                        vehWidth.setText(String.valueOf(container.getWidth()));
                        vehHeight.setText(String.valueOf(container.getHeight()));
                        vehCustomContainer.setSelected(false);
                        vehStandardContainer.setEnabled(true);
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
                if (selectedRow != -1){
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
                if (selectedRow != -1){
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
                    if (selectedRow == -1){
                        throw new RuntimeException("Select a row before Updating.");
                    }
                    String[] rowValues = {
                            prodName.getText().trim(),
                            (String) currencyCombo.getSelectedItem(),
                            prodValue.getText().trim(),
                            prodWeight.getText().trim(),
                            prodLength.getText().trim(),
                            prodWidth.getText().trim(),
                            prodHeight.getText().trim()
                    };
                    ValidationResult result = vc.validateProduct(rowValues[0], rowValues[1],
                            Double.parseDouble(rowValues[2]), Double.parseDouble(rowValues[3]),
                            Double.parseDouble(rowValues[4]), Double.parseDouble(rowValues[5]),
                            Double.parseDouble(rowValues[6]));
                    if (!result.isValid()){
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
                    if (selectedRow == -1){
                        throw new RuntimeException("Select a row before Updating.");
                    }
                    String[] rowValues = {
                            roStreet.getText().trim(),
                            roTown.getText().trim(),
                            roCity.getText().trim(),
                            roPostCode.getText().trim(),
                    };
                    ValidationResult result = vc.validatePlace(rowValues[0], rowValues[1], rowValues[2], rowValues[3]);

                    if (!result.isValid()){
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
                    if (selectedRow == -1){
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
                    if (selectedRow == -1){
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
                ArrayList<Place> places = getPlaces();
                DistanceMatrixController distanceMatrixController = new DistanceMatrixController();
                DistanceMatrix distanceMatrix = distanceMatrixController.distanceMatrixCall(places);
                RouteController routeController = new RouteController();
                routeController.calculateTraveling(distanceMatrix);
            }
        });
        btnSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settingsFrame = new Settings();
                settingsFrame.setVisible(true);
            }
        });
    }

    private void initTables(){
        //Init Tables
        String[] prodColumns = {"Name", "Currency", "Value", "Weight", "Length", "Width", "Height", "Volume"};
        productModel = new DefaultTableModel(prodColumns, 0);
        prodTable.setModel(productModel);

        String[] routeColumns = {"Street", "City", "Town", "Post Code"};
        routeModel = new DefaultTableModel(routeColumns, 0);
        routeTable.setModel(routeModel);

        vehicleDoc = vehDetailsPane.getStyledDocument();
    }

    private void clearRoute(){
        roStreet.setText(null);
        roTown.setText(null);
        roCity.setText(null);
        roPostCode.setText(null);
        routeTable.clearSelection();
    }

    private void clearVehicle(){
        vehFuelCon.setValue(0);
        vehMaxLoad.setValue(0);
        vehCustomContainer.setSelected(false);
        vehStandardContainer.setSelectedIndex(0);
        vehLength.setText(null);
        vehWidth.setText(null);
        vehHeight.setText(null);
    }

    private void clearProduct(){
        prodName.setText(null);
        currencyCombo.setSelectedIndex(0);
        prodValue.setText(null);
        prodWeight.setText(null);
        prodLength.setText(null);
        prodWidth.setText(null);
        prodHeight.setText(null);
        prodTable.clearSelection();
    }

    private String[] getRowValues(JTable table, int selectedRow){
        String[] rowValues = new String[table.getColumnCount()];
        for (int i = 0; i < rowValues.length; i++) {
            rowValues[i] = table.getValueAt(selectedRow, i).toString();
        }
        return rowValues;
    }

    private void addProductToTable(Product product){
        Object[] rowData = {
                product.getName(),
                product.getCurrency(),
                product.getValue(),
                product.getWeight(),
                product.getLength(),
                product.getWidth(),
                product.getHeight(),
                product.getVolume()
        };
        productModel.addRow(rowData);
    }

    private void addRouteToTable(Place place){
        Object[] rowData = {
                place.getStreet(),
                place.getTown(),
                place.getCity(),
                place.getPostcode()
        };
        routeModel.addRow(rowData);
    }

    private void addVehicleToPane(Vehicle vehicle){
        String[] vehicleDetails = {
                String.format("Fuel Consumption:\t %skm per Litre\n", vehicle.getFuelConsumption()),
                String.format("Max Weight:\t\t %skg\n", vehicle.getMaxWeight()),
                String.format("Container Length:\t %scm\n", vehicle.getConLength()),
                String.format("Container Width:\t %scm\n", vehicle.getConWidth()),
                String.format("Container Height:\t %scm\n", vehicle.getConHeight()),
                String.format("Container Volume:\t %sm^3\n", vehicle.getConVolume()),
        };
        try {
            vehicleDoc.remove(0, vehicleDoc.getLength());
            for (String detail : vehicleDetails){
                vehicleDoc.insertString(vehicleDoc.getLength(), detail, null);
            }
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(null, "There was an Error Displaying Vehicle Details", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Display New Vehicle: {}", ex.getMessage());
        }
    }

    private Place makePlace(){
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
            if (!result.isValid()){
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

    private Vehicle makeVehicle(){
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
            if (!result.isValid()){
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
            if (!result.isValid()){
                throw new RuntimeException(result.getMessage());
            }
            return new Product(name, currency, value, weight, length, width, height);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Product Inputs have Mismatched Datatypes: {}", ex.getMessage());
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Required Product Inputs have Missing Info: {}", ex.getMessage());
        } catch (RuntimeException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Product Input Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Failed to Create New Product: {}", ex.getMessage());
        }
        return null;
    }

    private ArrayList<Product> getProducts() throws RuntimeException{
        String name;
        String currency;
        double value;
        double weight;
        double length;
        double width;
        double height;
        ArrayList<Product> products = new ArrayList<>();

        if (prodTable.getRowCount() <= 0){
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

    private Vehicle getVehicle() throws RuntimeException{
        try {
            String text = vehicleDoc.getText(0, vehicleDoc.getLength());
            if (text.isEmpty()){
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

    private ArrayList<Place> getPlaces() throws RuntimeException{
        String streetAddress;
        String town;
        String city;
        String postcode;
        ArrayList<Place> places = new ArrayList<>();

        if (routeTable.getRowCount() <= 0){
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

    public JPanel getRootPanel(){
        return rootPanel;
    }
}
