package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.FrontController;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.model.CheckoutRecordEntry;
import edu.miu.mpp.library.model.LibraryMember;

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
    private final String[] DEFAULT_COLUMN_HEADERS
            = {"Member ID", "ISBN", "Book Copy ID", "Checkout Date", "Due Date"};

    public CheckoutWindow() {
        model = new DefaultTableModel(null, DEFAULT_COLUMN_HEADERS);
        tblRecordEntry.setModel(model);
        tblRecordEntry.setShowGrid(true);
        txtMemberID.getDocument().addDocumentListener(new ValueChangedListener());
        txtISBN.getDocument().addDocumentListener(new ValueChangedListener());

        btnCheckout.addActionListener(e -> {
            try {
                LibraryMember libraryMember = frontController.checkoutBook(txtMemberID.getText().trim(), txtISBN.getText().trim());
                displayInfo("Checking out '" + txtISBN.getText().trim() + "' was success");
                List<String[]> rows = parseRows(libraryMember);
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
    private List<String[]> parseRows(LibraryMember libraryMember) {
        List<CheckoutRecordEntry> checkoutRecordEntries = libraryMember.getCheckoutRecord().getCheckoutRecordEntries();
        List<String[]> rows = new ArrayList<>();
        checkoutRecordEntries.forEach(checkoutRecordEntry -> {
            String[] row = new String[DEFAULT_COLUMN_HEADERS.length];
            row[0] = libraryMember.getMemberId();
            row[1] = checkoutRecordEntry.getIsbn();
            row[2] = checkoutRecordEntry.getBookCopyId();
            row[3] = checkoutRecordEntry.getCheckoutDate().toString();
            row[4] = checkoutRecordEntry.getDueDate().toString();
            rows.add(row);
        });

        return rows;
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
