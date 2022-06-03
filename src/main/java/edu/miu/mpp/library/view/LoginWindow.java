package edu.miu.mpp.library.view;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {
    private JPanel mainPane;
    private JTextField tfUserName;
    private JPasswordField tfPassword;
    private JButton loginButton;

    private LibAppWindow libAppWindow;

    public LoginWindow(LibAppWindow libAppWindow) {
        this.libAppWindow = libAppWindow;
        loginButton.addActionListener(e ->
                libAppWindow.authenUser(tfUserName.getText().trim(), tfPassword.getPassword())
        );
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
