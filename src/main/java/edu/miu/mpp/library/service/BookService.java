package edu.miu.mpp.library.service;

import edu.miu.mpp.library.exception.BookAddException;
import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.exception.BookCopyAddException;
import edu.miu.mpp.library.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static edu.miu.mpp.library.util.Util.isValidIsbn;

public class BookService extends AbstractService {
    private Map<String, Book> bookMap;
    public Book addBook(String isbn, String title, int maxCheckoutLength, List<Author> authors) throws BookAddException {
        // Validate parameters
        if (!isValidIsbn(isbn)) {
            throw new BookAddException("Invalid ISBN number. Valid ISBN number must have 10 or 13 number digits.");
        }
        for (Book book : bookMap.values()) {
            if (book.getIsbn().equals(isbn)) {
                throw new BookAddException("A book with the ISBN existed.");
            }
        }
        if (title == null || title.isEmpty()) {
            throw new BookAddException("No title specified.");
        }
        if (maxCheckoutLength < 1) {
            throw new BookAddException("Invalid max checkout length. It must be > 0.");
        }
        if (authors.size() < 1) {
            throw new BookAddException("No author.");
        }

        // Create new book and save it
        Book book = new Book(isbn, title, maxCheckoutLength, authors);
        bookMap.put(isbn, book);
        saveBooks(bookMap);

        return book;
    }

    public Book addBookCopy(String isbn) {
        // Validate parameters
        if (!isValidIsbn(isbn)) {
            throw new BookCopyAddException("Invalid ISBN number. Valid ISBN number must have 10 or 13 number digits.");
        }
        Book book = bookMap.get(isbn);
        if (book == null) {
            throw new BookCopyAddException("Book does not exist.");
        }

        // Add copy
        book.addCopy();
        saveBooks(bookMap);

        return book;
    }

    public Map<String, Book> findAllBooks() {
        bookMap = dataAccess.readBooksMap();
        return bookMap;
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
                .filter(BookCopy::isAvailable)
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
