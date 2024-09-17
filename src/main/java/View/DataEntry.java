package View;

import Controller.ValidationController;
import Model.Place;
import Model.Product;
import Model.ValidationResult;
import Model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.*;

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
    private JSpinner prodLength;
    private JSpinner prodWidth;
    private JSpinner prodHeight;
    private JSpinner prodWeight;
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
    private JSpinner vehHeight;
    private JSpinner vehWidth;
    private JSpinner vehLength;
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

    private final DefaultTableModel productModel;
    private final DefaultTableModel routeModel;
    private final StyledDocument vehicleDoc;
    ValidationController vc = new ValidationController();

    public DataEntry() {
        //Init Tables
        String[] prodColumns = {"Name", "Currency", "Value", "Weight", "Length", "Width", "Height", "Volume"};
        productModel = new DefaultTableModel(prodColumns, 0);
        prodTable.setModel(productModel);

        String[] routeColumns = {"Street", "City", "Town", "Post Code"};
        routeModel = new DefaultTableModel(routeColumns, 0);
        routeTable.setModel(routeModel);

        vehicleDoc = vehDetailsPane.getStyledDocument();

        prodAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = makeProduct();
                JOptionPane.showMessageDialog(null, product.toString());
                addProductToTable(product);
            }
        });
        vehAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicle = makeVehicle();
                JOptionPane.showMessageDialog(null, vehicle.toString());
                addVehicleToPane(vehicle);
            }
        });
        roAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Place place = makePlace();
                JOptionPane.showMessageDialog(null, place.toString());
                addRouteToTable(place);
            }
        });

        prodTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Continue Here
                super.mouseClicked(e);
            }
        });
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
        for (String detail : vehicleDetails){
            try {
                vehicleDoc.insertString(vehicleDoc.getLength(), detail, null);
            } catch (BadLocationException ex) {
                JOptionPane.showMessageDialog(null, "There was an Error Displaying Vehicle Details", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
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
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Route Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Route Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private Vehicle makeVehicle(){
        int fuelConsumption;
        int maxWeight;
        int conLength;
        int conWidth;
        int conHeight;

        try {
            fuelConsumption = Integer.parseInt(vehFuelCon.getValue().toString());
            maxWeight = Integer.parseInt(vehMaxLoad.getValue().toString());
            conLength = Integer.parseInt(vehLength.getValue().toString());
            conWidth = Integer.parseInt(vehWidth.getValue().toString());
            conHeight = Integer.parseInt(vehHeight.getValue().toString());

            ValidationResult result = vc.validateVehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
            if (!result.isValid()){
                throw new RuntimeException(result.getMessage());
            }
            return new Vehicle(fuelConsumption, maxWeight, conLength, conWidth, conHeight);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Vehicle Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private Product makeProduct() {
        String name;
        String currency;
        double value;
        int weight;
        int length;
        int width;
        int height;

        try {
            name = prodName.getText().trim();
            currency = (String) currencyCombo.getSelectedItem();
            value = Double.parseDouble(prodValue.getText().trim());
            weight = Integer.parseInt(prodWeight.getValue().toString());
            length = Integer.parseInt(prodLength.getValue().toString());
            width = Integer.parseInt(prodWidth.getValue().toString());
            height = Integer.parseInt(prodHeight.getValue().toString());

            ValidationResult result = vc.validateProduct(name, currency, value, weight, length, width, height);
            if (!result.isValid()){
                throw new RuntimeException(result.getMessage());
            }
            return new Product(name, currency, value, weight, length, width, height);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Incorrect data format/s.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Some Inputs are empty.", "Product Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (RuntimeException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Product Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /*
    private ArrayList<Product> makeProductList(){
        //make list
        //return list
    }
    private ArrayList<Vehicle> makeVehicleList(){
        //make list
        //return list
    }
    private ArrayList<Place> makePlaceList(){
        //make list
        //return list
    }
    */

    public JPanel getRootPanel(){
        return rootPanel;
    }
}
