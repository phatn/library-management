package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.BookCopy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddBookCopyWindow {
    private JPanel mainPanel;
    private JTextField isbnTxt;
    private JTable bookTable;
    private JTable copyTable;
    private JButton addCopyButton;
    private JSplitPane splitPane;

    private final String[] DEFAULT_BOOK_HEADERS = {"ISBN", "Title", "Number of Copies"};
    private final String[] DEFAULT_COPY_HEADERS = {"Copy Number", "Is Available"};

    private Map<String, Book> bookMap;

    private final SystemController systemController;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public AddBookCopyWindow() {
        systemController = new SystemController();
        bookMap = systemController.getAllBooks();

        splitPane.setResizeWeight(0.8);

        // Disable editing on tables
        bookTable.setDefaultEditor(Object.class, null);
        copyTable.setDefaultEditor(Object.class, null);
        // Show cell border
        bookTable.setShowGrid(true);
        bookTable.setGridColor(Color.GRAY);
        copyTable.setShowGrid(true);
        copyTable.setGridColor(Color.GRAY);

        addCopyButton.setEnabled(false);

        // Set tables' header
        setHeader(bookTable, DEFAULT_BOOK_HEADERS);
        setHeader(copyTable, DEFAULT_COPY_HEADERS);

        // fill default data
        fillBookTableData(bookMap);

        alignColumn();

        registerEventListener();
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                bookMap = systemController.getAllBooks();
                fillBookTableData(bookMap);
            }
        });
    }

    private void registerEventListener() {
        // add row selection event listener
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedIsbn = (String) bookTable.getValueAt(bookTable.getSelectedRow(), 0);
                    System.out.println(selectedIsbn);
                    updateCopy(selectedIsbn);
                    addCopyButton.setEnabled(true);
                } else {
                    updateCopy(null);
                    addCopyButton.setEnabled(false);
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

        // Add copy clicked
        addCopyButton.addActionListener(e -> {
            System.out.println("AddCopy clicked");
            addCopy();
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

    private void addCopy() {
        int selectedRow = bookTable.getSelectedRow();
        String selectedIsbn = (String) bookTable.getValueAt(selectedRow, 0);
        System.out.println(selectedIsbn);
        Book book = bookMap.get(selectedIsbn);
        if (book != null) {
            book.addCopy();
            updateCopy(selectedIsbn);
            systemController.saveBooks(bookMap);

            DefaultTableModel bookModel = (DefaultTableModel) bookTable.getModel();
            bookModel.setValueAt(book.getCopies().length, selectedRow, 2);
        }
    }

    private void updateCopy(String isbn) {
        Book book = bookMap.get(isbn);
        DefaultTableModel model = (DefaultTableModel) copyTable.getModel();
        model.setRowCount(0);
        if (book != null) {
            System.out.println(book);
            BookCopy[] bookCopies = book.getCopies();
            for (BookCopy bookCopy : bookCopies) {
                Object[] row = {bookCopy.getCopyNum(), bookCopy.isAvailable()};
                model.addRow(row);
            }
        }
    }
}
