import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        PDFManager p = new PDFManager("ch5.pdf");
        UIWindow ui;
        JFrame frame = new JFrame("UIWindow");
        frame.setContentPane((ui = new UIWindow(p)).getMain());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        ui.importImages(p.renderPages(50));
        ui.createRenders(100);
    }
}
