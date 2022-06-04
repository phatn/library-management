package edu.miu.mpp.library.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import edu.miu.mpp.library.controller.FrontController;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        Map<String, LibraryMember> libraryMemberMap = frontController.getAllLibraryMembers();
        List<String> data = new ArrayList<>();
        data.add("--Select--");
        data.addAll(libraryMemberMap.values().stream()
                .map(l -> new MemberCboData(l.getMemberId(), l.getFirstName(), l.getLastName()).toString())
                .toList());

        model = new DefaultComboBoxModel(data.toArray(new String[data.size()]));
        cboMemberID.setModel(model);
        cboMemberID.updateUI();

        tableModel = new DefaultTableModel(null, DEFAULT_COLUMN_HEADERS);
        tblRecordEntry.setModel(tableModel);
        tblRecordEntry.setShowGrid(true);
        cboMemberID.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = e.getItem().toString();
                clearTable(tableModel);
                if(!selectedItem.contains("Select")) {
                    String memberID = selectedItem.substring(0, selectedItem.indexOf("-"));
                    LibraryMember libraryMember = frontController.getAllLibraryMembers().get(memberID);
                    if(libraryMember != null) {
                        List<String[]> rows = Util.parseCheckoutRecordEntryRows(libraryMember);
                        rows.forEach(tableModel::addRow);
                        btnPrint.setEnabled(rows.isEmpty() ? false : true);
                        tblRecordEntry.updateUI();
                    }
                }
            }
        });

        btnPrint.addActionListener(e -> {
            printConsole(tableModel.getDataVector());
            exportPDF(tblRecordEntry);
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void updateData() {

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
        for(int i = 0; i < allRows.size(); i++) {
            for(int j = 0; j < allRows.get(i).size(); j++) {
                System.out.print(allRows.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    private void exportPDF(JTable table) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String outputDir = System.getProperty("user.dir");
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        long date = zdt.toInstant().toEpochMilli();
        String fileName =  outputDir + File.separator + "checkout_record_" + date + ".pdf";
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            Graphics2D g2 = cb.createGraphicsShapes(500, 500);
            Shape oldClip = g2.getClip();
            g2.clipRect(0, 0, 500, 500);

            table.print(g2);
            g2.setClip(oldClip);

            g2.dispose();
            cb.restoreState();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            displayError("Error to export checkout record file!");
        }
        document.close();
        displayInfo("Checkout record file is at " + fileName);
    }
}
