package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.FrontController;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.model.CheckoutRecordEntry;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class CheckoutWindow implements MessageableWindow {

    private final FrontController frontController = new SystemController();

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel checkoutPanel;
    private JLabel lbnMemberID;
    private JTextField txtMemberID;
    private JLabel lbnBookISBN;
    private JTextField txtISBN;
    private JButton btnCheckout;
    private JPanel recordEntryPanel;
    private JTable tblRecordEntry;
    private JScrollPane checkoutScrollPane;

    private JScrollPane scrollPane;

    private final DefaultTableModel model;
    public CheckoutWindow() {
        model = new DefaultTableModel(null, Util.DEFAULT_COLUMN_HEADERS);
        tblRecordEntry.setModel(model);
        tblRecordEntry.setShowGrid(true);
        txtMemberID.getDocument().addDocumentListener(new ValueChangedListener());
        txtISBN.getDocument().addDocumentListener(new ValueChangedListener());

        btnCheckout.addActionListener(e -> {
            try {
                LibraryMember libraryMember = frontController.checkoutBook(txtMemberID.getText().trim(), txtISBN.getText().trim());
                displayInfo("Checking out '" + txtISBN.getText().trim() + "' was success");
                List<String[]> rows = Util.parseCheckoutRecordEntryRows(libraryMember);
                rows.forEach(model::addRow);
                tblRecordEntry.updateUI();
            } catch (BookCheckoutException ex) {
                displayError(ex.getMessage());
            }
        });
    }

    private void updateCheckoutButton() {
        btnCheckout.setEnabled(false);
        if(!txtMemberID.getText().trim().isEmpty() && !txtISBN.getText().trim().isEmpty()) {
            btnCheckout.setEnabled(true);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void updateData() {

    }

    class ValueChangedListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateCheckoutButton();
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            updateCheckoutButton();
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
            updateCheckoutButton();
        }
    }
}
