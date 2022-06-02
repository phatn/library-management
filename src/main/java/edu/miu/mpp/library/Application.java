package edu.miu.mpp.library;

import edu.miu.mpp.library.view.LibAppWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Application {
    public static final String OUTPUT_DIR = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "edu"
            + File.separator + "miu" + File.separator + "mpp" + File.separator + "library" + File.separator + "dao" + File.separator + "storage";
    public static void main(String[] args) {
        System.out.println(OUTPUT_DIR);
        SwingUtilities.invokeLater(() -> {
            LibAppWindow libAppWindow = new LibAppWindow();
            libAppWindow.setSize(400, 200);
            libAppWindow.setTitle("MPP Library Application");
            libAppWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            centerFrameOnDesktop(libAppWindow);
            libAppWindow.setVisible(true);
        });
    }
    public static void centerFrameOnDesktop(Component frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int height = toolkit.getScreenSize().height;
        int width = toolkit.getScreenSize().width;
        int frameHeight = frame.getSize().height;
        int frameWidth = frame.getSize().width;
        frame.setLocation(((width - frameWidth) / 2), (height - frameHeight) / 3);
    }
}
