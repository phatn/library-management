package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.*;
import edu.miu.mpp.library.util.Strings;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static edu.miu.mpp.library.view.LibAppWindow.statusBar;

public class DueDateBookCopyWindow {
    private JPanel mainPanel;
    private JTextField isbnTxt;
    private JTable bookTable;
    private JTable copyTable;
    private JSplitPane splitPane;
    private JTextField memberId;
    private JButton searchButton;

    private List<CheckoutRecordEntry> checkoutRecordEntryList;
    private final String[] DEFAULT_BOOK_HEADERS = {"ISBN", "Title", "Number of Copies"};
    private final String[] DEFAULT_COPY_HEADERS = {"Copy Number", "Due date", "Overdue"};

    private Map<String, Book> bookMap;

    private final SystemController systemController;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public DueDateBookCopyWindow() {
        systemController = new SystemController();
        bookMap = new HashMap<>();

        init();

        // fill default data
        fillBookTableData(bookMap);

        alignColumn();

        registerEventListener();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String getMemberId = memberId.getText().strip();
                Map<String, LibraryMember> mapMembers = systemController.getAllLibraryMembers();
                if (!mapMembers.containsKey(getMemberId)) {
                    statusBar.setText(String.format(Strings.CANNOT_FIND_MEMBER_ID, getMemberId));
                    statusBar.setForeground(Util.ERROR_MESSAGE_COLOR);
                } else {
                    LibraryMember member = mapMembers.get(getMemberId);
                    checkoutRecordEntryList = member.getCheckoutRecord().getCheckoutRecordEntries();
                    List<String> ISBNs = new LinkedList<>();
                    for (CheckoutRecordEntry tempt : checkoutRecordEntryList) {
                        ISBNs.add(tempt.getIsbn());
                    }
                    Map<String, Book> allBooks = systemController.getAllBooks();
                    for (String key : allBooks.keySet()) {
                        if (ISBNs.contains(key)) bookMap.put(key, allBooks.get(key));
                    }
                    fillBookTableData(bookMap);
                }
            }
        });
    }

    private void init() {
        splitPane.setResizeWeight(0.8);

        // Disable editing on tables
        bookTable.setDefaultEditor(Object.class, null);
        copyTable.setDefaultEditor(Object.class, null);
        // Show cell border
        bookTable.setShowGrid(true);
        bookTable.setGridColor(Color.GRAY);
        copyTable.setShowGrid(true);
        copyTable.setGridColor(Color.GRAY);

        // Set tables' header
        setHeader(bookTable, DEFAULT_BOOK_HEADERS);
        setHeader(copyTable, DEFAULT_COPY_HEADERS);
    }

    private void registerEventListener() {
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedIsbn = (String) bookTable.getValueAt(bookTable.getSelectedRow(), 0);
                    System.out.println(selectedIsbn);
                    updateCopy(selectedIsbn);
                } else {
                    updateCopy(null);
                }
            }
        });

        // add event listener to filter books
        isbnTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterBook(isbnTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterBook(isbnTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterBook(isbnTxt.getText());
            }
        });
    }

    private void alignColumn() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel bookColumnModel = bookTable.getColumnModel();
        bookColumnModel.getColumn(0).setCellRenderer(centerRenderer);
        bookColumnModel.getColumn(2).setCellRenderer(centerRenderer);

        TableColumnModel copyColumnModel = copyTable.getColumnModel();
        copyColumnModel.getColumn(0).setCellRenderer(centerRenderer);
        copyColumnModel.getColumn(1).setCellRenderer(centerRenderer);
        copyColumnModel.getColumn(2).setCellRenderer(centerRenderer);
    }

    private void setHeader(JTable table, String[] headers) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (String header : headers) {
            model.addColumn(header);
        }
    }

    private void fillBookTableData(Map<String, Book> tableData) {
        System.out.println(bookMap);
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);
        for (Book book : tableData.values()) {
            Object[] row = {book.getIsbn(), book.getTitle(), book.getCopies().length};
            System.out.println(Arrays.toString(row));
            model.addRow(row);
        }
    }

    private void filterBook(String text) {
        // clear entries
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);

        // set new values
        if (text == null || text.length() == 0) {
            fillBookTableData(bookMap);
            return;
        }

        HashMap<String, Book> filteredBooks = new HashMap<>();
        for (Map.Entry<String, Book> entry : bookMap.entrySet()) {
            if (entry.getKey().contains(text)) {
                filteredBooks.put(entry.getKey(), entry.getValue());
            }
        }
        fillBookTableData(filteredBooks);
    }

    private void updateCopy(String isbn) {
        DefaultTableModel model = (DefaultTableModel) copyTable.getModel();
        model.setRowCount(0);
        if (isbn != null) {
            for (CheckoutRecordEntry checkoutRecordEntry : checkoutRecordEntryList) {
                if (checkoutRecordEntry.getIsbn().equalsIgnoreCase(isbn)) {
                    Object[] row = {checkoutRecordEntry.getBookCopyId(), checkoutRecordEntry.getDueDate()
                            , checkoutRecordEntry.getDueDate().isBefore(LocalDate.now())};
                    model.addRow(row);
                }
            }
        }
    }
}
