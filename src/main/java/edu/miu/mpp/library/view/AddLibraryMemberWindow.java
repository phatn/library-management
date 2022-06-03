package edu.miu.mpp.library.view;

import edu.miu.mpp.library.controller.SystemController;
import edu.miu.mpp.library.dao.DataAccess;
import edu.miu.mpp.library.dao.DataAccessFacade;
import edu.miu.mpp.library.model.Address;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.util.Strings;
import edu.miu.mpp.library.util.Util;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.miu.mpp.library.view.LibAppWindow.statusBar;

public class AddLibraryMemberWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel AddLibraryMemberWindow;
    private JButton addMemberButton;
    private JButton deleteMemberButton;
    private JTextField street;
    private JTextField telephoneNumber;
    private JTextField zip;
    private JTextField lastName;
    private JTextField firstName;
    private JTextField city;
    private JTextField state;
    private JTextField memberId;
    private JPanel middlePanel;
    private JPanel lowerPanel;
    private JTable memberTable;
    private JScrollPane memberScroll;
    private CustomTableModel model;

    //table data and config
    private final String[] DEFAULT_COLUMN_HEADERS
            = {"Member Id", "First Name", "Last Name", "Street", "City", "State", "Zip", "Telephone number"};
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    private static final int TABLE_WIDTH = (int) (0.75 * SCREEN_WIDTH);
    private static final int DEFAULT_TABLE_HEIGHT = (int) (0.75 * SCREEN_HEIGHT);
    private final float COL_WIDTH_PROPORTIONS = 0.35f;
    private final Color errorColor = Util.ERROR_MESSAGE_COLOR;
    private final Color infoColor = Util.INFO_MESSAGE_COLOR;
    private SystemController systemController;

    public JPanel getMainPanel() {
        return AddLibraryMemberWindow;
    }

    public AddLibraryMemberWindow() {

        AddLibraryMemberWindow = new JPanel();
        AddLibraryMemberWindow.setLayout(new BorderLayout());
        systemController = new SystemController();
        memberScroll = new JScrollPane();

        updateTable();
        initPanel();

        //add or update new meber
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryMember libraryMember = new LibraryMember(memberId.getText().strip(), firstName.getText().strip()
                        , lastName.getText().strip(), telephoneNumber.getText().strip()
                        , new Address(street.getText().strip(), city.getText().strip(), state.getText().strip(), zip.getText().strip()));
                Boolean isValidate = validateMember(libraryMember);
                if (isValidate) {
                    setValues(model, libraryMember);
                    memberTable.updateUI();
                    clear();
                }
            }
        });

        //delete member
        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableModel tblModel = memberTable.getModel();
                String getMemberId = tblModel.getValueAt(memberTable.getSelectedRow(), 0).toString().strip();
                int opt = JOptionPane.showConfirmDialog(AddLibraryMemberWindow, String.format(Strings.DELETE_MESS, getMemberId), Strings.DELETE_TITLE, JOptionPane.YES_NO_OPTION);
                if (opt == 0) {
                    if (validateMemberId(getMemberId)) {
                        systemController.deleteLibraryMember(getMemberId);
                        clear();
                        model.setTableValues(parseMemberToArray());
                        memberTable.updateUI();
                        printNotify(Strings.SUCCESS_DELETE_MEMBER, getMemberId, infoColor);
                    }
                }
            }
        });

        //click row in table and fill box
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                getRowData();
            }
        });
    }

    private void getRowData() {
        TableModel tblModel = memberTable.getModel();
        String getMemberId = tblModel.getValueAt(memberTable.getSelectedRow(), 0).toString();
        String getFirstName = tblModel.getValueAt(memberTable.getSelectedRow(), 1).toString();
        String getLastName = tblModel.getValueAt(memberTable.getSelectedRow(), 2).toString();
        String getStreet = tblModel.getValueAt(memberTable.getSelectedRow(), 3).toString();
        String getCity = tblModel.getValueAt(memberTable.getSelectedRow(), 4).toString();
        String getState = tblModel.getValueAt(memberTable.getSelectedRow(), 5).toString();
        String getZip = tblModel.getValueAt(memberTable.getSelectedRow(), 6).toString();
        String getTelephoneNumber = tblModel.getValueAt(memberTable.getSelectedRow(), 7).toString();
        memberId.setText(getMemberId);
        street.setText(getStreet);
        telephoneNumber.setText(getTelephoneNumber);
        zip.setText(getZip);
        lastName.setText(getLastName);
        firstName.setText(getFirstName);
        city.setText(getCity);
        state.setText(getState);
    }

    private Boolean validateMember(LibraryMember libraryMember) {
        if (!validateMemberId(libraryMember.getMemberId())) {
            return false;
        } else if (!validateFirstName(libraryMember.getFirstName())) {
            return false;
        } else if (!validateLastName(libraryMember.getLastName())) {
            return false;
        } else if (!validateStreet(libraryMember.getAddress().getStreet())) {
            return false;
        } else if (!validateState(libraryMember.getAddress().getState())) {
            return false;
        } else if (!validateCity(libraryMember.getAddress().getCity())) {
            return false;
        } else if (!validateZip(libraryMember.getAddress().getZip())) {
            return false;
        } else if (!validatePhoneNumber(libraryMember.getTelephone())) {
            return false;
        }
        return true;
    }

    private boolean validateMemberId(String memberId) {
        if (!blankOrEmpty(memberId, Strings.MEMBER_ID) && isNumber(memberId, Strings.MEMBER_ID)) {
            return true;
        }
        return false;
    }

    private boolean validateFirstName(String firstName) {
        if (!blankOrEmpty(firstName, Strings.FIRST_NAME) && isWord(firstName, Strings.FIRST_NAME)) {
            return true;
        }
        return false;
    }

    private boolean validateLastName(String lastName) {
        if (!blankOrEmpty(lastName, Strings.LASTNAME) && isWord(lastName, Strings.LASTNAME)) {
            return true;
        }
        return false;
    }

    private boolean validateStreet(String street) {
        if (!blankOrEmpty(street, Strings.STREET)) {
            return true;
        }
        return false;
    }

    private boolean validateState(String state) {
        if (!blankOrEmpty(state, Strings.STATE) && isWord(state, Strings.STATE)) {
            return true;
        }
        return false;
    }

    private boolean validateCity(String city) {
        if (!blankOrEmpty(city, Strings.CITY) && isWord(city, Strings.CITY)) {
            return true;
        }
        return false;
    }

    private boolean validateZip(String zip) {
        if (!blankOrEmpty(zip, Strings.ZIP) && isNumber(zip, Strings.ZIP)) {
            return true;
        }
        return false;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (!blankOrEmpty(phoneNumber, Strings.PHONE_NUMBER)) {
            return true;
        }
        return false;
    }

    private boolean blankOrEmpty(String check, String print) {
        if (check.length() == 0 || check == null) {
            printNotify(Strings.FAIL_ADD_MEMBER_BLANK, print, errorColor);
            return true;
        }
        return false;
    }

    private boolean isNumber(String check, String print) {
        for (int i = 0; i < check.length(); ++i) {
            if (!Character.isDigit(check.charAt(i))) {
                printNotify(Strings.FAIL_ADD_MEMBER_MUST_NUMBER_ONLY, print, errorColor);
                return false;
            }
        }
        return true;
    }

    private boolean isWord(String check, String print) {
        for (int i = 0; i < check.length(); ++i) {
            if (!Character.isAlphabetic(check.charAt(i)) && check.charAt(i) != ' ') {
                printNotify(Strings.FAIL_ADD_MEMBER_MUST_WORD_ONLY, print, errorColor);
                return false;
            }
        }
        return true;
    }

    public void clear() {
        street.setText("");
        telephoneNumber.setText("");
        zip.setText("");
        lastName.setText("");
        firstName.setText("");
        city.setText("");
        state.setText("");
        memberId.setText("");
    }

    private void createCustomColumns(JTable table, int width, float proportions,
                                     String[] headers) {
        table.setAutoCreateColumnsFromModel(false);
        int num = headers.length;
        for (int i = 0; i < num; ++i) {
            TableColumn column = new TableColumn(i);
            column.setHeaderValue(headers[i]);
            column.setMinWidth(Math.round(proportions * width));
            table.addColumn(column);
        }
    }

    public List<String[]> parseMemberToArray() {
        Map<String, LibraryMember> data = systemController.getAllLibraryMembers();
        List<String[]> listResouse = new ArrayList<>();
        if (data != null) {
            for (String key : data.keySet()) {
                LibraryMember keyData = data.get(key);
                String[] input = {keyData.getMemberId(), keyData.getFirstName(), keyData.getLastName(),
                        keyData.getAddress().getStreet(), keyData.getAddress().getCity(), keyData.getAddress().getState(),
                        keyData.getAddress().getZip()
                        , keyData.getTelephone()};
                listResouse.add(input);
            }
        }
        return listResouse;
    }

    public void updateTable() {
        model = new CustomTableModel();
        model.setTableValues(parseMemberToArray());
        memberTable = new JTable(model);
        memberTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        createCustomColumns(memberTable, TABLE_WIDTH,
                COL_WIDTH_PROPORTIONS, DEFAULT_COLUMN_HEADERS);
        memberScroll.setPreferredSize(
                new Dimension(TABLE_WIDTH, DEFAULT_TABLE_HEIGHT));

        memberTable.setShowGrid(true);
        memberTable.setGridColor(Color.GRAY);
        memberTable.setShowGrid(true);
        memberTable.setGridColor(Color.GRAY);

        memberScroll.getViewport().add(memberTable);
    }

    public void initPanel() {
        //set panel
        AddLibraryMemberWindow.add(middlePanel, BorderLayout.NORTH);
        AddLibraryMemberWindow.add(memberScroll, BorderLayout.CENTER);
        AddLibraryMemberWindow.add(lowerPanel, BorderLayout.SOUTH);
        getContentPane().add(AddLibraryMemberWindow);
        AddLibraryMemberWindow.setVisible(true);
        pack();
    }

    private void setValues(CustomTableModel model, LibraryMember libraryMember) {
        systemController.addNewLibraryMember(libraryMember);
        model.setTableValues(parseMemberToArray());
        printNotify(Strings.SUCCESS_ADD_MEMBER, libraryMember.getMemberId(), errorColor);
    }

    public void printNotify(String title, String memberId, Color color) {
        statusBar.setText(String.format(title, memberId));
        statusBar.setForeground(color);
    }

}
