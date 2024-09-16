import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Data Entry Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        View.DataEntry dataEntry = new View.DataEntry();
        frame.setContentPane(dataEntry.getRootPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
