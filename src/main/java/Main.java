import View.DataEntry;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        DataEntry dataEntryForm = new DataEntry();
        dataEntryForm.setThisForm(dataEntryForm);
        dataEntryForm.setVisible(true);
    }
}
