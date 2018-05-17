import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.image.BufferedImage;

public class UIWindow {
    private JScrollPane scrollPane;
    private JPanel topPanel;
    private JSpinner spinner1;
    private JButton createButton;
    private JPanel mainPanel;
    private JPanel scrollPanel;
    private JSlider slider1;
    private JButton deleteButton;
    private int lastActualizedSize;
    private List<BufferedImage> images;
    private List<JLabel> labels;
    private PDFManager manager;
    private JFileChooser jfc;

    public void importImages(BufferedImage[] bufferedImages) {
        images = new ArrayList<BufferedImage>();
        labels = new ArrayList<>();
        images.addAll(Arrays.asList(bufferedImages));
    }

    public void createRenders(int height) {
        JLabel l;
        for (BufferedImage bi : images) {
            int width = height * bi.getWidth() / bi.getHeight();
            l = new JLabel(new ImageIcon(getScaledImage(bi, width, height)));
            setMouseListener(l);
            labels.add(l);
            scrollPanel.add(l);
        }
        scrollPanel.updateUI();
    }



    private void setMouseListener(JLabel j) {
        j.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Pressed");
                spinner1.setValue(1 + labels.indexOf(j));
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void resizeRenders(int height) {
        synchronized (scrollPanel.getTreeLock()) {
            for (int i = 0; i < labels.size(); i++) {
                JLabel j = labels.get(i);
                Icon icon = j.getIcon();
                int width = height * images.get(i).getWidth() / images.get(i).getHeight();
                j.setIcon(new ImageIcon(getScaledImage(images.get(i), width, height)));
            }
            scrollPanel.updateUI();
        }
    }

    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }


    public UIWindow(PDFManager m) {
        manager = m;
        scrollPanel.setLayout(new WrapLayout());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        jfc = new JFileChooser();

        createSliderListener();
        createDeleteButtonListener();
        createCreateButtonListener();
    }

    private void createCreateButtonListener() {
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfc.setSelectedFile(new File("untitled.pdf"));
                int userSelection = jfc.showSaveDialog(topPanel);
                int numToDelete = (Integer) spinner1.getValue();
                numToDelete = Math.min(numToDelete, manager.getNumPages());
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    try {
                        File toSave = jfc.getSelectedFile();
                        PDFManager.savePDF(manager.makePrefixPDF(numToDelete, (i) -> {
                            scrollPanel.remove(0);
                            images.remove(0);
                            labels.remove(0);
                            scrollPanel.updateUI();
                        }), toSave);
                    } catch (IOException ex) {
                    }
                }
            }

        });
    }

    private void createDeleteButtonListener() {
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int numToDelete = (Integer) spinner1.getValue();
                    numToDelete = Math.min(numToDelete, manager.getNumPages());
                    if (numToDelete <= 0) {
                        return;
                    }
                    manager.deletePrefix(numToDelete, (i) -> {
                        scrollPanel.remove(0);
                        images.remove(0);
                        labels.remove(0);
                        scrollPanel.updateUI();
                    });
                } catch (IOException ex) {
                    System.out.println("flip");
                }
            }
        });
    }

    private void createSliderListener() {
        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int curSize = slider1.getValue();
                if (Math.abs(curSize - lastActualizedSize) > 5) {
                    resizeRenders(curSize);
                    lastActualizedSize = curSize;
                }
            }
        });
    }

    public JPanel getMain() {
        return mainPanel;
    }

    /*
    public static void main(String[] args) {
        JFrame frame = new JFrame("UIWindow");
        frame.setContentPane(new UIWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    */
}
