package edu.miu.mpp.library.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginWindow implements ActionListener, KeyListener, DocumentListener{
    private JPanel mainPane;
    private JTextField tfUserName;
    private JPasswordField tfPassword;
    private JButton loginButton;

    private LibAppWindow libAppWindow;

    public LoginWindow(LibAppWindow libAppWindow) {
        tfUserName.getDocument().addDocumentListener(this);
        tfPassword.getDocument().addDocumentListener(this);
        loginButton.setEnabled(false);
        this.libAppWindow = libAppWindow;
        // add the listener to the textField
        loginButton.addKeyListener(this);
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            libAppWindow.authenUser(tfUserName.getText().trim(), tfPassword.getPassword());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private void updateLoginButton() {
        loginButton.setEnabled(false);
        if(tfPassword.getPassword().length > 0 && !tfUserName.getText().trim().isEmpty()) {
            loginButton.setEnabled(true);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateLoginButton();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateLoginButton();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateLoginButton();
    }
}


