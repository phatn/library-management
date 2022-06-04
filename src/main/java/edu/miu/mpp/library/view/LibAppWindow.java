package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.FrontController;
import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.ListItem;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.util.Strings;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import java.awt.*;

public class LibAppWindow extends JFrame {

    private JPanel mainPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public JPanel getCards() {
        return cards;
    }

    private Role role;

    JPanel cards;
    JList<ListItem> linkList;

    ListItem checkoutItem = new ListItem(Strings.CHECK_OUT, true);
    ListItem addMemberItem = new ListItem(Strings.ADD_MEMBER, false);
    ListItem addBookCopyItem = new ListItem(Strings.ADD_BOOK_COPY, false);
    ListItem checkBookCopyItem = new ListItem(Strings.CHECK_BOOK_COPY, true);
    ListItem addBookItem = new ListItem(Strings.ADD_BOOK, false);

    ListItem logoutItem = new ListItem(Strings.LOG_OUT, true);

    ListItem[] sellerItems = {checkoutItem, addMemberItem};
    ListItem[] memberItems = {checkoutItem, addBookCopyItem, addBookItem};

    public ListItem[] getSellerItems() {
        return sellerItems;
    }

    public ListItem[] getMemberItems() {
        return memberItems;
    }

    public JList<ListItem> getLinkList() {
        return linkList;
    }

    public void setLinkList(JList<ListItem> linkList) {
        this.linkList = linkList;
    }

    public static JTextArea statusBar = new JTextArea(Strings.WELCOME);

    private final FrontController systemController = new SystemController();

    private JSplitPane innerPane;
    private JSplitPane outerPane;

    public LibAppWindow() {
        cards = new JPanel();
        cards.setLayout(new BorderLayout());
        LoginWindow loginWindow = new LoginWindow(this);
        //loginWindow.setLibAppWindow(this);
        cards.add(loginWindow.getMainPane(), BorderLayout.CENTER);
        this.add(cards, BorderLayout.CENTER);
    }

    public void createLinkLabels() {
        DefaultListModel<ListItem> model = new DefaultListModel<>();
        if (role == Role.LIBRARIAN) {
            model.addElement(checkoutItem);
            model.addElement(checkBookCopyItem);
        } else if (role == Role.ADMIN) {
            model.addElement(addMemberItem);
            model.addElement(addBookItem);
            model.addElement(addBookCopyItem);
        } else {
            model.addElement(checkoutItem);
            model.addElement(checkBookCopyItem);
            model.addElement(addMemberItem);
            model.addElement(addBookItem);
            model.addElement(addBookCopyItem);
        }
        model.addElement(logoutItem);
        linkList = new JList<>(model);
        // selected first item in list
        int begin = 0;
        int end = 0;
        linkList.setSelectionInterval(begin, end);
        linkList.setCellRenderer(new DefaultListCellRenderer() {

            @SuppressWarnings("rawtypes")
            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);
                if (value instanceof ListItem) {
                    ListItem nextItem = (ListItem) value;
                    setText(nextItem.getItemName());
                    if (isSelected) {
                        setForeground(Color.WHITE);
                        setBackground(Util.DARK_BLUE);
                    }
                }
                return c;
            }
        });
    }

    public void createMainPanels() {

        // Checkout Book Panel
        CheckoutWindow checkoutWindow = new CheckoutWindow();
        JPanel checkoutPanel = checkoutWindow.getMainPanel();

        // Add Library Member Window
        AddLibraryMemberWindow addLibraryMemberWindow = new AddLibraryMemberWindow();
        JPanel addLibraryMemberPanel = addLibraryMemberWindow.getMainPanel();

        // Add Book Copy Window
        AddBookCopyWindow addBookCopyWindow = new AddBookCopyWindow();
        JPanel addBookCopyPanel = addBookCopyWindow.getMainPanel();

        // Add Book Window
        AddBookWindow addBookWindow = new AddBookWindow();
        JPanel addBookPanel = addBookWindow.getMainPanel();

        // Add Book Copy Window
        DueDateBookCopyWindow dueDateBookCopyWindow = new DueDateBookCopyWindow();
        JPanel dueDateBookCopyPanel = dueDateBookCopyWindow.getMainPanel();

        cards.setLayout(new CardLayout());
        cards.add(checkoutPanel, checkoutItem.getItemName());
        cards.add(addBookCopyPanel, addBookCopyItem.getItemName());
        cards.add(addLibraryMemberPanel, addMemberItem.getItemName());
        cards.add(addBookPanel, addBookItem.getItemName());
        cards.add(dueDateBookCopyPanel, checkBookCopyItem.getItemName());
    }

    public void addComponents() {
        Util.adjustLabelFont(statusBar, Util.DARK_BLUE, true);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        createLinkLabels();
        createMainPanels();
        CardLayout cl = (CardLayout) (cards.getLayout());
        linkList.addListSelectionListener(event -> {
            String value = linkList.getSelectedValue().getItemName();
            if (value == Strings.LOG_OUT.toString()) {
                handleLogout();
            } else {
                boolean allowed = linkList.getSelectedValue().getHighlight();
                cl.show(cards, value);
            }
        });

        if (innerPane == null && outerPane == null) {
            innerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, linkList, cards);
            innerPane.setDividerLocation(180);
            outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane, statusBar);
            outerPane.setDividerLocation(500);
            this.add(outerPane, BorderLayout.CENTER);
        } else {
            innerPane.setLeftComponent(linkList);
            innerPane.setDividerLocation(180);
            outerPane.setRightComponent(statusBar);
            outerPane.setDividerLocation(500);
        }
    }

    private void handleLogout() {
        int opt = JOptionPane.showConfirmDialog(mainPanel, Strings.LOG_OUT_MESS, Strings.LOG_OUT_TITLE, JOptionPane.YES_NO_OPTION);
        if (opt == 0) {
            removeComponents();
            innerPane.setLeftComponent(null);
            outerPane.setRightComponent(null);
            setSize(400, 200);
            setLocationRelativeTo(null);
            setVisible(true);
            LoginWindow loginWindow = new LoginWindow(this);
            cards.add(loginWindow.getMainPane(), BorderLayout.CENTER);
        }
    }

    public void removeComponents() {
        cards.removeAll();
        cards.invalidate();
        cards.repaint();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void authenUser(String username, char[] password) {
        if (username.length() > 0 && password.length > 0) {
            try {
                this.role = systemController.login(username, password);
                statusBar.setText(String.format(Strings.WELCOME, username));
                removeComponents();
                addComponents();
            } catch (LoginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
}
