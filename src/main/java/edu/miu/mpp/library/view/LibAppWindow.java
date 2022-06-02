package edu.miu.mpp.library.view;

import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.ListItem;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.service.LoginService;
import edu.miu.mpp.library.service.ServiceFactory;
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

    JPanel cards;
    JList<ListItem> linkList;

    ListItem checkoutItem = new ListItem(Strings.CHECK_OUT.toString(), true);
    ListItem addMemberItem = new ListItem(Strings.ADD_MEMBER.toString(), false);
    ListItem addBookCopyItem = new ListItem(Strings.ADD_BOOK_COPY.toString(), false);

    ListItem logoutItem = new ListItem(Strings.LOG_OUT.toString(), true);

    ListItem[] sellerItems = {checkoutItem, addMemberItem};
    ListItem[] memberItems = {checkoutItem, addBookCopyItem};

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

    public static JTextArea statusBar = new JTextArea(Strings.WELCOME.toString());

    private LoginService loginService = (LoginService) ServiceFactory.getServiceInstance(LoginService.class);

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
        model.addElement(checkoutItem);
        model.addElement(addBookCopyItem);
        model.addElement(addMemberItem);
        model.addElement(logoutItem);

        linkList = new JList<>(model);
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
                    if (nextItem.getHighlight()) {
                        setForeground(Util.LINK_AVAILABLE);
                    } else {
                        setForeground(Util.LINK_NOT_AVAILABLE);
                    }
                    if (isSelected) {
                        setForeground(Util.DARK_BLUE);
                        setBackground(new Color(236, 243, 245));
                        setBackground(Color.WHITE);
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

        cards.setLayout(new CardLayout());
        cards.add(checkoutPanel, checkoutItem.getItemName());
        cards.add(addBookCopyPanel, addBookCopyItem.getItemName());
        cards.add(addLibraryMemberPanel, addMemberItem.getItemName());

    }

    public void addComponents() {
        cards.removeAll();
        cards.invalidate();
        cards.repaint();
        Util.adjustLabelFont(statusBar, Util.DARK_BLUE, true);
        setSize(800, 450);
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

        JSplitPane innerPane
                = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, linkList, cards);
        innerPane.setDividerLocation(180);
        JSplitPane outerPane
                = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane, statusBar);
        outerPane.setDividerLocation(350);
        add(outerPane, BorderLayout.CENTER);
    }

    private void handleLogout() {
        int opt = JOptionPane.showConfirmDialog(mainPanel, Strings.LOG_OUT_MESS.toString(), Strings.LOG_OUT_TITLE.toString(), JOptionPane.YES_NO_OPTION);
        if (opt == 0) {
            cards.removeAll();
            cards.invalidate();
            cards.repaint();
        }
    }

    public void removeComponents() {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void authenUser(String username, char[] password) {
        if (username.length() == 0) {
            JOptionPane.showMessageDialog(this, "Username field must be non empty");
        } else if (password.length == 0) {
            JOptionPane.showMessageDialog(this, "Password field must be non empty");
        } else {
            try {
                Role role = loginService.login(username, password);
                addComponents();
            } catch (LoginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
}
