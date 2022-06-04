package edu.miu.mpp.library.view;

import edu.miu.mpp.library.util.Strings;
import edu.miu.mpp.library.util.Util;

import static edu.miu.mpp.library.view.LibAppWindow.currentUser;
import static edu.miu.mpp.library.view.LibAppWindow.statusBar;
public interface MessageableWindow {

    public default void displayError(String msg) {
        statusBar.setForeground(Util.ERROR_MESSAGE_COLOR);
        statusBar.setText(msg);
    }

    public default void displayInfo(String msg) {
        statusBar.setForeground(Util.INFO_MESSAGE_COLOR);
        statusBar.setText(msg);
    }

    public default void setWelcomeUser() {
        statusBar.setText(String.format(Strings.WELCOME, currentUser));
        statusBar.setForeground(Util.DARK_BLUE);
    }
}
