package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.FrontController;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ExportCheckoutRecordWindow implements MessageableWindow {

    private final FrontController frontController = new SystemController();
    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel middlePanel;
    private JComboBox cboMemberID;
    private JTable tblRecordEntry;
    private JButton btnPrint;

    private final DefaultTableModel tableModel;

    private final String[] DEFAULT_COLUMN_HEADERS
            = {"Member ID", "ISBN", "Book Copy ID", "Checkout Date", "Due Date"};
    private ComboBoxModel model;

    public ExportCheckoutRecordWindow() {
        populateCboData();
        tableModel = new DefaultTableModel(null, DEFAULT_COLUMN_HEADERS);
        tblRecordEntry.setModel(tableModel);
        tblRecordEntry.setShowGrid(true);
        cboMemberID.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = e.getItem().toString();
                clearTable(tableModel);
                List<String[]> rows = new ArrayList<>();
                if(!selectedItem.contains("Select")) {
                    String memberID = selectedItem.substring(0, selectedItem.indexOf("-"));
                    LibraryMember libraryMember = frontController.getAllLibraryMembers().get(memberID);
                    if(libraryMember != null) {
                        rows = Util.parseCheckoutRecordEntryRows(libraryMember);
                        rows.forEach(tableModel::addRow);
                        tblRecordEntry.updateUI();
                    }
                }
                btnPrint.setEnabled(rows.isEmpty() ? false : true);

            }
        });

        btnPrint.addActionListener(e -> {
            printConsole(tableModel.getDataVector());
        });
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                setWelcomeUser();
                populateCboData();
            }
        });
    }

    void populateCboData() {
        Map<String, LibraryMember> libraryMemberMap = frontController.getAllLibraryMembers();
        List<String> data = new ArrayList<>();
        data.add("--Select--");
        data.addAll(libraryMemberMap.values().stream()
                .map(l -> new MemberCboData(l.getMemberId(), l.getFirstName(), l.getLastName()).toString())
                .toList());

        model = new DefaultComboBoxModel(data.toArray(new String[data.size()]));
        cboMemberID.setModel(model);
        cboMemberID.updateUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    class MemberCboData {
        private String memberID;

        private String firstName;

        private String lastName;

        public MemberCboData(String memberID, String firstName, String lastName) {
            this.memberID = memberID;
            this.firstName = firstName;
            this.lastName = lastName;
        }
        @Override
        public String toString() {
            return memberID + "-" + firstName + " " + lastName;
        }
    }

    private void clearTable(DefaultTableModel tableModel) {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
    }

    private void printConsole(Vector<Vector> allRows) {
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("%10s %15s %15s %20s %20s", "MEMBER ID", "ISBN", "BOOK COPY ID", "CHECKOUT DATE", "DUE DATE");
        System.out.println();
        System.out.println("---------------------------------------------------------------------------------------");

        for(int i = 0; i < allRows.size(); i++) {
            int colSize = allRows.get(i).size();
            System.out.format("%10s %15s %15s %20s %20s",
                    colSize > 0 ? allRows.get(i).get(0) : "",
                    colSize > 1 ? allRows.get(i).get(1) : "",
                    colSize > 2 ? allRows.get(i).get(2) : "",
                    colSize > 3 ? allRows.get(i).get(3) : "",
                    colSize > 4 ? allRows.get(i).get(4) : "");
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }
}
