import View.DataEntry;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Program start error: " + ex.getMessage(),
                    "Start Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        DataEntry dataEntryForm = new DataEntry();
        dataEntryForm.setThisForm(dataEntryForm);
        dataEntryForm.setVisible(true);
    }
}
