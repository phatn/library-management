package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.Author;
import edu.miu.mpp.library.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddBookWindow implements MessageableWindow {
    private JPanel mainPanel;
    private JTextField titleTxt;

    private JFormattedTextField isbnTxt;
    private JList<Author> authorList;
    private JButton addButton;
    private JTable bookTable;
    private JComboBox maxCheckoutComboBox;

    private final String[] DEFAULT_BOOK_HEADERS = {"ISBN", "Title", "Authors", "Number of Copies"};
    private Map<String, Book> bookMap;
    private List<Author> authorDataList;
    private final SystemController systemController;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public AddBookWindow() {
        systemController = new SystemController();
        bookMap = systemController.getAllBooks();

        // Disable editing on tables
        bookTable.setDefaultEditor(Object.class, null);
        // Show cell border
        bookTable.setShowGrid(true);
        bookTable.setGridColor(Color.GRAY);
        Integer[] maxCheckoutDates = {7, 21};
        maxCheckoutComboBox.setModel(new DefaultComboBoxModel<Integer>(maxCheckoutDates));

        setHeader(bookTable, DEFAULT_BOOK_HEADERS);
        alignColumn();
        getBooks();
        getAuthors();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                getBooks();
                getAuthors();
            }
        });
    }

    private void getBooks() {
        bookMap = systemController.getAllBooks();
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);
        for (Book book : bookMap.values()) {
            List<Author> authors = book.getAuthors();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < authors.size(); i++) {
                sb.append(authors.get(i));
                if (i < authors.size() - 1) {
                    sb.append("; ");
                }
            }
            Object[] row = {book.getIsbn(), book.getTitle(), sb.toString(), book.getCopies().length};
            System.out.println(Arrays.toString(row));
            model.addRow(row);
        }
    }

    private void getAuthors() {
        authorDataList = systemController.getAuthors();
        DefaultListModel<Author> defaultListModel = new DefaultListModel<>();
        for (Author author : authorDataList) {
            defaultListModel.addElement(author);
        }
        authorList.setModel(defaultListModel);
    }

    private void setHeader(JTable table, String[] headers) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (String header : headers) {
            model.addColumn(header);
        }
    }

    private void alignColumn() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel bookColumnModel = bookTable.getColumnModel();
        bookColumnModel.getColumn(0).setCellRenderer(centerRenderer);
        bookColumnModel.getColumn(3).setCellRenderer(centerRenderer);
    }

    private void addBook() {
        String isbn = isbnTxt.getText();

        if (!isValidIsbn(isbn)) {
            displayError("Invalid ISBN number. Valid ISBN number must have 10 or 13 number digits.");
            return;
        }
        if (isBookExisted(isbn)) {
            displayError("A book with the ISBN existed.");
            return;
        }

        String title = titleTxt.getText();
        if (title == null || title.isEmpty()) {
            displayError("No title specified.");
            return;
        }


        int maxCheckoutLength = (int)maxCheckoutComboBox.getSelectedItem();

        List<Author> authors = authorList.getSelectedValuesList();

        if (authors.size() < 1) {
            displayError("No author selected.");
            return;
        }
        Book book = new Book(isbn, title, maxCheckoutLength, authors);
        bookMap.put(isbn, book);
        systemController.saveBooks(bookMap);
        displayInfo("Book added.");
        getBooks();
    }

    private boolean isValidIsbn(String isbn) {
        String regex = "^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(isbn).matches();
    }

    private boolean isBookExisted(String isbn) {
        for (Book book : bookMap.values()) {
            if (book.getIsbn().equals(isbn)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void displayError(String msg) {
        MessageableWindow.super.displayError(msg);
    }

    @Override
    public void updateData() {

    }
}
