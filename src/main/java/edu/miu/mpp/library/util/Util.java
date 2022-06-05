package edu.miu.mpp.library.util;

import edu.miu.mpp.library.model.CheckoutRecordEntry;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.model.User;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComponent;


public class Util {

    public static final String[] DEFAULT_COLUMN_HEADERS
            = {"Member ID", "ISBN", "Book Copy ID", "Checkout Date", "Due Date"};
    public static final Color DARK_BLUE = Color.BLUE.darker();
    public static final Color ERROR_MESSAGE_COLOR = Color.RED.darker(); //dark red
    public static final Color INFO_MESSAGE_COLOR = new Color(24, 98, 19); //dark green
    public static final Color LINK_AVAILABLE = Color.BLACK;
    public static final Color LINK_NOT_AVAILABLE = Color.gray;
    //rgb(18, 75, 14)

    public static Font makeSmallFont(Font f) {
        return new Font(f.getName(), f.getStyle(), (f.getSize()-2));
    }

    public static void adjustLabelFont(JComponent label, Color color, boolean bigger) {
        if(bigger) {
            Font f = new Font(label.getFont().getName(),
                    label.getFont().getStyle(), (label.getFont().getSize()+2));
            label.setFont(f);
        } else {
            Font f = new Font(label.getFont().getName(),
                    label.getFont().getStyle(), (label.getFont().getSize()-2));
            label.setFont(f);
        }
        label.setForeground(color);

    }
    /** Sorts a list of numeric strings in natural number order */
    public static List<String> numericSort(List<String> list) {
        Collections.sort(list, new NumericSortComparator());
        return list;
    }

    static class NumericSortComparator implements Comparator<String>{
        @Override
        public int compare(String s, String t) {
            if(!isNumeric(s) || !isNumeric(t))
                throw new IllegalArgumentException("Input list has non-numeric characters");
            int sInt = Integer.parseInt(s);
            int tInt = Integer.parseInt(t);
            if(sInt < tInt) return -1;
            else if(sInt == tInt) return 0;
            else return 1;
        }
    }

    public static boolean isNumeric(String s) {
        if(s==null) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static User findUser(List<User> list, User user) {
        for(User u : list) {
            if(u.equals(user)) return u;
        }
        return null;
    }

    public static List<String[]> parseCheckoutRecordEntryRows(LibraryMember libraryMember) {
        List<CheckoutRecordEntry> checkoutRecordEntries = libraryMember.getCheckoutRecord().getCheckoutRecordEntries();
        List<String[]> rows = new ArrayList<>();
        checkoutRecordEntries.forEach(checkoutRecordEntry -> {
            String[] row = new String[DEFAULT_COLUMN_HEADERS.length];
            row[0] = libraryMember.getMemberId();
            row[1] = checkoutRecordEntry.getIsbn();
            row[2] = checkoutRecordEntry.getBookCopyId();
            row[3] = checkoutRecordEntry.getCheckoutDate().toString();
            row[4] = checkoutRecordEntry.getDueDate().toString();
            rows.add(row);
        });

        return rows;
    }

    public static boolean isValidIsbn(String isbn) {
        String regex = "^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(isbn).matches();
    }
}
