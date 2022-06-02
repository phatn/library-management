package edu.miu.mpp.library.util;

public enum Strings {
    CHECK_OUT("Checkout"),
    ADD_MEMBER("Add member"),
    ADD_BOOK_COPY("Add book copy"),
    LOG_OUT("Log out"),
    WELCOME("Welcome to the MPP Library!"),
    LOG_OUT_MESS("Do you want to log out?"),
    LOG_OUT_TITLE("LOG OUT"),
    ;

    private final String text;

    /**
     * @param text
     */
    Strings(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
