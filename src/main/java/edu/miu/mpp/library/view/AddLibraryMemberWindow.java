package edu.miu.mpp.library.view;

import edu.miu.mpp.library.dao.DataAccess;
import edu.miu.mpp.library.dao.DataAccessFacade;
import edu.miu.mpp.library.model.Address;
import edu.miu.mpp.library.model.LibraryMember;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public JPanel getMainPanel() {
        return AddLibraryMemberWindow;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.AddLibraryMemberWindow = mainPanel;
    }

    public AddLibraryMemberWindow() {
        AddLibraryMemberWindow = new JPanel();
        AddLibraryMemberWindow.setLayout(new BorderLayout());

        //set table
        updateModel();
        memberTable = new JTable(model);
        createCustomColumns(memberTable, TABLE_WIDTH,
                COL_WIDTH_PROPORTIONS, DEFAULT_COLUMN_HEADERS);
        memberScroll = new JScrollPane();
        memberScroll.setPreferredSize(
                new Dimension(TABLE_WIDTH, DEFAULT_TABLE_HEIGHT));
        memberScroll.getViewport().add(memberTable);

        //set panel
        AddLibraryMemberWindow.add(middlePanel, BorderLayout.NORTH);
        AddLibraryMemberWindow.add(memberScroll, BorderLayout.CENTER);
        AddLibraryMemberWindow.add(lowerPanel, BorderLayout.SOUTH);

        getContentPane().add(AddLibraryMemberWindow);
        AddLibraryMemberWindow.setVisible(true);
        pack();

        //set action listner
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(model);
                memberTable.updateUI();
                clear();
            }
        });

        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataAccess da = new DataAccessFacade();
                TableModel tblModel = memberTable.getModel();
                String getMemberId = tblModel.getValueAt(memberTable.getSelectedRow(), 0).toString();
                da.deleteMember(getMemberId);
                clear();
                model.setTableValues(parseMemberToArray());
                memberTable.updateUI();
            }
        });

        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
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
        });
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
        DataAccess da = new DataAccessFacade();
        Map<String, LibraryMember> data = da.readMemberMap();
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

    public void updateModel() {
        model = new CustomTableModel();
        model.setTableValues(parseMemberToArray());
    }

    private void setValues(CustomTableModel model) {
        LibraryMember libraryMember = new LibraryMember(memberId.getText().strip(), firstName.getText().strip()
                , lastName.getText().strip(), telephoneNumber.getText().strip()
                , new Address(street.getText().strip(), city.getText().strip(), state.getText().strip(), zip.getText().strip()));
        DataAccess da = new DataAccessFacade();
        da.saveNewMember(libraryMember);
        model.setTableValues(parseMemberToArray());
    }


}
