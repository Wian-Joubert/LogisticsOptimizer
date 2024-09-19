package View;

import Controller.FileController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField apiKey;
    private JButton saveAsKeyButton;
    private JTextField textField3;
    private JButton saveFileTableDirectory;
    private JButton browseFileTable;
    private JTextField apiKeyField;
    private JButton saveKey;

    public Settings() {
        this.setTitle("Settings");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Close only the Settings window
        this.setSize(500, 400);
        this.setContentPane(panel1);
        this.setLocationRelativeTo(null);

        FileController fileController = new FileController();

        saveKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String api_key;
                api_key = apiKeyField.getText().trim();
                fileController.writeApiKey(api_key);
                JOptionPane.showMessageDialog(null, "API Key is:" + fileController.readApiKey());
            }
        });
    }
}