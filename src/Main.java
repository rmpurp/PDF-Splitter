import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        JFileChooser jfc = new JFileChooser();
        int result = jfc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            PDFManager p = new PDFManager(jfc.getSelectedFile().getAbsolutePath());
            UIWindow ui;
            JFrame frame = new JFrame("UIWindow");
            frame.setContentPane((ui = new UIWindow(p)).getMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            ui.importImages(50, 100);
            //ui.importImages(p.renderPages(50));
            //ui.createRenders(100);
        }
    }
}
