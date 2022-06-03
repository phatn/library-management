package edu.miu.mpp.library.service;

import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class BookService extends AbstractService {
    public Map<String, Book> findAllBooks() {
        return dataAccess.readBooksMap();
    }

    public void saveBooks(Map<String, Book> books) {
        dataAccess.saveBooksMap(books);
    }

    public LibraryMember checkout(String libraryMemberID, String isbn) throws BookCheckoutException {
        Map<String, LibraryMember> libraryMemberMap = dataAccess.readMemberMap();
        LibraryMember libraryMember = Optional.ofNullable(libraryMemberMap.get(libraryMemberID))
                .orElseThrow(() -> new BookCheckoutException("No library members found with " + libraryMemberID));

        Map<String, Book> bookMap = dataAccess.readBooksMap();
        Book book = Optional.ofNullable(bookMap.get(isbn))
                .orElseThrow(() -> new BookCheckoutException("No books found with isbn " + isbn));
        BookCopy availableBookCopy = Arrays.stream(book.getCopies())
                .filter(bookCopy -> bookCopy.isAvailable())
                .findFirst()
                .orElseThrow(() -> new BookCheckoutException("No book copies available of " + isbn));

        // Make this book copy unavailable
        availableBookCopy.changeAvailability();
        CheckoutRecordEntry checkoutRecordEntry = new CheckoutRecordEntry(
                isbn,
                String.valueOf(availableBookCopy.getCopyNum()),
                LocalDate.now(),
                LocalDate.now().plusDays(book.getMaxCheckoutLength()));

        libraryMember.getCheckoutRecord().addCheckoutRecordEntry(checkoutRecordEntry);

        // Persist books and their book copies
        dataAccess.saveBooksMap(bookMap);

        // Persist checkout record entry to its library member
        dataAccess.saveNewMember(libraryMember);

        return dataAccess.readMemberMap().get(libraryMemberID);
    }
}
