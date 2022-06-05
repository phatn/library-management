package edu.miu.mpp.library.controller;

import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.Author;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.LibraryMember;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller is to accept all requests from the UI
 */
public interface FrontController {
    Role login(String username, char[] password) throws LoginException;

    Map<String, Book> getAllBooks();

    void saveBooks(Map<String, Book> books);

    Map<String, LibraryMember> getAllLibraryMembers();

    void addNewLibraryMember(LibraryMember libraryMember);

    void deleteLibraryMember(String memberId);

    LibraryMember checkoutBook(String libraryMemberID, String isbn) throws BookCheckoutException;

    List<Author> getAuthors();

    void addBook(String isbn, String title, int maxCheckoutLength, List<Author> authors);
}
