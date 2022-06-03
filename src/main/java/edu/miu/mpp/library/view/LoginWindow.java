package edu.miu.mpp.library.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginWindow implements ActionListener, KeyListener {
    private JPanel mainPane;
    private JTextField tfUserName;
    private JPasswordField tfPassword;
    private JButton loginButton;

    private LibAppWindow libAppWindow;

    public LoginWindow(LibAppWindow libAppWindow) {

        this.libAppWindow = libAppWindow;
        // add the listener to the textField
        loginButton.addKeyListener(this);
        loginButton.addActionListener(e ->
                libAppWindow.authenUser(tfUserName.getText().trim(), tfPassword.getPassword())
        );
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            libAppWindow.authenUser(tfUserName.getText().trim(), tfPassword.getPassword());
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

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

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
