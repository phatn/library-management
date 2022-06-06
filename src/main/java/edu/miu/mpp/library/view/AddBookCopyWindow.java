package edu.miu.mpp.library.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.exception.BookCopyAddException;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.BookCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

public class AddBookCopyWindow implements MessageableWindow {

    private static final Logger LOG = LoggerFactory.getLogger(AddBookCopyWindow.class);
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
                setWelcomeUser();
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
        LOG.info("All Books: {}", bookMap);
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
        Book book;
        try {
            book = systemController.addBookCopy(selectedIsbn);
        } catch (BookCopyAddException ex) {
            displayError(ex.getMessage());
            return;
        }
        updateCopy(selectedIsbn);

        DefaultTableModel bookModel = (DefaultTableModel) bookTable.getModel();
        bookModel.setValueAt(book.getCopies().length, selectedRow, 2);
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
        mainPanel.setLayout(new GridLayoutManager(3, 4, new Insets(10, 5, 0, 5), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Filter Books by ISBN");
        mainPanel.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        isbnTxt = new JTextField();
        isbnTxt.setColumns(15);
        isbnTxt.setText("");
        mainPanel.add(isbnTxt, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        splitPane = new JSplitPane();
        splitPane.setOrientation(0);
        mainPanel.add(splitPane, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(162, 100));
        splitPane.setRightComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, 100), null, null, 0, false));
        copyTable = new JTable();
        scrollPane1.setViewportView(copyTable);
        addCopyButton = new JButton();
        addCopyButton.setMargin(new Insets(0, 10, 0, 10));
        addCopyButton.setText("Add Copy");
        panel1.add(addCopyButton, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1), new Dimension(10, -1), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setMinimumSize(new Dimension(17, 100));
        splitPane.setLeftComponent(scrollPane2);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        bookTable = new JTable();
        bookTable.setMinimumSize(new Dimension(100, 100));
        bookTable.setToolTipText("Select a book to see list of copies.");
        scrollPane2.setViewportView(bookTable);
        final JLabel label2 = new JLabel();
        label2.setText("Add Book Copy");
        mainPanel.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
