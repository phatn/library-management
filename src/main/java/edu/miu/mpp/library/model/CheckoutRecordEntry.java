package edu.miu.mpp.library.model;

import java.io.Serializable;
import java.time.LocalDate;

public class CheckoutRecordEntry implements Serializable {

    private String isbn;

    private String bookCopyId;

    private LocalDate checkoutDate;

    private LocalDate dueDate;

    public CheckoutRecordEntry(String isbn, String bookCopyId, LocalDate checkoutDate, LocalDate dueDate) {
        this.isbn = isbn;
        this.bookCopyId = bookCopyId;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(String bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

}
