package View;

import javax.swing.*;

public class DataEntry {
    private JPanel rootPanel;
    private JLabel lblTitle;
    private JPanel buttonPanel;
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
    private JList vehDetailsList;
    private JSpinner vehHeight;
    private JSpinner vehWidth;
    private JSpinner vehLength;
    private JSpinner vehFuelCon;
    private JSpinner vehMaxLoad;
    private JButton vehAdd;
    private JButton vehSave;
    private JButton vehReset;

    public JPanel getRootPanel(){
        return rootPanel;
    }

}
