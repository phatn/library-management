package edu.miu.mpp.library.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.model.*;
import edu.miu.mpp.library.util.Strings;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static edu.miu.mpp.library.view.LibAppWindow.statusBar;

public class DueDateBookCopyWindow implements MessageableWindow {
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
                    if (ISBNs.size() == 0) {
                        bookMap = new HashMap<>();
                    } else {
                        for (String key : allBooks.keySet()) {
                            if (ISBNs.contains(key)) bookMap.put(key, allBooks.get(key));
                        }
                    }
                    fillBookTableData(bookMap);
                }
            }
        });
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                setWelcomeUser();
                bookMap = new HashMap<>();
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
        mainPanel.setLayout(new GridLayoutManager(3, 7, new Insets(10, 5, 0, 5), -1, -1));
        splitPane = new JSplitPane();
        splitPane.setOrientation(0);
        mainPanel.add(splitPane, new GridConstraints(2, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(162, 100));
        splitPane.setRightComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, 100), null, null, 0, false));
        copyTable = new JTable();
        scrollPane1.setViewportView(copyTable);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1), new Dimension(10, -1), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setMinimumSize(new Dimension(17, 100));
        splitPane.setLeftComponent(scrollPane2);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        bookTable = new JTable();
        bookTable.setMinimumSize(new Dimension(100, 100));
        bookTable.setToolTipText("Select a book to see list of copies.");
        scrollPane2.setViewportView(bookTable);
        final JLabel label1 = new JLabel();
        label1.setText("Check book copy");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memberId = new JTextField();
        mainPanel.add(memberId, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Filter Books by ISBN");
        mainPanel.add(label2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        searchButton = new JButton();
        searchButton.setText("Search");
        mainPanel.add(searchButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Member Id");
        mainPanel.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isbnTxt = new JTextField();
        isbnTxt.setColumns(15);
        isbnTxt.setText("");
        mainPanel.add(isbnTxt, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
