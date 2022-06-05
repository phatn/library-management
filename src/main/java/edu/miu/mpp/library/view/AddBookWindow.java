package edu.miu.mpp.library.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.exception.BookAddException;
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

import static edu.miu.mpp.library.util.Util.isValidIsbn;

public class AddBookWindow implements MessageableWindow {
    private JPanel mainPanel;
    private JTextField titleTxt;

    private JFormattedTextField isbnTxt;
    private JList<Author> authorList;
    private JButton addButton;
    private JTable bookTable;
    private JComboBox maxCheckoutComboBox;

    private final String[] DEFAULT_BOOK_HEADERS = {"ISBN", "Title", "Authors", "Max Checkout", "Number of Copies"};
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
                setWelcomeUser();
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
            Object[] row = {book.getIsbn(), book.getTitle(), sb.toString(), book.getMaxCheckoutLength(), book.getCopies().length};
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
        bookColumnModel.getColumn(4).setCellRenderer(centerRenderer);
    }

    private void addBook() {
        if (!validateInputs()) {
            return;
        }

        String isbn = isbnTxt.getText();
        String title = titleTxt.getText();
        int maxCheckoutLength = (int) maxCheckoutComboBox.getSelectedItem();
        List<Author> authors = authorList.getSelectedValuesList();

        try {
            systemController.addBook(isbn, title, maxCheckoutLength, authors);
        } catch (BookAddException ex) {
            String msg = ex.getMessage();
            displayError(msg);
            return;
        }

        displayInfo("Book added.");
        getBooks();
        clearInputs();
    }

    private boolean validateInputs() {
        String isbn = isbnTxt.getText();

        if (!isValidIsbn(isbn)) {
            displayError("Invalid ISBN number. Valid ISBN number must have 10 or 13 number digits.");
            return false;
        }
        if (isBookExisted(isbn)) {
            displayError("A book with the ISBN existed.");
            return false;
        }

        String title = titleTxt.getText();
        if (title == null || title.isEmpty()) {
            displayError("No title specified.");
            return false;
        }


        List<Author> authors = authorList.getSelectedValuesList();
        if (authors.size() < 1) {
            displayError("No author selected.");
            return false;
        }

        return true;
    }

    private boolean isBookExisted(String isbn) {
        for (Book book : bookMap.values()) {
            if (book.getIsbn().equals(isbn)) {
                return true;
            }
        }

        return false;
    }

    private void clearInputs() {
        isbnTxt.setText("");
        titleTxt.setText("");
        authorList.clearSelection();
    }

    @Override
    public void displayError(String msg) {
        MessageableWindow.super.displayError(msg);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(13, 5, new Insets(10, 5, 0, 5), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Add Book");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, 2, 7, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Title");
        mainPanel.add(label2, new GridConstraints(3, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Max Checkout Length");
        mainPanel.add(label3, new GridConstraints(5, 0, 3, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("ISBN");
        mainPanel.add(label4, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isbnTxt = new JFormattedTextField();
        isbnTxt.setText("");
        isbnTxt.setToolTipText("ISBN: 10 or 13 number");
        mainPanel.add(isbnTxt, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        titleTxt = new JTextField();
        mainPanel.add(titleTxt, new GridConstraints(3, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        authorList = new JList();
        authorList.setSelectedIndex(-1);
        authorList.setToolTipText("Select authors");
        mainPanel.add(authorList, new GridConstraints(1, 4, 9, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        addButton = new JButton();
        addButton.setText("Add Book");
        mainPanel.add(addButton, new GridConstraints(10, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(12, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bookTable = new JTable();
        scrollPane1.setViewportView(bookTable);
        final JLabel label5 = new JLabel();
        label5.setText("Authors");
        mainPanel.add(label5, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Book List");
        mainPanel.add(label6, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        maxCheckoutComboBox = new JComboBox();
        mainPanel.add(maxCheckoutComboBox, new GridConstraints(5, 1, 3, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
