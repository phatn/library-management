package edu.miu.mpp.library.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow {
    private JPanel mainPane;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton loginButton;

    private LibAppWindow libAppWindow;

    public LoginWindow(LibAppWindow libAppWindow) {
        this.libAppWindow = libAppWindow;
        loginButton.addActionListener(e -> libAppWindow.addComponents());
    }

    public LibAppWindow getLibAppWindow() {
        return libAppWindow;
    }

    public void setLibAppWindow(LibAppWindow libAppWindow) {
        this.libAppWindow = libAppWindow;
    }

    public JPanel getMainPane() {
        return mainPane;
    }

    public void setMainPane(JPanel mainPane) {
        this.mainPane = mainPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
